import flet as ft

from config import API_BASE_URL


def main(page: ft.Page) -> None:
    page.title = "Paperless"
    page.appbar = ft.AppBar(title=ft.Text("Paperless"))
    page.add(
        ft.SafeArea(
            content=ft.Container(
                padding=16,
                content=ft.Column(
                    controls=[
                        ft.Text("Paperless server", weight=ft.FontWeight.BOLD),
                        ft.Text(API_BASE_URL, selectable=True),
                    ],
                ),
            ),
        ),
    )


if __name__ == "__main__":
    ft.run(main)
