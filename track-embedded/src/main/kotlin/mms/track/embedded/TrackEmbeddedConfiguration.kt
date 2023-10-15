package mms.track.embedded

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
class TrackEmbeddedConfiguration {

    @Bean
    fun trackRepository(dslContext: DSLContext): TrackRepository = TrackRepository(dslContext)
}
