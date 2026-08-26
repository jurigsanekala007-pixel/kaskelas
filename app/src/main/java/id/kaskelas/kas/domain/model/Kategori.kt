package id.kaskelas.kas.domain.model

/**
 * Default kategori bawaan — di-seed ke tabel categories via Room migration.
 * Enum ini dipakai sebagai referensi compile-time dan fallback jika DB kosong.
 * Kategori bisa dikelola user melalui menu Pengaturan.
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
