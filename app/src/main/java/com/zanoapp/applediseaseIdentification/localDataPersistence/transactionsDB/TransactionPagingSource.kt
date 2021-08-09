package com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB

import androidx.paging.PagingSource
import androidx.paging.PagingState

class TransactionPagingSource : PagingSource<Int, Transaction>() {
    override fun getRefreshKey(state: PagingState<Int, Transaction>): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Transaction> {
        TODO("Not yet implemented")
    }
}