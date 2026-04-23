package com.boni.stemflow.core.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.boni.stemflow.core.domain.model.Track
import com.boni.stemflow.core.network.NetworkDataSource
import retrofit2.HttpException
import java.io.IOException

/**
 * Paginates iTunes Search despite the API ignoring `offset`: every call returns the same top-N
 * regardless of offset, capped at 200 items per query. Each APPEND issues one network call with a
 * larger `limit` and emits only the unseen suffix, deduplicating by `trackId` to absorb any
 * ranking drift between calls.
 */
class ITunesSearchPagingSource(
    private val query: String,
    private val network: NetworkDataSource,
    private val pageSize: Int = DEFAULT_PAGE_SIZE,
) : PagingSource<Int, Track>() {

    private var cache: List<Track> = emptyList()
    private val seenTrackIds: MutableSet<Long> = HashSet()

    override val jumpingSupported: Boolean = false

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Track> {
        if (query.isBlank() || params.loadSize <= 0) {
            return emptyPage()
        }

        val startIndex = (params.key ?: 0).coerceAtLeast(0)

        return try {
            val batch = collectUnique(from = startIndex, count = params.loadSize)
            LoadResult.Page(data = batch.tracks, prevKey = null, nextKey = batch.nextKey)
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Track>): Int? = null

    private suspend fun collectUnique(from: Int, count: Int): Batch {
        val tracks = ArrayList<Track>(count)
        var cursor = from
        while (tracks.size < count) {
            if (!ensureCacheCovers(cursor)) {
                return Batch(tracks, nextKey = null)
            }
            val track = cache[cursor]
            if (seenTrackIds.add(track.trackId)) {
                tracks.add(track)
            }
            cursor++
        }
        return Batch(tracks, nextKey = cursor)
    }

    private suspend fun ensureCacheCovers(index: Int): Boolean {
        if (index < cache.size) return true
        if (cache.size >= MAX_LIMIT) return false

        val targetLimit = (cache.size + pageSize).coerceAtMost(MAX_LIMIT)
        val fetched = network.search(term = query, limit = targetLimit, offset = 0)
        if (fetched.size <= cache.size) return false

        cache = fetched
        return index < cache.size
    }

    private fun emptyPage(): LoadResult.Page<Int, Track> =
        LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)

    private data class Batch(val tracks: List<Track>, val nextKey: Int?)

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
        const val MAX_LIMIT = 200
    }
}
