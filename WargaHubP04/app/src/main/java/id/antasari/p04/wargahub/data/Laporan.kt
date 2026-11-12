package id.antasari.p04.wargahub.data

data class Laporan(
    val id: Long,
    val judul: String,
    val kategori: String,
    val deskripsi: String,
    val status: String
)
