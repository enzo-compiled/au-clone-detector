import flet as ft
import webbrowser

def main(page: ft.Page):
    page.fonts={
        "Poppins": "https://github.com/google/fonts/raw/main/ofl/poppins/Poppins-Regular.ttf",
        "CascadiaCode": "https://github.com/microsoft/cascadia-code/releases/download/v2111.01/CascadiaCode.ttf"
    }
    page.theme = ft.Theme(font_family="Poppins") #cambiar?
    page.title = "AU Clone Code Detector"
    page.theme_mode = ft.ThemeMode.LIGHT #cambiar luego a SYSTEM
    page.bgcolor = ft.Colors.BLUE_GREY_300
    #page.padding = 10
    page.scroll = ft.ScrollMode.ADAPTIVE
    fontBody=ft.TextThemeStyle.LABEL_MEDIUM
    fontBody1=ft.TextStyle(size=22,font_family="Poppins")
    info = ft.Text("This is a software clone detector application using anti-unification algorithms, which are:", size=22,theme_style=fontBody)
    infoAlg1 = ft.Text("Anti-unification for Nominal Terms", size=19,theme_style=fontBody)
    infoAlg2 = ft.Text("Anti-Unification for Unranked Terms and Hedges", size=19,theme_style=fontBody)
    textos_inputs = ("Select an algorithm:", "Output Mode:", "Output lines limit:", 
                    "Conmutative Symbols:", "Associative Symbols:", "Code 1:", "Code 2:")
    
    dot = ft.CircleAvatar(radius=2,bgcolor=ft.Colors.BLACK)
    primer = ft.Row(controls=[dot, infoAlg1],alignment=ft.MainAxisAlignment.START)
    seg = ft.Row(controls=[dot, infoAlg2],alignment=ft.MainAxisAlignment.START)

    def handle_link_highlight(e):
        e.control.style = ft.TextStyle(
            decoration=ft.TextDecoration.UNDERLINE,
            color=ft.Colors.BLUE_700,
            weight=ft.FontWeight.BOLD,
        )
        e.control.update()

    def handle_link_unhighlight(e):
        e.control.style = ft.TextStyle(
            decoration=ft.TextDecoration.UNDERLINE,
            color=ft.Colors.BLUE_900,
            weight=ft.FontWeight.BOLD,
        )
        e.control.update()
    
    def open_link(e):
        webbrowser.open("https://google.com")

    link1=ft.TextSpan(
        "library of the algorithms",
        style=ft.TextStyle(
            decoration=ft.TextDecoration.UNDERLINE,
            color=ft.Colors.BLUE_900,
            weight=ft.FontWeight.BOLD,
        ),
        on_click=open_link,
        on_enter=handle_link_highlight,
        on_exit=handle_link_unhighlight,
    )

    sobre_pag= ft.Text(
        spans=[
            ft.TextSpan("You can check out the web ", style=fontBody1), 
            link1,                                                
            ft.TextSpan(" where are differents one!", style=fontBody1), 
        ],
        size=22,
    )


    homeinfo = ft.Column(
        [
            ft.Text(
                "Python Clone Detector",
                size=40,
                weight=ft.FontWeight.W_900,
            ),
            info,
            primer,
            seg,
            ft.Text(
    spans=[
        ft.TextSpan("You can check out the web ", style=fontBody),
        ft.TextSpan(
            "library of the algorithms",
            style=ft.TextStyle(
                decoration=ft.TextDecoration.UNDERLINE,
                color=ft.Colors.BLUE_900,
                weight=ft.FontWeight.BOLD,
            ),
            url="https://google.com",
        ),
        ft.TextSpan(" where are differents ones!", style=fontBody),
    ],
)
        ],
        spacing=5, 
        )

    page.add(
        ft.Tabs(
            selected_index=0,
            length=3,
            expand=True,
            animation_duration=1,
            content=ft.Column(
                expand=True,
                controls=[
                    ft.TabBar(
                        tabs=[
                            ft.Tab(label="Home",icon=ft.Icons.HOME),
                            ft.Tab(label="Nominal",icon=ft.Icons.COMPUTER),
                            ft.Tab(label="Unranked", icon=ft.Icons.COMPUTER_SHARP),
                        ]
                    ),
                    ft.TabBarView(
                        expand=True,
                        controls=[
                            ft.Container(
                                content=homeinfo
                            ),
                                ft.Container(
                                    content=ft.Text("This is Tab 2"),
                                    alignment=ft.Alignment.CENTER,
                                ),
                                ft.Container(
                                    content=ft.Text("This is Tab 3"),
                                    alignment=ft.Alignment.CENTER,
                                ),
                        ],
                    ),
                ],
            ),
        )
    )


if __name__ == "__main__":
    ft.run(main)