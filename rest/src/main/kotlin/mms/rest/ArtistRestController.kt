package mms.rest

import mms.artist.api.Artist
import mms.artist.api.ArtistApi
import mms.core.ResourceId
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class CreateArtistRequest(
    val name: String,
    val aliases: Set<String>,
) {
    fun toArtist(): Artist =
        Artist(name = name, aliases = aliases)
}

data class RenameArtistRequest(
    val name: String,
)

data class ArtistResource(
    val id: String,
    val name: String,
    val aliases: Set<String>,
)

fun Artist.toResource(): ArtistResource =
    ArtistResource(id.external, name, aliases)

@RestController
@RequestMapping("/public/api/v1/artists")
class ArtistRestController(private val api: ArtistApi) {

    @PostMapping
    fun create(@RequestBody request: CreateArtistRequest): ResponseEntity<ArtistResource> =
        request.toArtist()
            .let { api.save(it) }
            .let { ResponseEntity.ok(it.toResource()) }

    @GetMapping("/{id}")
    fun get(@PathVariable id: String): ResponseEntity<ArtistResource> =
        id.takeIf { ResourceId.isValid(it) }
            ?.let { api.findById(ResourceId(it)) }
            ?.let { ResponseEntity.ok(it.toResource()) }
            ?: ResponseEntity.notFound().build()

    // Using REST "custom" method to put emphasis on the fact that changing artist name might have implications:
    // 1. Need to update search indexes
    // 2. Need to update caches
    @PostMapping("/{id}:rename")
    fun rename(@PathVariable id: String, @RequestBody request: RenameArtistRequest): ResponseEntity<Any> =
        id.takeIf { ResourceId.isValid(it) }
            ?.let { api.rename(ResourceId(it), request.name) }
            ?.let { ResponseEntity.ok().build() }
            ?: ResponseEntity.notFound().build()
}
