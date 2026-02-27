import flet as ft
import webbrowser, subprocess, ast, os  

rutaNom = ("algoritmos/eqnauac-lib.jar","algoritmos/urau-src/src/at/jku/risc/stout/urau/AntiUnifyMain.java")
var1 = "java"
arg_1 = "AU"
arg_2 = ["SIMPLE", "VERBOSE", "PROGRESS", "ALL", "SILENT"]

def main(page: ft.Page):
    page.fonts={
        "Poppins": "fonts/Poppins-Regular.ttf",
    }
    page.theme = ft.Theme(font_family="Poppins")
    page.title = "AU Clone Code Detector"
    page.theme_mode = ft.ThemeMode.LIGHT
    page.bgcolor = ft.Colors.BLUE_GREY_300
    page.scroll = None #ft.ScrollMode.ADAPTIVE

    fontBody = ft.TextStyle(size=22, font_family="Poppins")

    info = ft.Text("This is a software clone detector application using anti-unification algorithms, which are:", size=22)
    infoAlg1 = ft.Text("Anti-unification for Nominal Terms", size=19)
    infoAlg2 = ft.Text("Anti-Unification for Unranked Terms and Hedges", size=19)
    textos_inputs = ("Select an algorithm:", "Output Mode:", "Output lines limit:", 
                    "Conmutative Symbols:", "Associative Symbols:", "Code 1:", "Code 2:")

    dot = ft.CircleAvatar(radius=2, bgcolor=ft.Colors.BLACK)
    primer = ft.Row(controls=[dot, infoAlg1], alignment=ft.MainAxisAlignment.START)
    seg = ft.Row(controls=[dot, infoAlg2], alignment=ft.MainAxisAlignment.START)

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
    def open_link2(e):
        webbrowser.open("https://risc.jku.at/m/alexander-baumgartner/")

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
            sobre_pag,
        ],
        spacing=5,
    )

    comboBoxNmodee = ft.Dropdown(
        width=250,
        label="Mode",
        #hint_text="Select a Mode",
        options=[
            ft.dropdown.Option(key="simple", text=arg_2[0]),
            ft.dropdown.Option(key="verbose", text=arg_2[1]),
            ft.dropdown.Option(key="progress", text=arg_2[2]),
            ft.dropdown.Option(key="all", text=arg_2[3])
        ]
    )

    notaNominal = ft.Text("Note: This algorithm only accepts codes with similar arity, be careful!", theme_style=ft.TextThemeStyle.LABEL_SMALL)

    #estilo labels
    label_style = ft.TextStyle(
        size=12,
        color=ft.Colors.BLUE_GREY_600,
        weight=ft.FontWeight.W_500,
        letter_spacing=1.2,
    )

    #TextField de parte1 y parte2
    input_style = {
        "text_size": 14,
        "border_color": ft.Colors.BLUE_GREY_300,
        "focused_border_color": ft.Colors.BLUE_600,
        "bgcolor": ft.Colors.with_opacity(0.3, ft.Colors.WHITE),
        "border_radius": 6,
        "content_padding": ft.Padding.symmetric(horizontal=12, vertical=10),
        "cursor_color": ft.Colors.BLUE_600,
        "width" : 700
    }

    def labeled_field(label, control):
        return ft.Column(
            controls=[
                ft.Text(label.upper(), style=label_style),
                control,
            ],
            spacing=4,
            expand=True,
        )

    parte1 = ft.Row(
        controls=[
            labeled_field("Output Mode", ft.Dropdown(
                options=[
                    ft.dropdown.Option(key="simple",text=arg_2[0]),
                    ft.dropdown.Option(key="verbose",text=arg_2[1]),
                    ft.dropdown.Option(key="progress",text=arg_2[2]),
                    ft.dropdown.Option(key="all",text=arg_2[3]),
                ],
                border_color=ft.Colors.BLUE_GREY_600,
                focused_border_color=ft.Colors.BLUE_600,
                bgcolor=ft.Colors.with_opacity(0.75, ft.Colors.WHITE),
                border_radius=6,
                content_padding=ft.padding.symmetric(horizontal=20, vertical=10),
                expand=True,
                hint_text="Select the mode"
            )),
            labeled_field("Output Lines Limit", ft.TextField(
                hint_text="-1 = No limit",
                **input_style,
            )),
        ],
        spacing=20,
        #expand=False,
        width=700
    )

    parte2 = ft.Row(
        controls=[
            labeled_field("Commutative Symbols", ft.TextField(
                hint_text="Add, Mult...",
                **input_style,
            )),
            labeled_field("Associative Symbols", ft.TextField(
                hint_text="Add, Mult...",
                **input_style,
            )),
        ],
        spacing=20,
        #expand=False,
        width=700        
    )

    def centrar(control):
        return ft.Row(
            controls=[
                ft.Container(
                    content=control,
                    padding=ft.padding.symmetric(horizontal=30),
                )
            ],
            alignment=ft.MainAxisAlignment.CENTER, 
        )

    textocode = (ft.Text(textos_inputs[5], font_family="Poppins",theme_style=ft.TextThemeStyle.LABEL_MEDIUM), ft.Text(textos_inputs[6], font_family="Poppins",theme_style=ft.TextThemeStyle.LABEL_MEDIUM))
    textcode1=ft.Container(content=textocode[0])
    textcode2=ft.Container(content=textocode[1])
    

    code_style = {
        "multiline": True,
        "min_lines": 20,
        "max_lines": None,
        "expand": True,
        "text_size": 13,
        "content_padding": ft.padding.all(12),
        "cursor_color": ft.Colors.BLACK,
        "bgcolor": ft.Colors.with_opacity(0.45, ft.Colors.WHITE),
        "border_color": ft.Colors.BLUE_GREY_400,
        "focused_border_color": ft.Colors.BLUE_GREY_900,
        "border_radius": 8,
        "hint_text": "Paste your code...",
        "hint_style": ft.TextStyle(color=ft.Colors.BLACK, size=12),
    }

    code_out = {
        "multiline": True,
        "min_lines": 20,
        "max_lines": None,
        "expand": True,
        "text_size": 13,
        "content_padding": ft.padding.all(12),
        "cursor_color": ft.Colors.BLACK,
        "bgcolor": ft.Colors.with_opacity(0.45, ft.Colors.WHITE),
        "border_color": ft.Colors.BLUE_GREY_400,
        "focused_border_color": ft.Colors.BLUE_GREY_900,
        "border_radius": 8,
        "hint_style": ft.TextStyle(color=ft.Colors.BLACK, size=12),
        "read_only": True,
        #"selectable": True
    }

    code1_block = ft.Column(
        controls=[
            ft.Container(
                content=ft.Text(
                    "Code 1",
                    size=13,
                    weight=ft.FontWeight.BOLD,
                    font_family="Poppins",
                    color=ft.Colors.BLUE_GREY_700,
                ),
                bgcolor=ft.Colors.with_opacity(0.25, ft.Colors.WHITE),
                border_radius=ft.border_radius.only(top_left=8, top_right=8),
                padding=ft.padding.symmetric(horizontal=12, vertical=6),
            ),
            ft.TextField(**code_style), #** desmpaquetado
        ],
        spacing=0,
        expand=True,
    )

    code2_block = ft.Column(
        controls=[
            ft.Container(
                content=ft.Text(
                    "Code 2",
                    size=13,
                    weight=ft.FontWeight.BOLD,
                    font_family="Poppins",
                    color=ft.Colors.BLUE_GREY_700,
                ),
                bgcolor=ft.Colors.with_opacity(0.25, ft.Colors.WHITE),
                border_radius=ft.border_radius.only(top_left=8, top_right=8),
                padding=ft.padding.symmetric(horizontal=12, vertical=6),
            ),
            ft.TextField(**code_style),
        ],
        spacing=0,
        expand=True,
    )

    parte3 = ft.Row(
        controls=[code1_block, code2_block],
        spacing=20,
        expand=True,
        alignment=ft.MainAxisAlignment.CENTER,
        vertical_alignment=ft.CrossAxisAlignment.START,
    )

    boton = ft.Row(
        controls=[
            ft.Button(
                content="START",
                style=ft.ButtonStyle(
                    color=ft.Colors.BLUE_GREY_700,
                    bgcolor=ft.Colors.with_opacity(0.25, ft.Colors.WHITE),
                    overlay_color=ft.Colors.with_opacity(0.1, ft.Colors.BLUE_600),
                    shadow_color=ft.Colors.TRANSPARENT,
                    side=ft.BorderSide(width=1, color=ft.Colors.BLUE_GREY_400),
                    shape=ft.RoundedRectangleBorder(radius=6),
                    padding=ft.padding.symmetric(horizontal=40, vertical=14),
                    text_style=ft.TextStyle(
                        size=13,
                        weight=ft.FontWeight.W_600,
                        letter_spacing=2,
                    ),
                ),
            )
        ],
        alignment=ft.MainAxisAlignment.CENTER,
        )
    
    parte_output = ft.Column(
        controls=[
            ft.Text("Output", font_family="Poppins",theme_style=ft.TextThemeStyle.LABEL_MEDIUM, size=22),
            ft.TextField(**code_out)
        ]
    )

    nominal_view = ft.Column(
        controls=[
            notaNominal,
            centrar(parte1),
            centrar(parte2),
            parte3,
            boton,
            parte_output
        ],
        scroll=ft.ScrollMode.ADAPTIVE,
        expand=True
    )

    check_iterate = ft.Checkbox(
        label="ITERATE ALL POSSIBILITIES",
        label_style=ft.TextStyle(
            size=12,
            color=ft.Colors.BLUE_GREY_600,
            weight=ft.FontWeight.W_500,
            letter_spacing=1.2,
        ),
        fill_color={
            ft.ControlState.SELECTED: ft.Colors.BLUE_600,
            ft.ControlState.DEFAULT: ft.Colors.TRANSPARENT,
        },
        border_side={
            ft.ControlState.DEFAULT: ft.BorderSide(width=1, color=ft.Colors.BLUE_GREY_400),
            ft.ControlState.SELECTED: ft.BorderSide(width=1, color=ft.Colors.BLUE_600),
        },
        check_color=ft.Colors.WHITE,
        splash_radius=0,
        label_position="LEFT"
    )

    parte1_uk = ft.Row(
        controls=[
            labeled_field("Output Mode", ft.Dropdown(
                options=[
                    ft.dropdown.Option(key="simple",text=arg_2[0]),
                    ft.dropdown.Option(key="verbose",text=arg_2[1]),
                    ft.dropdown.Option(key="progress",text=arg_2[2]),
                ],
                border_color=ft.Colors.BLUE_GREY_600,
                focused_border_color=ft.Colors.BLUE_600,
                bgcolor=ft.Colors.with_opacity(0.75, ft.Colors.WHITE),
                border_radius=6,
                content_padding=ft.padding.symmetric(horizontal=20, vertical=10),
                expand=True,
                hint_text="Select the mode",
                width=300
            )),
            check_iterate,
        ],
        spacing=20,
        #expand=False,
        width=700
    )

    code1_block_uk = ft.Column(
        controls=[
            ft.Container(
                content=ft.Text(
                    "Code 1",
                    size=13,
                    weight=ft.FontWeight.BOLD,
                    font_family="Poppins",
                    color=ft.Colors.BLUE_GREY_700,
                ),
                bgcolor=ft.Colors.with_opacity(0.25, ft.Colors.WHITE),
                border_radius=ft.border_radius.only(top_left=8, top_right=8),
                padding=ft.padding.symmetric(horizontal=12, vertical=6),
            ),
            ft.TextField(**code_style), #** desmpaquetado
        ],
        spacing=0,
        expand=True,
    )

    code2_block_uk = ft.Column(
        controls=[
            ft.Container(
                content=ft.Text(
                    "Code 2",
                    size=13,
                    weight=ft.FontWeight.BOLD,
                    font_family="Poppins",
                    color=ft.Colors.BLUE_GREY_700,
                ),
                bgcolor=ft.Colors.with_opacity(0.25, ft.Colors.WHITE),
                border_radius=ft.border_radius.only(top_left=8, top_right=8),
                padding=ft.padding.symmetric(horizontal=12, vertical=6),
            ),
            ft.TextField(**code_style),
        ],
        spacing=0,
        expand=True,
    )

    parte3_uk = ft.Row(
        controls=[code1_block_uk, code2_block_uk],
        spacing=20,
        expand=True,
        alignment=ft.MainAxisAlignment.CENTER,
        vertical_alignment=ft.CrossAxisAlignment.START,
    )

    boton_uk = ft.Row(
        controls=[
            ft.Button(
                content="START",
                style=ft.ButtonStyle(
                    color=ft.Colors.BLUE_GREY_700,
                    bgcolor=ft.Colors.with_opacity(0.25, ft.Colors.WHITE),
                    overlay_color=ft.Colors.with_opacity(0.1, ft.Colors.BLUE_600),
                    shadow_color=ft.Colors.TRANSPARENT,
                    side=ft.BorderSide(width=1, color=ft.Colors.BLUE_GREY_400),
                    shape=ft.RoundedRectangleBorder(radius=6),
                    padding=ft.padding.symmetric(horizontal=40, vertical=14),
                    text_style=ft.TextStyle(
                        size=13,
                        weight=ft.FontWeight.W_600,
                        letter_spacing=2,
                    ),
                ),
            )
        ],
        alignment=ft.MainAxisAlignment.CENTER,
        )
    
    parte_output_uk = ft.Column(
        controls=[
            ft.Text("Output", font_family="Poppins",theme_style=ft.TextThemeStyle.LABEL_MEDIUM, size=22),
            ft.TextField(**code_out)
        ],
    )

    unranked_view = ft.Column(
        controls=[
            ft.Text("jejeje"),
            centrar(parte1_uk),
            parte3_uk,
            boton_uk,
            parte_output_uk
        ],
        scroll=ft.ScrollMode.ADAPTIVE,
        expand=True
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
                            ft.Tab(label="Home", icon=ft.Icons.HOME),
                            ft.Tab(label="Nominal", icon=ft.Icons.COMPUTER),
                            ft.Tab(label="Unranked", icon=ft.Icons.COMPUTER_SHARP),
                        ]
                    ),
                    ft.TabBarView(
                        expand=True,
                        controls=[
                            ft.Container(content=homeinfo),
                            ft.Container(
                                content=nominal_view,
                            ),
                            ft.Container(
                                content=unranked_view,
                            ),
                        ],
                    ),
                ],
            ),
        )
    )

    def handle_link_highlight2(e):
        e.control.style = ft.TextStyle(
            #decoration=ft.TextDecoration.UNDERLINE,
            color=ft.Colors.BLUE_700,
            weight=ft.FontWeight.BOLD,
            size=11,
        )
        e.control.update()

    def handle_link_unhighlight2(e):
        e.control.style = ft.TextStyle(
            color=ft.Colors.BLUE_GREY_600,
            weight=ft.FontWeight.BOLD,
            size=11,
            #decoration=ft.TextDecoration.UNDERLINE,
        )
        e.control.update()

    creditos = sobre_pag = ft.Text(
        spans=[
            ft.TextSpan("Developed by Enzo V. Cornejo and guided by "),  
            ft.TextSpan(
                "Alexander Baumgartner",
                style=ft.TextStyle(
                size=11,
                color=ft.Colors.BLUE_GREY_600,
                weight=ft.FontWeight.BOLD,
                #decoration=ft.TextDecoration.UNDERLINE,
            ),
                on_click=open_link2,
                on_enter=handle_link_highlight2,
                on_exit=handle_link_unhighlight2,
            ),
        ],
        style=ft.TextStyle(
            size=11,
            color=ft.Colors.BLUE_GREY_300,
            letter_spacing=1.2,
            weight=ft.FontWeight.W_400,
        ),
    )
    page.add(ft.Container(
        content=ft.Row(
            controls=[creditos],
            alignment=ft.MainAxisAlignment.START,
        ),
        bgcolor=ft.Colors.BLUE_GREY_300,
        padding=ft.padding.symmetric(vertical=8),
        expand=False,
        height=35, 
        border=ft.border.only(top=ft.BorderSide(width=1, color=ft.Colors.BLUE_GREY_500)), 
    ))


if __name__ == "__main__":
    ft.run(main)