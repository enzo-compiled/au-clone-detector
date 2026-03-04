import flet as ft

font_body = ft.TextStyle(size=22, font_family="Poppins")

#estilo labels
label_style = ft.TextStyle(
    size=12,
    color=ft.Colors.BLUE_GREY_600,
    weight=ft.FontWeight.W_500,
    letter_spacing=1.2,
)

#TextField
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

#bloques codes
code_style = {
    "multiline": True,
    "min_lines": 20,
    "max_lines": None,
    "expand": True,
    "text_size": 18,
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
    "text_size": 18,
    "content_padding": ft.padding.all(12),
    "cursor_color": ft.Colors.BLACK,
    "bgcolor": ft.Colors.with_opacity(0.45, ft.Colors.WHITE),
    "border_color": ft.Colors.BLUE_GREY_400,
    "focused_border_color": ft.Colors.BLUE_GREY_900,
    "border_radius": 8,
    "hint_style": ft.TextStyle(color=ft.Colors.BLACK, size=12),
    "read_only": True,
}