package mms.artist.test.fixture

import mms.artist.api.Artist
import mms.core.ResourceId
import kotlin.random.Random

object ArtistFixture {
    private val random = Random(1234)

    val artist = Artist(ResourceId("0dkj5j9pdtswk"), "A-ha", setOf("A-ha"))

    fun artist(name: String = "name-${random.nextLong()}", aliases: Set<String> = setOf(name)): Artist =
        Artist(ResourceId.generate(), name, aliases)
}
