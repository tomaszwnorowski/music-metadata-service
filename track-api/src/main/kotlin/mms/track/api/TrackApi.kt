package mms.track.api

import mms.core.ResourceId
import java.time.Duration

enum class Genre(val id: ResourceId) {
    POP(ResourceId("0dwxca9p7cg7m")),
    ROCK(ResourceId("0dwxca9pfcg7s")),
    ;

    companion object {
        fun ofId(id: ResourceId) =
            values().single { id == it.id }
    }
}

data class Track(
    val id: ResourceId = ResourceId.generate(),
    val title: String,
    val length: Duration,
    val genre: Genre,
    val artist: ResourceId,
)

data class Page(
    val afterTrack: ResourceId? = null,
    // TODO should be externalized to configuration property
    val limit: Int = 10,
)

interface TrackApi {
    fun save(track: Track): Track

    fun findAll(artistId: ResourceId, page: Page = Page()): List<Track>
}

class TrackAlreadyExists(title: String) : RuntimeException()
