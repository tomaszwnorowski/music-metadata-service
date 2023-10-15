package mms.engine.embedded

import mms.core.ResourceId
import mms.engine.embedded.jooq.codegen.tables.records.EngineArtistOfTheDayRecord
import mms.engine.embedded.jooq.codegen.tables.references.ENGINE_ARTIST_OF_THE_DAY
import org.jooq.DSLContext
import org.jooq.impl.DSL.max
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

open class EngineRepository(private val jooq: DSLContext) {

    @Transactional
    open fun findArtistOfTheDay(day: LocalDate): ResourceId? =
        with(ENGINE_ARTIST_OF_THE_DAY) {
            jooq.select(ARTIST_ID)
                .from(ENGINE_ARTIST_OF_THE_DAY)
                .where(DATE.eq(day))
                .fetchOne { ResourceId(it[ARTIST_ID]!!) }
        }

    @Transactional
    open fun findLastArtistOfTheDay(): Pair<LocalDate, ResourceId>? =
        with(ENGINE_ARTIST_OF_THE_DAY) {
            jooq.select(DATE, ARTIST_ID)
                .from(ENGINE_ARTIST_OF_THE_DAY)
                .where(DATE.eq(jooq.select(max(DATE)).from(ENGINE_ARTIST_OF_THE_DAY)))
                .fetchOne { it[DATE]!! to ResourceId(it[ARTIST_ID]!!) }
        }

    @Transactional
    open fun saveNextArtistOfTheDay(day: LocalDate, artist: ResourceId) =
        jooq.insertInto(ENGINE_ARTIST_OF_THE_DAY)
            .set(EngineArtistOfTheDayRecord(ResourceId.generate().internal, artist.internal, day))
            .execute()
}
