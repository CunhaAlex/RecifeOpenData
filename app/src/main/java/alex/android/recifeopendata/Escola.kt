package alex.android.recifeopendata

data class Escola(
    var escola_codigo: Int = 0,
    var endereco: String = ""
) {
    override fun toString() = endereco
}