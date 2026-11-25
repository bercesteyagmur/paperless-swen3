from datetime import datetime

import flet as ft
import httpx

from config import API_BASE_URL


async def get_json(path: str):
    async with httpx.AsyncClient(timeout=10.0) as client:
        response = await client.get(f"{API_BASE_URL}{path}")
        response.raise_for_status()
        return response.json()


async def upload_pdf(filename: str, content: bytes) -> None:
    async with httpx.AsyncClient(timeout=30.0) as client:
        response = await client.post(
            f"{API_BASE_URL}/api/files",
            files={"file": (filename, content, "application/pdf")},
        )
        response.raise_for_status()


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
    upload_status = ft.Text()

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
        heading = ft.Text("Documents", size=24, weight=ft.FontWeight.BOLD)
        list_controls = [heading, upload_button, upload_status]
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

        if not documents:
            show_result(screen, *list_controls, ft.Text("No files uploaded yet."))
            return

        show_result(
            screen,
            *list_controls,
            *[
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
                for document in documents
            ],
        )

    async def show_details(event) -> None:
        back = ft.TextButton("Back to list", on_click=show_documents)
        screen = show_loading(back)
        try:
            document = await get_json(f"/api/files/{event.control.data}")
        except (httpx.HTTPError, ValueError):
            show_result(screen, back, ft.Text("Could not load document details."))
            return

        summary = (document.get("summary") or "").strip()
        show_result(
            screen,
            back,
            ft.Text(document["originalFileName"], size=24, weight=ft.FontWeight.BOLD),
            ft.Text(f"Uploaded at: {uploaded_at(document)}"),
            ft.Text(f"Tags: {tag_names(document)}"),
            ft.Text("Summary", size=18, weight=ft.FontWeight.BOLD),
            ft.Text(summary or "No summary available yet.", selectable=True),
        )

    async def on_pdf_selected(event: ft.FilePickerResultEvent) -> None:
        if not event.files or upload_button.disabled:
            return

        selected = event.files[0]
        if not selected.name.lower().endswith(".pdf") or not selected.bytes:
            upload_status.value = "Select a non-empty PDF."
            upload_status.color = ft.Colors.ERROR
            page.update()
            return

        screen = body.content
        upload_button.disabled = True
        upload_status.value = "Uploading PDF..."
        upload_status.color = None
        page.update()

        try:
            await upload_pdf(selected.name, selected.bytes)
        except httpx.HTTPError:
            upload_status.value = "Could not upload the PDF."
            upload_status.color = ft.Colors.ERROR
            upload_button.disabled = False
            page.update()
            return

        upload_status.value = "PDF uploaded successfully."
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
