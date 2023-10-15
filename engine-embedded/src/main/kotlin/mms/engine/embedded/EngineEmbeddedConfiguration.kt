package mms.engine.embedded

import mms.artist.api.ArtistApi
import org.jooq.DSLContext
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ImportAutoConfiguration(
    classes = [
        DataSourceAutoConfiguration::class,
        TransactionAutoConfiguration::class,
        FlywayAutoConfiguration::class,
        JooqAutoConfiguration::class,
    ],
)
class EngineEmbeddedConfiguration {

    @Bean
    fun engineRepository(dslContext: DSLContext) = EngineRepository(dslContext)

    @Bean
    fun engineEmbedded(repository: EngineRepository, api: ArtistApi) = EngineEmbedded(repository, api)
}
