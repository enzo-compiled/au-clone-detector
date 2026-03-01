import flet as ft
import webbrowser

def build(page: ft.Page):
    fontBody = ft.TextStyle(size=22, font_family="Poppins")

    def handle_link_highlight(e):
        e.control.style = ft.TextStyle(
            #decoration=ft.TextDecoration.UNDERLINE,
            color=ft.Colors.BLUE_700,
            weight=ft.FontWeight.BOLD,
            size=22,
        )
        e.control.update()

    def handle_link_unhighlight(e):
        e.control.style = ft.TextStyle(
            #decoration=ft.TextDecoration.UNDERLINE,
            color=ft.Colors.BLUE_900,
            weight=ft.FontWeight.BOLD,
            size=22,
        )
        e.control.update()

    def open_link(e):
        webbrowser.open("https://www3.risc.jku.at/projects/stout/library.html")

    dot = ft.CircleAvatar(radius=2, bgcolor=ft.Colors.BLACK)
    info = ft.Text("This is a software clone detector application using anti-unification algorithms, which are:", size=22)
    infoAlg1 = ft.Text("Anti-unification for Nominal Terms", size=19)
    infoAlg2 = ft.Text("Anti-Unification for Unranked Terms and Hedges", size=19)
    primer = ft.Row(controls=[dot, infoAlg1], alignment=ft.MainAxisAlignment.START)
    seg = ft.Row(controls=[dot, infoAlg2], alignment=ft.MainAxisAlignment.START)
    sobre_pag = ft.Text(
            spans=[
                ft.TextSpan("You can check out the web ", style=fontBody),  
                ft.TextSpan(
                    "library of the algorithms",
                    style=ft.TextStyle(
                        #decoration=ft.TextDecoration.UNDERLINE,
                        color=ft.Colors.BLUE_900,
                        weight=ft.FontWeight.BOLD,
                        size=22,
                    ),
                    on_click=open_link,
                    on_enter=handle_link_highlight,
                    on_exit=handle_link_unhighlight,
                ),
                ft.TextSpan(" where are differents one!", style=fontBody),  
            ],
            size=22,
    )
    return ft.Column(
        controls=[
            ft.Text(
                "Python Clone Detector",
                size=40,
                weight=ft.FontWeight.W_900,
            ),
            info,
            primer,
            seg,
            sobre_pag,
        ],
        spacing=5,
    )