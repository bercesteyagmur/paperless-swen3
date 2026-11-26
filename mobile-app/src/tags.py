import flet as ft
import httpx

from api import add_tag, remove_tag


def tag_editor(page: ft.Page, document: dict) -> ft.Column:
    document_id = document["id"]
    tags = document.get("tags") or []
    tag_list = ft.Row(wrap=True)
    name = ft.TextField(label="Tag name")
    status = ft.Text()

    def render_tags() -> None:
        tag_list.controls = [
            ft.TextButton(
                f"Remove {tag['name']}",
                icon=ft.Icons.CLOSE,
                data=tag["id"],
                on_click=remove,
            )
            for tag in tags
        ] or [ft.Text("No tags")]

    def set_busy(busy: bool) -> None:
        add_button.disabled = tag_list.disabled = busy
        page.update()

    async def add(_event) -> None:
        nonlocal tags
        if add_button.disabled:
            return
        tag_name = (name.value or "").strip()
        if not tag_name:
            status.value = "Enter a tag name."
            page.update()
            return

        status.value = ""
        set_busy(True)
        try:
            updated_document = await add_tag(document_id, tag_name)
            tags = updated_document.get("tags") or []
            name.value = ""
            render_tags()
            status.value = "Tag added."
        except (httpx.HTTPError, ValueError):
            status.value = "Could not add the tag."
        finally:
            set_busy(False)

    async def remove(event) -> None:
        nonlocal tags
        if add_button.disabled:
            return
        tag_id = event.control.data
        status.value = ""
        set_busy(True)
        try:
            await remove_tag(document_id, tag_id)
            tags = [tag for tag in tags if tag["id"] != tag_id]
            render_tags()
            status.value = "Tag removed."
        except httpx.HTTPError:
            status.value = "Could not remove the tag."
        finally:
            set_busy(False)

    add_button = ft.Button("Add tag", on_click=add)
    render_tags()
    return ft.Column(
        controls=[ft.Text("Tags", size=18, weight=ft.FontWeight.BOLD), tag_list, name, add_button, status],
    )
