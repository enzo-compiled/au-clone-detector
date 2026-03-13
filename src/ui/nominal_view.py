import flet as ft
import subprocess
from styles.theme import label_style, input_style, code_style, code_out
from ui.components import labeled_field, start_button, centrar, code_block
from collectors.parseCode import parse_CodeN

class NominalView:
    def __init__(self, page: ft.Page):
        self.notaNominal = ft.Text("Note: This algorithm only accepts codes with similar arity, be careful!", theme_style=ft.TextThemeStyle.LABEL_SMALL)
        self.page = page
        self.comboBoxNmodee = ft.Dropdown(
            width=250,
            #label="Mode",
            options=[
                ft.dropdown.Option(key="SIMPLE", text="SIMPLE"),
                ft.dropdown.Option(key="VERBOSE", text="VERBOSE"),
                ft.dropdown.Option(key="PROGRESS", text="PROGRESS"),
                ft.dropdown.Option(key="ALL", text="ALL")
            ],
            border_color=ft.Colors.BLUE_GREY_600,
            focused_border_color=ft.Colors.BLUE_600,
            bgcolor=ft.Colors.with_opacity(0.75, ft.Colors.WHITE),
            border_radius=6,
            content_padding=ft.padding.symmetric(horizontal=20, vertical=10),
            expand=True,
            hint_text="Select the mode"
        )
        self.field_lines = ft.TextField(hint_text="-1 = No limit", **input_style)
        self.field_comm = ft.TextField(hint_text="defect=Add, Mult", **input_style)
        self.field_assoc = ft.TextField(hint_text="defect=Add, Mult", **input_style)
        self.field_code1 = ft.TextField(**code_style)
        self.field_code2 = ft.TextField(**code_style)
        self.field_output = ft.TextField(**code_out)

    def on_start(self, e):
        code1 = self.field_code1.value
        code2 = self.field_code2.value
        mode = self.comboBoxNmodee.value or "SIMPLE"
        lines = self.field_lines.value or "-1"
        comm = self.field_comm.value or ""
        assoc = self.field_assoc.value or ""
        #en estas últimas dos, por defecto son Add y Mult

        problem = f"{str(parse_CodeN(code1))} =^= {str(parse_CodeN(code2))}"

        #si assoc ó comm están vacíos
        if not assoc:
            cmd = ["java", "-jar", "algoritmos/eqnauac-lib.jar",
                "AU", mode, problem, "", "Add, Mult, ", "Add, Mult, " + comm, "true", "false", lines]
        elif not comm:
            cmd = ["java", "-jar", "algoritmos/eqnauac-lib.jar",
                "AU", mode, problem, "", "Add, Mult, " + assoc, "Add, Mult", "true", "false", lines]

        try:
            result = subprocess.run(cmd,capture_output=True,text=True)
            self.field_output.value = result.stdout or result.stderr
        except Exception as ex:
            self.field_output.value = f"Error: {ex}"

        print(parse_CodeN(code1))
        print(parse_CodeN(code2))
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
                labeled_field("Output Mode", self.comboBoxNmodee),
                labeled_field("Output Lines Limit", self.field_lines),
            ],
            spacing=20,
            width=700,
        )

        parte2 = ft.Row(
            controls=[
                labeled_field("Commutative Symbols", self.field_comm),
                labeled_field("Associative Symbols",  self.field_assoc),
            ],
            spacing=20,
            width=700,
        )

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
                self.notaNominal,
                centrar(parte1),
                centrar(parte2),
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





