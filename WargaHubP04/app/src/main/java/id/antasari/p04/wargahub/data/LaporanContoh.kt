package id.antasari.p04.wargahub.data

object LaporanContoh {
    private val kategori = listOf(
        "Lampu jalan padam", "Sampah menumpuk", "Drainase tersumbat"
    )

    val semua: List<Laporan> = (1..15).map { nomor ->
        val jenis = kategori[(nomor - 1) % kategori.size]
        Laporan(
            id = nomor.toLong(),
            judul = "$jenis di zona latihan $nomor",
            kategori = jenis,
            deskripsi = "Contoh masalah di zona latihan $nomor. " +
                "Data fiktif ini dipakai untuk mempelajari daftar " +
                "dan navigasi rincian laporan.",
            status = "Simulasi"
        )
    }

    fun cari(id: Long): Laporan? = semua.find { it.id == id }
}
