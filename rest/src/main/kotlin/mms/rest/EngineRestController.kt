package mms.rest

import mms.engine.api.EngineApi
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

data class ArtistOfTheDay(
    val id: String,
)

@RestController
@RequestMapping("/public/api/v1/recommendations")
class EngineRestController(private val engine: EngineApi) {

    @GetMapping("/artist-of-the-day")
    fun get(@RequestParam date: LocalDate?): ResponseEntity<ArtistOfTheDay> =
        ResponseEntity.ok(ArtistOfTheDay(engine.getArtistOfTheDay(date ?: LocalDate.now())!!.external))
}
