package mms.rest

import mms.core.ResourceId
import mms.track.api.Genre
import mms.track.api.Page
import mms.track.api.Track
import mms.track.api.TrackApi
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Duration

data class CreateTrackRequest(
    val title: String,
    val lengthInSeconds: Long,
    val artistId: String,
    val genre: Genre,
) {
    fun toTrack(): Track =
        Track(
            title = title,
            length = Duration.ofSeconds(lengthInSeconds),
            genre = genre,
            artist = ResourceId(artistId),
        )
}

data class TrackPage(
    val tracks: List<TrackResource>,
)

data class TrackResource(
    val id: String,
    val title: String,
    val lengthInSeconds: Long,
    val artistId: String,
    val genre: Genre,
)

fun Track.toResource(): TrackResource =
    TrackResource(
        id.external,
        title,
        length.toSeconds(),
        artist.external,
        genre,
    )

@RestController
@RequestMapping("/public/api/v1/tracks")
class TrackRestController(private val api: TrackApi) {

    @PostMapping
    fun create(request: CreateTrackRequest): ResponseEntity<TrackResource> =
        request.toTrack()
            .let { api.save(it) }
            .let { ResponseEntity.ok(it.toResource()) }

    @GetMapping
    fun list(
        @RequestParam("artistId") artistId: String,
        @RequestParam("lastTrackId") lastTrackId: String?,
    ): ResponseEntity<TrackPage> =
        Page(lastTrackId?.let { ResourceId(it) })
            .let { api.findAll(ResourceId(artistId), it) }
            .map { it.toResource() }
            .let { TrackPage(it) }
            .let { ResponseEntity.ok(it) }
}
