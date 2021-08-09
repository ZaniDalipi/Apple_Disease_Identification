package com.zanoapp.applediseaseIdentification.utils

import com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB.Transaction

object TransactionMockData {
    val transactions = mutableListOf(
        Transaction(" ", "No data", 232, 1.40, "27/05/2020", "second transaction", "ds"),
        Transaction("Incomes", "Apples", 1000, 0.40, "27/05/2021", "first transaction", "edo"),
        Transaction(
            "Expenses",
            "Fertilizers",
            500,
            0.20,
            "27/05/2020",
            "second transaction",
            "ds"
        ),
        Transaction("Incomes", "Bannanas", 550, 1.0, "27/05/2021", "first transaction", "edo"),
        Transaction("Expenses", "Gas", 350, 0.70, "27/05/2020", "second transaction", "ds"),
        Transaction("Incomes", "Oranges", 2000, 0.20, "27/05/2021", "first transaction", "edo"),
        Transaction(
            "Expenses",
            "Tractor equipments",
            4503,
            1.20,
            "27/05/2020",
            "second transaction",
            "ds"
        ),
        Transaction("Incomes", "Apples", 252, 0.40, "27/05/2021", "first transaction", "edo"),
        Transaction(
            "Expenses",
            "Fertilizers",
            222,
            1.40,
            "27/05/2020",
            "second transaction",
            "ds"
        ),
        Transaction(
            "Expenses",
            "Fertilizers",
            333,
            1.40,
            "27/05/2020",
            "second transaction",
            "ds"
        ),
        Transaction(
            "Expenses",
            "Fertilizers",
            444,
            1.40,
            "27/05/2020",
            "second transaction",
            "ds"
        ),
        Transaction(
            "Expenses",
            "Fertilizers",
            555,
            1.40,
            "27/05/2020",
            "second transaction",
            "ds"
        ),
        Transaction(
            "Expenses",
            "Fertilizers",
            666,
            1.40,
            "27/05/2020",
            "second transaction",
            "ds"
        ),
        Transaction(
            "Expenses",
            "Fertilizers",
            777,
            1.40,
            "27/05/2020",
            "second transaction",
            "ds"
        ),

        )
}