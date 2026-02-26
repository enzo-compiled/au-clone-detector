import flet as ft
import subprocess, ast, os

def main(page: ft.Page):
    page.title = "AU Clone Code Detector"
    page.theme_mode = ft.ThemeMode.LIGHT #cambiar luego a SYSTEM
    page.bgcolor = ft.Colors.BLUE_GREY_300
    page.padding = 30
    page.scroll = ft.ScrollMode.ADAPTIVE
    fontBody=ft.TextThemeStyle.LABEL_MEDIUM
    info = ft.Text("This is a software clone detector application using anti-unification algorithms, which are:", size=22,theme_style=fontBody)
    infoAlg1 = ft.Text("Anti-unification for nominal terms", size=19,theme_style=fontBody)
    infoAlg2 = ft.Text("Anti-Unification for Unranked Terms and Hedges", size=19,theme_style=fontBody)
    textos_inputs = ("Select an algorithm:", "Output Mode:", "Output lines limit:", 
                    "Conmutative Symbols:", "Associative Symbols:", "Code 1:", "Code 2:")
    
    dot = ft.CircleAvatar(radius=2,bgcolor=ft.Colors.BLACK)
    primer = ft.Row(controls=[dot, infoAlg1],alignment=ft.MainAxisAlignment.START)
    seg = ft.Row(controls=[dot, infoAlg2],alignment=ft.MainAxisAlignment.START)

    comboBoxAlg = ft.Dropdown(
        width=300,
        label="Algorithm",
        #hint_text="no sé que es estp",
        options=[
            ft.dropdown.Option(key="nominal", text="Nominal"),
            ft.dropdown.Option(key="unranked", text="Unranked Terms and Hedges")
        ],
    )
    #seleccion algor
    fila_col11 = ft.Column(
        controls=ft.Text(textos_inputs[0], size=19,theme_style=ft.TextThemeStyle.LABEL_SMALL)
    )
    fila_col21 = ft.Column(
        controls=comboBoxAlg
    )

    page.add(
        ft.Column(
        [
            ft.Text(
                "Python CodeCloneDetector",
                size=40,
                weight=ft.FontWeight.W_900,
            ),
            info,
            primer,
            seg
        ],
        spacing=2, 
        ),
        ft.Row(
            controls=[fila_col11,fila_col21],
            alignment=ft.MainAxisAlignment.CENTER,
            spacing=250
        ),
        if "nominal":
            pass
    )
if __name__ == "__main__":
    ft.run(main)
