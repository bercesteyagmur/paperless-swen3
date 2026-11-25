from datetime import datetime

import flet as ft
import httpx

from config import API_BASE_URL


async def get_json(path: str):
    async with httpx.AsyncClient(timeout=10.0) as client:
        response = await client.get(f"{API_BASE_URL}{path}")
        response.raise_for_status()
        return response.json()


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
        screen = show_loading(heading)
        try:
            documents = await get_json("/api/files")
        except (httpx.HTTPError, ValueError):
            show_result(
                screen,
                heading,
                ft.Text("Could not load documents."),
                ft.TextButton("Try again", on_click=show_documents),
            )
            return

        if not documents:
            show_result(screen, heading, ft.Text("No files uploaded yet."))
            return

        show_result(
            screen,
            heading,
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

    await show_documents()


if __name__ == "__main__":
    ft.run(main)
