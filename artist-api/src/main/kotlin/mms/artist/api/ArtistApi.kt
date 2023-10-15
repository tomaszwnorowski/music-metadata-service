package mms.artist.api

import mms.core.ResourceId

data class Artist(
    val id: ResourceId = ResourceId.generate(),
    val name: String,
    val aliases: Set<String> = setOf(name),
) {
    init {
        require(aliases.contains(name)) { "Name must be one of the aliases" }
    }
}

interface ArtistApi {
    fun save(artist: Artist): Artist

    fun findById(id: ResourceId): Artist?
    fun findByAlias(alias: String): Artist?

    // Dedicated methods instead of general purpose update on all properties because:
    // 1. update on properties used for searching should propagate to search indexes
    // 2. update on properties used for searching should update caches
    // In other words, there might be non-trivial logic involved that wouldn't be needed for "regular" fields.
    fun rename(id: ResourceId, name: String)

    fun findAll(after: ResourceId? = null, limit: Int = 20): List<Artist>
}

class ArtistNotFoundException(resourceId: ResourceId) : RuntimeException()
