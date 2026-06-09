package com.ejemplo.misfinanzas.modelo

enum class Categoria(
    val emoji: String,
    val etiqueta: String,
    val colorFondoHex: String
) {
    SALARIO("💰", "Salario", "#E8F5E9"),
    FREELANCE("💻", "Freelance", "#E0F2F1"),
    COMIDA("🛒", "Comida", "#FFF3E0"),
    TRANSPORTE("🚇", "Transporte", "#E3F2FD"),
    SERVICIOS("💡", "Servicios", "#F3E5F5"),
    ENTRETENIMIENTO("🎬", "Entretenimiento", "#FCE4EC"),
    SALUD("🏥", "Salud", "#FFEBEE"),
    EDUCACION("📚", "Educación", "#E8EAF6"),
    OTROS("📦", "Otros", "#F5F5F5");

    fun esIngreso(): Boolean = this == SALARIO || this == FREELANCE
}
