package br.edu.utfpr.projeto_android_aplicado

data class Lancamento(
    val id: Long,
    val tipo: String, // 'C' para Crédito, 'D' para Débito
    val detalhe: String,
    val valor: Double,
    val dataLancamento: String
)
