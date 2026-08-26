# Keep line numbers for crash deobfuscation via mapping.txt
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Enum TransactionType: restore backup memakai TransactionType.valueOf() dari nama
# yang dibaca runtime (JSON), jadi nilai enum tidak boleh di-obfuscate/strip.
-keepclassmembers enum id.kaskelas.kas.domain.model.TransactionType {
    *;
}

# Enum KategoriMasuk & KategoriKeluar: filter chip & transaction form memakai
# .label (nama) yang harus stabil untuk UI state & backup restore.
-keepclassmembers enum id.kaskelas.kas.domain.model.KategoriMasuk {
    *;
}
-keepclassmembers enum id.kaskelas.kas.domain.model.KategoriKeluar {
    *;
}
