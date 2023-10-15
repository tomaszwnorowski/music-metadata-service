package mms.track.test.fixture

import mms.core.ResourceId
import mms.track.api.Genre
import mms.track.api.Track
import java.time.Duration
import kotlin.random.Random

object TrackFixture {

    private val random: Random = Random(1234)

    // TODO stable id
    val track = Track(
        ResourceId.generate(),
        "Take On Me",
        Duration.ofSeconds(224),
        Genre.POP,
        ResourceId.generate(),
    )

    fun track(artist: ResourceId = ResourceId.generate(), title: String): Track =
        Track(
            id = ResourceId.generate(),
            title = title,
            length = Duration.ofSeconds(random.nextLong(150, 300)),
            genre = Genre.values().random(random),
            artist = artist,
        )
}
