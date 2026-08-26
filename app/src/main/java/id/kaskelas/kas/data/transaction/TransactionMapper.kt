package id.kaskelas.kas.data.transaction

import id.kaskelas.kas.domain.model.Transaction
import id.kaskelas.kas.domain.model.TransactionType

fun TransactionEntity.toDomain(): Transaction = Transaction(
    id = id,
    type = TransactionType.valueOf(type),
    amount = amount,
    category = category,
    date = date,
    note = note,
)

fun Transaction.toEntity(): TransactionEntity = TransactionEntity(
    id = id,
    type = type.name,
    amount = amount,
    category = category,
    date = date,
    note = note,
)
