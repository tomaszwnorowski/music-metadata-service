package mms.engine.embedded

import mms.artist.api.ArtistApi
import mms.core.ResourceId
import mms.engine.api.EngineApi
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap

open class EngineEmbedded(
    private val repository: EngineRepository,
    private val api: ArtistApi,
    private val cache: MutableMap<LocalDate, ResourceId> = ConcurrentHashMap(),
) : EngineApi, ApplicationListener<ApplicationReadyEvent> {

    // for simplicity lets assume that database is not empty, otherwise we have much greater issue to solve
    override fun getArtistOfTheDay(date: LocalDate): ResourceId? =
        cache.computeIfAbsent(date) { repository.findArtistOfTheDay(date)!! }

    // TODO transaction mixed with rest calls, used TransactionOperations and handle them programmatically
    @Transactional
    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        precomputeArtistOfTheDay()
    }

    // since the ordering is deterministic we can precompute artist for next n (arbitrary number) days
    internal fun precomputeArtistOfTheDay() {
        val lastArtist = repository.findLastArtistOfTheDay()

        if (lastArtist == null) {
            precomputeFromScratch(LocalDate.now())
        } else {
            precomputeContinue(lastArtist.second, lastArtist.first)
        }
    }

    private fun precomputeFromScratch(currentDate: LocalDate) {
        val artists = api.findAll()

        artists.withIndex().forEach { (index, artist) ->
            repository.saveNextArtistOfTheDay(currentDate.plusDays((index).toLong()), artist.id)
        }
    }

    private fun precomputeContinue(lastId: ResourceId, lastDate: LocalDate) {
        val artists = api.findAll(lastId).ifEmpty { api.findAll() }

        artists.withIndex().forEach { (index, artist) ->
            repository.saveNextArtistOfTheDay(lastDate.plusDays((index + 1).toLong()), artist.id)
        }
    }
}
