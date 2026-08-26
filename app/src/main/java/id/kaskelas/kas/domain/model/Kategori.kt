package id.kaskelas.kas.domain.model

/**
 * Kategori fixed bawaan (keputusan desain: tidak bisa dikelola user di MVP).
 * Dipisah per jenis supaya pilihan kategori selalu konsisten dengan jenis transaksi.
 */
enum class KategoriMasuk(val label: String) {
    IURAN("Iuran"),
    ACARA("Acara Kelas"),
    DONASI("Donasi"),
    LAINNYA_MASUK("Lainnya"),
}

enum class KategoriKeluar(val label: String) {
    SNACK("Snack"),
    PERLENGKAPAN("Perlengkapan"),
    KONSUMSI("Konsumsi"),
    LAINNYA_KELUAR("Lainnya"),
}
