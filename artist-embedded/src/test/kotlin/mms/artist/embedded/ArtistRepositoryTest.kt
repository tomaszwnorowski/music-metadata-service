package mms.artist.embedded

import mms.artist.test.fixture.ArtistFixture.artist
import mms.test.tag.IntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@IntegrationTest
@SpringBootTest(
    classes = [ArtistEmbeddedConfiguration::class],
    properties = [
        "spring.datasource.url=jdbc:tc:postgresql:15.0://integration",
        "logging.level.org.jooq.tools.LoggerListener=DEBUG",
    ],
)
class ArtistRepositoryTest(@Autowired private val repository: ArtistRepository) {

    @Test
    fun `should save artist with default alias`() {
        // when
        val persistedArtist = repository.save(artist)

        // then
        assertAll(
            { assertThat(persistedArtist.id).isNotNull() },
            { assertThat(persistedArtist.name).isEqualTo(artist.name) },
            { assertThat(persistedArtist.aliases).isEqualTo(artist.aliases) },
        )
    }

    @Test
    fun `should save artist with multiple aliases`() {
        // given
        val newArtist = artist("artist-1", setOf("artist-1", "artist-1-alias-1", "artist-1-alias2"))

        // when
        val persistedArtist = repository.save(newArtist)

        // then
        assertAll(
            { assertThat(persistedArtist.id).isNotNull() },
            { assertThat(persistedArtist.name).isEqualTo(newArtist.name) },
            { assertThat(persistedArtist.aliases).containsOnly("artist-1", "artist-1-alias-1", "artist-1-alias2") },
        )
    }

    @Test
    fun `should save new name as an alias while renaming arist`() {
        // given
        val newArtist = artist("artist-2", setOf("artist-2", "artist-2-alias-1", "artist-2-alias-2"))

        // when
        val persistedArtist = repository.save(newArtist).also {
            repository.rename(it.id, "renamed")
        }

        // then
        with(repository.findById(persistedArtist.id)!!) {
            assertAll(
                { assertThat(id).isNotNull() },
                { assertThat(name).isEqualTo("renamed") },
                { assertThat(aliases).containsOnly("renamed", "artist-2", "artist-2-alias-1", "artist-2-alias-2") },
            )
        }
    }
}
