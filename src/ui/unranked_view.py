import flet as ft
import subprocess
from styles.theme import label_style, input_style, code_style, code_out
from ui.components import code_block, labeled_field, start_button, centrar
from collectors.parseCode import parse_CodeU

class UnrankedView:
    def __init__(self, page: ft.Page):
        self.page = page

        self.comboBoxUModee = ft.Dropdown(
                options=[
                    ft.dropdown.Option(key="",text="SIMPLE"),
                    ft.dropdown.Option(key="-v",text="VERBOSE"),
                    ft.dropdown.Option(key="-d",text="PROGRESS"),
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
            label_position="LEFT",
            value=True
            )
        self.field_code1  = ft.TextField(**code_style)
        self.field_code2  = ft.TextField(**code_style)
        self.field_output = ft.TextField(**code_out)

    

    def on_start(self, e):
        code1 = self.field_code1.value
        code2 = self.field_code2.value
        mode  = self.comboBoxUModee.value
        iterate = self.check_iterate.value

        problem = f"({parse_CodeU(code1)} =^= {parse_CodeU(code2)})"

        try:
            if iterate:
                cmd = ["java", "algoritmos/urau-src/src/at/jku/risc/stout/urau/AntiUnifyMain.java"]

            if iterate:
                cmd.append("-a")
            if mode:
                cmd.append(mode)

            cmd.append(problem)
            result = subprocess.run(cmd, capture_output=True, text=True)
            self.field_output.value = result.stdout or result.stderr
        except Exception as ex:
            self.field_output.value = f"Error: {ex}"

        print(cmd)
        self.field_output.update()

    def reset_code1(self, e=None):
        self.field_code1.value = ""
        self.field_code1.update()

    def reset_code2(self, e=None):
        self.field_code2.value = ""
        self.field_code2.update()

    async def open_picker_code1(self, e):
        files = await ft.FilePicker().pick_files(
            allowed_extensions=["py", "txt"]
        )
        if files:
            with open(files[0].path, "r") as f:
                self.field_code1.value = f.read()
            self.field_code1.update()

    async def open_picker_code2(self, e):
        files = await ft.FilePicker().pick_files(
            allowed_extensions=["py", "txt"]
        )
        if files:
            with open(files[0].path, "r") as f:
                self.field_code2.value = f.read()
            self.field_code2.update()

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

        #! botones

        def b_icon(icon, tooltip, on_click):
            return ft.IconButton(
                icon = icon,
                icon_size=16,
                icon_color=ft.Colors.BLUE_GREY_400,
                tooltip=tooltip,
                on_click=on_click,
                style=ft.ButtonStyle(
                    padding=ft.padding.all(4),
                    overlay_color=ft.Colors.TRANSPARENT,
                ),
            )

        bt1 = [
            b_icon(ft.Icons.UPLOAD_FILE, "Load file", self.open_picker_code1),
            b_icon(ft.Icons.CLOSE, "Clear", self.reset_code1)
        ]

        bt2 = [
            b_icon(ft.Icons.UPLOAD_FILE, "Load file",  self.open_picker_code2),
            b_icon(ft.Icons.CLOSE, "Clear", self.reset_code2),
        ]


        parte3 = ft.Row(
            controls=[
                code_block("Code 1", self.field_code1, botones=bt1),
                code_block("Code 2", self.field_code2, botones=bt2),
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
