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
                ft.dropdown.Option(key="simple", text="SIMPLE"),
                ft.dropdown.Option(key="verbose", text="VERBOSE"),
                ft.dropdown.Option(key="progress", text="PROGRESS"),
                ft.dropdown.Option(key="all", text="ALL")
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
        self.field_comm = ft.TextField(hint_text="Add, Mult...", **input_style)
        self.field_assoc = ft.TextField(hint_text="Add, Mult...", **input_style)
        self.field_code1 = ft.TextField(**code_style)
        self.field_code2 = ft.TextField(**code_style)
        self.field_output = ft.TextField(**code_out)

    def on_start(self, e):
        code1 = self.field_code1.value
        code2 = self.field_code2.value
        mode = self.comboBoxNmodee.value
        lines = self.field_lines.value or "-1"
        comm = self.field_comm.value or ""
        assoc = self.field_assoc.value or ""

        problem = f"{str(parse_CodeN(code1))} =^= {str(parse_CodeN(code2))}"

        #si assoc y comm están vacíos
        if not assoc and not comm:
            cmd = ["java", "-jar", "algoritmos/eqnauac-lib.jar",
                "AU", mode.upper(), problema, "", "", "true", "false", lines]
        else:
            cmd = ["java", "-jar", "algoritmos/eqnauac-lib.jar",
                "AU", mode.upper(), problem, "", assoc, comm, "", "true", "false", lines]

        try:
            result = subprocess.run(cmd,capture_output=True,text=True)
            self.field_output.value = result.stdout or result.stderr
            #self.field_output.value = "esto es una prueba de como se ve"
        except Exception as ex:
            self.field_output.value = f"Error: {ex}"

        print(parse_CodeN(code1))
        print(parse_CodeN(code2))
        self.field_output.update()

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





