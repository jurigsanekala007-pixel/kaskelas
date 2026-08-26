# Keep line numbers for crash deobfuscation via mapping.txt
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Enum TransactionType: restore backup memakai TransactionType.valueOf() dari nama
# yang dibaca runtime (JSON), jadi nilai enum tidak boleh di-obfuscate/strip.
-keepclassmembers enum id.kaskelas.kas.domain.model.TransactionType {
    *;
}
