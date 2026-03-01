import flet as ft
import webbrowser
from ui.home_view import build
from ui.nominal_view import NominalView
from ui.unranked_view import UnrankedView

def main(page: ft.Page):
    page.fonts = {"Poppins": "fonts/Poppins-Regular.ttf"}
    page.theme = ft.Theme(font_family="Poppins")
    page.title = "AU Clone Code Detector"
    page.theme_mode = ft.ThemeMode.LIGHT
    page.bgcolor = ft.Colors.BLUE_GREY_300
    page.scroll = None

    def open_link2(e):
        webbrowser.open("https://risc.jku.at/m/alexander-baumgartner/")

    def handle_link_highlight2(e):
        e.control.style = ft.TextStyle(color=ft.Colors.BLUE_700, weight=ft.FontWeight.BOLD, size=11)
        e.control.update()

    def handle_link_unhighlight2(e):
        e.control.style = ft.TextStyle(color=ft.Colors.BLUE_GREY_600, weight=ft.FontWeight.BOLD, size=11)
        e.control.update()

    creditos = ft.Text(
        spans=[
            ft.TextSpan("Developed by Enzo V. Cornejo and guided by "),
            ft.TextSpan(
                "Alexander Baumgartner",
                style=ft.TextStyle(size=11, color=ft.Colors.BLUE_GREY_600, weight=ft.FontWeight.BOLD),
                on_click=open_link2,
                on_enter=handle_link_highlight2,
                on_exit=handle_link_unhighlight2,
            ),
        ],
        style=ft.TextStyle(size=11, color=ft.Colors.BLUE_GREY_300, letter_spacing=1.2),
    )

    page.add(
        ft.Tabs(
            length=3,
            selected_index=0,
            expand=True,
            animation_duration=1,
            content=ft.Column(
                expand=True,
                controls=[
                    ft.TabBar(tabs=[
                        ft.Tab(label="Home", icon=ft.Icons.HOME),
                        ft.Tab(label="Nominal", icon=ft.Icons.COMPUTER),
                        ft.Tab(label="Unranked", icon=ft.Icons.COMPUTER_SHARP),
                    ]),
                    ft.TabBarView(
                        expand=True,
                        controls=[
                            ft.Container(content=build(page)),
                            ft.Container(content=NominalView(page).build()),
                            ft.Container(content=UnrankedView(page).build()),
                        ],
                    ),
                ],
            ),
        )
    )

    page.add(
        ft.Container(
            content=ft.Row(controls=[creditos], alignment=ft.MainAxisAlignment.START),
            bgcolor=ft.Colors.BLUE_GREY_300,
            padding=ft.padding.symmetric(horizontal=12, vertical=8),
            height=35,
            border=ft.border.only(top=ft.BorderSide(width=1, color=ft.Colors.BLUE_GREY_500)),
        )
    )

if __name__ == "__main__":
    ft.run(main)