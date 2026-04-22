package com.boni.stemflow.core.database.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.boni.stemflow.core.database.entity.RemoteKeysEntity
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RemoteKeysDaoTest : BaseDaoTest() {

    @Test
    fun upsertAndReadByQuery() = runTest {
        val dao = db.remoteKeysDao()
        dao.upsert(RemoteKeysEntity(query = "q", prevKey = null, nextKey = 1))
        val loaded = dao.keysForQuery("q")!!
        assertEquals(1, loaded.nextKey)
        assertNull(loaded.prevKey)
    }

    @Test
    fun upsertReplacesExisting() = runTest {
        val dao = db.remoteKeysDao()
        dao.upsert(RemoteKeysEntity("q", prevKey = null, nextKey = 1))
        dao.upsert(RemoteKeysEntity("q", prevKey = 1, nextKey = 2))
        assertEquals(2, dao.keysForQuery("q")!!.nextKey)
    }

    @Test
    fun clearForQueryRemovesRow() = runTest {
        val dao = db.remoteKeysDao()
        dao.upsert(RemoteKeysEntity("q", prevKey = null, nextKey = 1))
        dao.clearForQuery("q")
        assertNull(dao.keysForQuery("q"))
    }
}
