import flet as ft
import subprocess
from styles.theme import label_style, input_style, code_style, code_out
from ui.components import code_block, labeled_field, start_button, centrar

class UnrankedView:
    def __init__(self, page: ft.Page):
        self.page = page

        self.comboBoxUModee = ft.Dropdown(
                options=[
                    ft.dropdown.Option(key="simple",text="SIMPLE"),
                    ft.dropdown.Option(key="verbose",text="VERBOSE"),
                    ft.dropdown.Option(key="progress",text="PROGRESS"),
                ],
                border_color=ft.Colors.BLUE_GREY_600,
                focused_border_color=ft.Colors.BLUE_600,
                bgcolor=ft.Colors.with_opacity(0.75, ft.Colors.WHITE),
                border_radius=6,
                content_padding=ft.padding.symmetric(horizontal=20, vertical=10),
                expand=True,
                hint_text="Select the mode",
                width=300
            )
        
        self.check_iterate = ft.Checkbox(
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
        self.field_code1  = ft.TextField(**code_style)
        self.field_code2  = ft.TextField(**code_style)
        self.field_output = ft.TextField(**code_out)

    

    def on_start(self, e):
        pass
        """code1 = self.field_code1.value
        code2 = self.field_code2.value
        mode  = self.dropdown_mode.value
        iterate = self.check_iterate.value

        try:
            args = ["java", "algoritmos/urau-src/...", code1, code2, mode]
            if iterate:
                args.append("--iterate")
            result = subprocess.run(args, capture_output=True, text=True)
            self.field_output.value = result.stdout or result.stderr
        except Exception as ex:
            self.field_output.value = f"Error: {ex}"

        self.field_output.update()"""

    def build(self):
        parte1 = ft.Row(
            controls=[
                labeled_field("Output Mode", self.comboBoxUModee),
                ft.Column(
                    controls=[
                        ft.Text("OPTIONS", style=label_style),
                        self.check_iterate,
                    ],
                    spacing=4,
                    expand=True,
                ),
            ],
            spacing=20,
            width=700,
        )

        parte3 = ft.Row(
            controls=[
                code_block("Code 1", self.field_code1),
                code_block("Code 2", self.field_code2),
            ],
            spacing=20,
            expand=True,
            vertical_alignment=ft.CrossAxisAlignment.START,
        )

        boton = start_button(self.on_start)

        return ft.Column(
            controls=[
                centrar(parte1),
                parte3,
                boton,
                ft.Column(controls=[
                    ft.Text("Output", font_family="Poppins",
                            theme_style=ft.TextThemeStyle.LABEL_MEDIUM, size=22),
                    self.field_output,
                ]),
            ],
            scroll=ft.ScrollMode.ADAPTIVE,
            expand=True,
        )
