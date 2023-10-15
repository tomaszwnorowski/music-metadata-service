package mms.track.embedded

import mms.core.ResourceId
import mms.test.tag.IntegrationTest
import mms.track.api.Page
import mms.track.api.TrackAlreadyExists
import mms.track.test.fixture.TrackFixture.track
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@IntegrationTest
@SpringBootTest(
    classes = [TrackEmbeddedConfiguration::class],
    properties = [
        "spring.datasource.url=jdbc:tc:postgresql:15.0://integration",
        "logging.level.org.jooq.tools.LoggerListener=DEBUG",
    ],
)
class TrackRepositoryTest(@Autowired private val repository: TrackRepository) {

    @Test
    fun `should save new track`() {
        // given
        val newTrack = track(title = "title")

        // when
        val persistedTrack = repository.save(newTrack)

        // then
        assertAll(
            { Assertions.assertThat(persistedTrack.id).isNotNull() },
            { Assertions.assertThat(persistedTrack.title).isEqualTo(newTrack.title) },
            { Assertions.assertThat(persistedTrack.length).isEqualTo(newTrack.length) },
            { Assertions.assertThat(persistedTrack.genre).isEqualTo(newTrack.genre) },
            { Assertions.assertThat(persistedTrack.artist).isEqualTo(newTrack.artist) },
        )
    }

    @Test
    fun `should fail to save track with the same name and artist`() {
        // given
        val artist = ResourceId.generate()
        val title = "title"

        // then
        Assertions.assertThatThrownBy {
            repository.save(track(title = title, artist = artist))
            repository.save(track(title = title, artist = artist))
        }.isInstanceOf(TrackAlreadyExists::class.java)
    }

    @Test
    fun `should support pagination`() {
        // given
        val artist = ResourceId.generate()
        val tracks = (0..25).map { track(artist, "title-$it") }

        // when
        tracks.forEach { repository.save(it) }

        // then
        val fistPage = repository.findAll(artist, Page())
        val secondPage = repository.findAll(artist, Page(afterTrack = fistPage.last().id))
        val thirdPage = repository.findAll(artist, Page(afterTrack = secondPage.last().id))
        val fourthPage = repository.findAll(artist, Page(afterTrack = thirdPage.last().id))
        // and
        assertAll(
            { Assertions.assertThat(fistPage).isEqualTo(tracks.take(10)) },
            { Assertions.assertThat(secondPage).isEqualTo(tracks.drop(10).take(10)) },
            { Assertions.assertThat(thirdPage).isEqualTo(tracks.drop(20)) },
            { Assertions.assertThat(fourthPage).isEmpty() },
        )
    }
}
