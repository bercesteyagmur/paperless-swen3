from datetime import datetime

import flet as ft
import httpx

from api import change_document, get_json, search_documents, upload_pdf
from tags import tag_editor


def uploaded_at(document: dict) -> str:
    value = document.get("uploadedAt")
    if not value:
        return "Unknown"
    try:
        return datetime.fromisoformat(value).strftime("%d.%m.%Y %H:%M")
    except ValueError:
        return value


def tag_names(document: dict) -> str:
    return ", ".join(tag["name"] for tag in document.get("tags") or []) or "No tags"


async def main(page: ft.Page) -> None:
    page.title = "Paperless"
    page.appbar = ft.AppBar(title=ft.Text("Paperless"))
    page.scroll = ft.ScrollMode.AUTO
    body = ft.Container(padding=16)
    page.add(ft.SafeArea(content=body))
    list_status = ft.Text()
    selected_tag = "all"

    def show_loading(*controls: ft.Control) -> ft.Column:
        screen = ft.Column(controls=[*controls, ft.ProgressRing()])
        body.content = screen
        page.update()
        return screen

    def show_result(screen: ft.Column, *controls: ft.Control) -> None:
        if body.content is screen:
            screen.controls = list(controls)
            page.update()

    async def show_documents(_event=None) -> None:
        nonlocal selected_tag
        heading = ft.Text("Documents", size=24, weight=ft.FontWeight.BOLD)
        list_controls = [heading, upload_button, list_status]
        screen = show_loading(*list_controls)
        try:
            documents = await get_json("/api/files")
        except (httpx.HTTPError, ValueError):
            show_result(
                screen,
                *list_controls,
                ft.Text("Could not load documents."),
                ft.TextButton("Try again", on_click=show_documents),
            )
            return

        documents_by_id = {document["id"]: document for document in documents}
        matching_ids = None
        search_version = 0
        search_pending = False
        search_status = ft.Text()
        tag_filter = None

        def render_documents() -> None:
            if search_pending:
                show_result(screen, *list_controls)
                return

            candidates = documents
            if matching_ids is not None:
                candidates = [documents_by_id[document_id] for document_id in matching_ids if document_id in documents_by_id]

            visible = [
                document for document in candidates
                if selected_tag == "all" or any(
                    str(tag["id"]) == selected_tag for tag in document.get("tags") or []
                )
            ]
            cards = [
                ft.Card(
                    content=ft.Container(
                        padding=12,
                        content=ft.Column(
                            controls=[
                                ft.TextButton(
                                    document["originalFileName"],
                                    data=document["id"],
                                    on_click=show_details,
                                ),
                                ft.Text(f"Uploaded at: {uploaded_at(document)}"),
                                ft.Text(tag_names(document)),
                            ],
                        ),
                    ),
                )
                for document in visible
            ]
            if not cards:
                if matching_ids is not None:
                    message = "No matching documents found."
                elif not documents:
                    message = "No files uploaded yet."
                else:
                    message = "No documents with this tag."
                cards = [ft.Text(message)]
            show_result(screen, *list_controls, *cards)

        async def on_search_input(_event) -> None:
            nonlocal matching_ids, search_pending, search_version
            query = (search_field.value or "").strip()
            search_version += 1
            version = search_version
            if len(query) < 3:
                matching_ids = None
                search_pending = False
                search_status.value = ""
                render_documents()
                return

            search_pending = True
            search_status.value = "Searching..."
            search_status.color = None
            render_documents()
            try:
                results = await search_documents(query)
            except (httpx.HTTPError, ValueError):
                if version == search_version:
                    matching_ids = None
                    search_pending = False
                    search_status.value = "Could not search documents."
                    search_status.color = ft.Colors.ERROR
                    render_documents()
                return

            if version == search_version:
                matching_ids = [result["documentId"] for result in results]
                search_pending = False
                search_status.value = ""
                render_documents()

        async def clear_search(_event) -> None:
            nonlocal selected_tag
            search_field.value = ""
            selected_tag = "all"
            if tag_filter is not None:
                tag_filter.value = "all"
            await on_search_input(None)

        def filter_documents(event) -> None:
            nonlocal selected_tag
            selected_tag = event.control.value or "all"
            render_documents()

        search_field = ft.TextField(label="Search document content", on_change=on_search_input, expand=True)
        list_controls.extend(
            [ft.Row(controls=[search_field, ft.TextButton("Clear", on_click=clear_search)]), search_status],
        )

        try:
            tags = await get_json("/api/tags")
        except (httpx.HTTPError, ValueError):
            selected_tag = "all"
            list_controls.append(ft.Text("Could not load tags."))
        else:
            options = [ft.DropdownOption(key="all", text="All tags")]
            options.extend(ft.DropdownOption(key=str(tag["id"]), text=tag["name"]) for tag in tags)
            if selected_tag not in {option.key for option in options}:
                selected_tag = "all"
            tag_filter = ft.Dropdown(label="Filter by tag", value=selected_tag, options=options, on_select=filter_documents)
            list_controls.append(tag_filter)
        render_documents()

    async def show_details(event) -> None:
        document_id = event.control.data
        back = ft.TextButton("Back to list", on_click=show_documents)
        screen = show_loading(back)
        try:
            document = await get_json(f"/api/files/{document_id}")
        except (httpx.HTTPError, ValueError):
            show_result(screen, back, ft.Text("Could not load document details."))
            return

        filename = document["originalFileName"]
        extension = filename[-4:] if filename.lower().endswith(".pdf") else ".pdf"
        name = ft.TextField(
            label="New name",
            value=filename[:-4] if filename.lower().endswith(".pdf") else filename,
            suffix=ft.Text(extension),
        )
        status = ft.Text()

        async def save_change(method: str, action: str, payload: dict | None = None) -> None:
            if rename_button.disabled or body.content is not screen:
                return
            rename_button.disabled = delete_button.disabled = back.disabled = True
            status.value = ""
            page.update()

            try:
                await change_document(document_id, method, payload)
            except httpx.HTTPError as error:
                if method == "PATCH" and isinstance(error, httpx.HTTPStatusError) and error.response.status_code == 409:
                    status.value = "A document with that name already exists."
                else:
                    status.value = f"Could not {action} the document."
                status.color = ft.Colors.ERROR
                rename_button.disabled = delete_button.disabled = back.disabled = False
                page.update()
                return

            list_status.value = "Document renamed." if method == "PATCH" else "Document deleted."
            list_status.color = None
            await show_documents()

        async def rename_document(_event) -> None:
            new_name = (name.value or "").strip()
            if new_name.lower().endswith(".pdf"):
                new_name = new_name[:-4].rstrip()
            if not new_name:
                status.value = "Enter a file name."
                status.color = ft.Colors.ERROR
                page.update()
                return
            await save_change("PATCH", "rename", {"newFileName": new_name + extension})

        def confirm_delete(_event) -> None:
            async def delete_document(_event) -> None:
                page.pop_dialog()
                await save_change("DELETE", "delete")

            page.show_dialog(
                ft.AlertDialog(
                    modal=True,
                    title=ft.Text("Delete document?"),
                    content=ft.Text(filename),
                    actions=[
                        ft.TextButton("Cancel", on_click=lambda _event: page.pop_dialog()),
                        ft.TextButton("Delete", on_click=delete_document),
                    ],
                ),
            )

        rename_button = ft.Button("Rename", on_click=rename_document)
        delete_button = ft.TextButton("Delete", on_click=confirm_delete)
        summary = (document.get("summary") or "").strip()
        show_result(
            screen,
            back,
            ft.Text(filename, size=24, weight=ft.FontWeight.BOLD),
            ft.Text(f"Uploaded at: {uploaded_at(document)}"),
            tag_editor(page, document),
            name,
            ft.Row(controls=[rename_button, delete_button], wrap=True),
            status,
            ft.Text("Summary", size=18, weight=ft.FontWeight.BOLD),
            ft.Text(summary or "No summary available yet.", selectable=True),
        )

    async def on_pdf_selected(event: ft.FilePickerResultEvent) -> None:
        nonlocal selected_tag
        if not event.files or upload_button.disabled:
            return

        selected = event.files[0]
        if not selected.name.lower().endswith(".pdf") or not selected.bytes:
            list_status.value = "Select a non-empty PDF."
            list_status.color = ft.Colors.ERROR
            page.update()
            return

        screen = body.content
        upload_button.disabled = True
        list_status.value = "Uploading PDF..."
        list_status.color = None
        page.update()

        try:
            await upload_pdf(selected.name, selected.bytes)
        except httpx.HTTPError:
            list_status.value = "Could not upload the PDF."
            list_status.color = ft.Colors.ERROR
            upload_button.disabled = False
            page.update()
            return

        list_status.value = "PDF uploaded successfully."
        selected_tag = "all"
        upload_button.disabled = False
        if body.content is screen:
            await show_documents()
        else:
            page.update()

    picker = ft.FilePicker(on_result=on_pdf_selected)
    page.services.append(picker)
    upload_button = ft.Button(
        "Upload PDF",
        action=ft.PickFiles(
            picker,
            file_type=ft.FilePickerFileType.CUSTOM,
            allowed_extensions=["pdf"],
            with_data=True,
        ),
    )

    await show_documents()


if __name__ == "__main__":
    ft.run(main)
