package mms.track.embedded

import mms.core.ResourceId
import mms.track.api.Genre
import mms.track.api.Page
import mms.track.api.Track
import mms.track.api.TrackAlreadyExists
import mms.track.api.TrackApi
import mms.track.embedded.jooq.codegen.tables.records.TrackTrackRecord
import mms.track.embedded.jooq.codegen.tables.references.TRACK_TRACK
import org.jooq.DSLContext
import org.jooq.types.YearToSecond
import org.springframework.transaction.annotation.Transactional

open class TrackRepository(private val jooq: DSLContext) : TrackApi {
    @Transactional
    override fun save(track: Track): Track =
        with(TRACK_TRACK) {
            if (!jooq.fetchExists(this, TITLE.eq(track.title), ARTIST_ID.eq(track.artist.internal))) {
                jooq.insertInto(this)
                    .set(track.toRecord())
                    .returning()
                    .fetchSingle()
                    .toTrack()
            } else {
                throw TrackAlreadyExists(track.title)
            }
        }

    // 1. stable ordering preserving insertion order (could be sorted by any other or multiple columns)
    // 2. keyset pagination since it's more efficient and intuitive
    @Transactional
    override fun findAll(artistId: ResourceId, page: Page): List<Track> =
        jooq.selectFrom(TRACK_TRACK)
            .where(TRACK_TRACK.ARTIST_ID.eq(artistId.internal))
            .orderBy(TRACK_TRACK.ID)
            .apply { page.afterTrack?.let { seek(it.internal) } }
            .limit(page.limit)
            .fetch()
            .map { it.toTrack() }

    private fun Track.toRecord(): TrackTrackRecord =
        TrackTrackRecord(
            id.internal,
            title,
            YearToSecond.valueOf(length),
            artist.internal,
            genre.id.internal,
        )

    private fun TrackTrackRecord.toTrack(): Track =
        Track(
            ResourceId(id!!),
            title!!,
            length!!.toDuration(),
            Genre.ofId(ResourceId(genreId!!)),
            ResourceId(artistId!!),
        )
}
