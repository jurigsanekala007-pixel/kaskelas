package id.kaskelas.kas.core.util

import id.kaskelas.kas.domain.model.Transaction
import id.kaskelas.kas.domain.model.TransactionType
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

/**
 * Serializer backup JSON versi (PRD §8.6). Format:
 * {"schemaVersion":1,"exportedAt":"2026-08-26","transactions":[{...}]}
 *
 * Restore menolak schemaVersion yang lebih baru dari [CURRENT_VERSION].
 */
object BackupSerializer {

    const val CURRENT_VERSION = 1

    fun toJson(transactions: List<Transaction>): String {
        val root = JSONObject()
        root.put("schemaVersion", CURRENT_VERSION)
        root.put("exportedAt", LocalDate.now().toString())
        val array = JSONArray()
        transactions.forEach { t ->
            array.put(
                JSONObject()
                    .put("id", t.id)
                    .put("type", t.type.name)
                    .put("amount", t.amount)
                    .put("category", t.category)
                    .put("date", t.date.toString())
                    .put("note", t.note),
            )
        }
        root.put("transactions", array)
        return root.toString(2)
    }

    /** Throws [BackupFormatException] kalau struktur tidak valid / versi terlalu baru. */
    fun fromJson(json: String): List<Transaction> {
        val root = try {
            JSONObject(json)
        } catch (e: Exception) {
            throw BackupFormatException("File bukan backup Kas Kelas yang valid")
        }

        if (!root.has("schemaVersion") || !root.has("transactions")) {
            throw BackupFormatException("Struktur backup tidak dikenal")
        }
        val version = root.optInt("schemaVersion", -1)
        if (version > CURRENT_VERSION) {
            throw BackupFormatException(
                "Backup dibuat dengan versi aplikasi lebih baru ($version > $CURRENT_VERSION)",
            )
        }

        val array = root.optJSONArray("transactions")
            ?: throw BackupFormatException("Daftar transaksi tidak ditemukan")
        val result = mutableListOf<Transaction>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i) ?: continue
            val type = try {
                TransactionType.valueOf(obj.getString("type"))
            } catch (e: Exception) {
                throw BackupFormatException("Jenis transaksi tidak valid pada item ${i + 1}")
            }
            val date = try {
                LocalDate.parse(obj.getString("date"))
            } catch (e: Exception) {
                throw BackupFormatException("Tanggal tidak valid pada item ${i + 1}")
            }
            result.add(
                Transaction(
                    id = obj.optLong("id", 0L),
                    type = type,
                    amount = obj.getLong("amount"),
                    category = obj.optString("category", "Lainnya"),
                    date = date,
                    note = obj.optString("note", ""),
                ),
            )
        }
        return result
    }
}

class BackupFormatException(message: String) : Exception(message)
