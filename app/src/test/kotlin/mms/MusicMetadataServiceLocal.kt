package mms

import mms.artist.api.Artist
import mms.artist.api.ArtistApi
import mms.track.api.Genre.POP
import mms.track.api.Track
import mms.track.api.TrackApi
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.PostgreSQLContainerProvider
import java.time.Duration.ofSeconds

fun main(args: Array<String>) {
    SpringApplication(MusicMetadataService::class.java, LocalInfrastructureConfiguration::class.java).apply {
        setAdditionalProfiles("local")
        setDefaultProperties(mapOf("logging.level.org.jooq.tools.LoggerListener" to "DEBUG"))
        run(*args)
    }
}

@TestConfiguration
internal class LocalInfrastructureConfiguration {

    private final val artists =
        listOf("A-ha", "U2", "Goo Goo Dolls").associateWith { Artist(name = it) }
    private final val tracks =
        listOf(
            Track(title = "Take On Me", length = ofSeconds(200), genre = POP, artist = artists["A-ha"]!!.id),
            Track(title = "Lifelines", length = ofSeconds(200), genre = POP, artist = artists["A-ha"]!!.id),
            Track(title = "True North", length = ofSeconds(200), genre = POP, artist = artists["A-ha"]!!.id),
            Track(title = "With or Without You", length = ofSeconds(200), genre = POP, artist = artists["U2"]!!.id),
            Track(title = "Iris", length = ofSeconds(200), genre = POP, artist = artists["Goo Goo Dolls"]!!.id),
        )

    @Bean
    @ServiceConnection
    fun postgresql(): PostgreSQLContainer<*> =
        PostgreSQLContainerProvider().newInstance("15.0") as PostgreSQLContainer<*>

    @Bean
    fun artistInitializer(api: ArtistApi): ApplicationRunner =
        ApplicationRunner { artists.values.forEach { api.save(it) } }

    @Bean
    fun tracksInitializer(api: TrackApi): ApplicationRunner =
        ApplicationRunner { tracks.forEach { api.save(it) } }
}
