package mms.artist.embedded

import mms.artist.api.Artist
import mms.artist.api.ArtistApi
import mms.artist.api.ArtistNotFoundException
import mms.artist.embedded.jooq.codegen.tables.records.ArtistAliasRecord
import mms.artist.embedded.jooq.codegen.tables.records.ArtistArtistRecord
import mms.artist.embedded.jooq.codegen.tables.references.ARTIST_ALIAS
import mms.artist.embedded.jooq.codegen.tables.references.ARTIST_ARTIST
import mms.core.ResourceId
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.Records
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.noCondition
import org.jooq.impl.DSL.select
import org.springframework.transaction.annotation.Transactional

open class ArtistRepository(private val jooq: DSLContext) : ArtistApi {

    @Transactional
    override fun save(artist: Artist): Artist = artist.apply {
        jooq.insertInto(ARTIST_ARTIST)
            .set(toArtistRecord())
            .execute()
            .also {
                jooq.batchInsert(toAliasRecords())
                    .execute()
            }
    }

    @Transactional
    override fun findById(id: ResourceId): Artist? =
        findOneByCondition(artistCondition = ARTIST_ARTIST.ID.eq(id.internal))

    @Transactional
    override fun findByAlias(alias: String): Artist? =
        findOneByCondition(aliasCondition = ARTIST_ALIAS.ALIAS.eq(alias))

    @Transactional
    override fun findAll(after: ResourceId?, limit: Int): List<Artist> =
        findAllByCondition(after, limit)

    private fun findOneByCondition(
        artistCondition: Condition = noCondition(),
        aliasCondition: Condition = noCondition(),
    ): Artist? =
        jooq.select(
            ARTIST_ARTIST.ID.convertFrom { ResourceId(it!!) },
            ARTIST_ARTIST.NAME.convertFrom { it!! },
            multiset(
                select(ARTIST_ALIAS.ALIAS)
                    .from(ARTIST_ALIAS)
                    .where(ARTIST_ALIAS.ARTIST_ID.eq(ARTIST_ARTIST.ID), aliasCondition),
            ).convertFrom { record -> record.map { it[ARTIST_ALIAS.ALIAS] }.toSet() },
        )
            .from(ARTIST_ARTIST)
            .where(artistCondition)
            .fetchOne(Records.mapping(::Artist))

    private fun findAllByCondition(
        after: ResourceId?,
        limit: Int,
    ): List<Artist> =
        jooq.select(
            ARTIST_ARTIST.ID.convertFrom { ResourceId(it!!) },
            ARTIST_ARTIST.NAME.convertFrom { it!! },
            multiset(
                select(ARTIST_ALIAS.ALIAS)
                    .from(ARTIST_ALIAS)
                    .where(ARTIST_ALIAS.ARTIST_ID.eq(ARTIST_ARTIST.ID)),
            ).convertFrom { record -> record.map { it[ARTIST_ALIAS.ALIAS] }.toSet() },
        )
            .from(ARTIST_ARTIST)
            .orderBy(ARTIST_ARTIST.ID)
            .apply { after?.let { seek(it.internal) } }
            .limit(limit)
            .fetch(Records.mapping(::Artist))

    // Should be promoted to service logic since it's not a data store specific operation. However, introducing another
    // indirection layer (service) just for this single use case wouldn't be pragmatic.
    @Transactional
    override fun rename(id: ResourceId, name: String) {
        jooq.update(ARTIST_ARTIST)
            .set(ARTIST_ARTIST.NAME, name)
            .where(ARTIST_ARTIST.ID.eq(id.internal))
            .execute()
            .let { if (it == 0) throw ArtistNotFoundException(id) }
            .also {
                jooq.insertInto(ARTIST_ALIAS)
                    .set(ArtistAliasRecord(ResourceId.generate().internal, name, id.internal))
                    .execute()
            }
    }

    private fun Artist.toArtistRecord(): ArtistArtistRecord =
        ArtistArtistRecord(id.internal, name)

    private fun Artist.toAliasRecords(): Collection<ArtistAliasRecord> =
        aliases.map { ArtistAliasRecord(ResourceId.generate().internal, it, id.internal) }
}
