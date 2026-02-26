import flet as ft

def main(page: ft.Page):
    # Un "dot" simple de color verde
    dot = ft.CircleAvatar(
        radius=2,
        bgcolor=ft.Colors.BLACK,
    )
    
    page.add(
        ft.Row([
            dot,
            ft.Text("Estado Activo")
        ])
    )

ft.run(main)