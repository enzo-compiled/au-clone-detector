import flet as ft
from styles.theme import label_style

def labeled_field(label, control):
    return ft.Column(
        controls=[
            ft.Text(label.upper(), style=label_style),
            control,
        ],
        spacing=4,
        expand=True,
    )

def centrar(control):
    return ft.Row(
        controls=[ft.Container(content=control, padding=ft.padding.symmetric(horizontal=30))],
        alignment=ft.MainAxisAlignment.CENTER,
    )

def code_block(label, field):
    return ft.Column(
        controls=[
            ft.Container(
                content=ft.Text(label, size=13, weight=ft.FontWeight.BOLD,
                                font_family="Poppins", color=ft.Colors.BLUE_GREY_700),
                bgcolor=ft.Colors.with_opacity(0.25, ft.Colors.WHITE),
                border_radius=ft.border_radius.only(top_left=8, top_right=8),
                padding=ft.padding.symmetric(horizontal=12, vertical=6),
            ),
            field,
        ],
        spacing=0,
        expand=True,
    )

def start_button(on_click):
    return ft.Row(
        controls=[
            ft.Button(
                content="START",
                #on_click=on_click,
                style=ft.ButtonStyle(
                    color=ft.Colors.BLUE_GREY_700,
                    bgcolor=ft.Colors.with_opacity(0.25, ft.Colors.WHITE),
                    overlay_color=ft.Colors.with_opacity(0.1, ft.Colors.BLUE_600),
                    shadow_color=ft.Colors.TRANSPARENT,
                    side=ft.BorderSide(width=1, color=ft.Colors.BLUE_GREY_400),
                    shape=ft.RoundedRectangleBorder(radius=6),
                    padding=ft.padding.symmetric(horizontal=40, vertical=14),
                    text_style=ft.TextStyle(size=13, weight=ft.FontWeight.W_600, letter_spacing=2),
                ),
            )
        ],
        alignment=ft.MainAxisAlignment.CENTER,
    )