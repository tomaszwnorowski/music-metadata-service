package mms.engine.embedded

import mms.artist.api.ArtistApi
import mms.artist.embedded.ArtistEmbeddedConfiguration
import mms.artist.test.fixture.ArtistFixture.artist
import mms.test.tag.IntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@IntegrationTest
@SpringBootTest(
    classes = [
        ArtistEmbeddedConfiguration::class,
        EngineEmbeddedConfiguration::class,
    ],
    properties = [
        "spring.datasource.url=jdbc:tc:postgresql:15.0://integration",
        "logging.level.org.jooq.tools.LoggerListener=DEBUG",
    ],
)
class EngineEmbeddedTest(
    @Autowired private val engine: EngineEmbedded,
    @Autowired private val api: ArtistApi,
) {

    @Test
    fun `should precompute artist of the day for the next 20 days`() {
        // given
        val today = LocalDate.now()
        val artists = (0..100).map { artist() }
        artists.forEach { api.save(it) }

        // when - application starts for the first time
        engine.precomputeArtistOfTheDay()
        // when - application starts for the second time
        engine.precomputeArtistOfTheDay()

        // then
        (0..20).forEach {
            val aristOfTheDay = engine.getArtistOfTheDay(today.plusDays(it.toLong()))
            assertThat(aristOfTheDay).isEqualTo(api.findById(artists[it].id)!!.id)
        }
    }
}
