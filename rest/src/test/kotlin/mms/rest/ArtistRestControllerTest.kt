package mms.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import mms.artist.api.ArtistApi
import mms.artist.test.fixture.ArtistFixture.artist
import mms.test.tag.IntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@IntegrationTest
@WebMvcTest(ArtistRestController::class)
class ArtistRestControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var json: ObjectMapper

    @MockkBean
    private lateinit var api: ArtistApi

    @Test
    fun `should return artists for existing identifier`() {
        every { api.findById(artist.id) } returns artist

        mvc.perform(get("/public/api/v1/artists/${artist.id.external}"))
            .andExpect(status().isOk)
            .andExpect(content().json(json.writeValueAsString(artist.toResource())))
    }

    @Test
    fun `should return not found for missing identifier`() {
        every { api.findById(artist.id) } returns null

        mvc.perform(get("/public/api/v1/resources/${artist.id.external}"))
            .andExpect(status().isNotFound)
    }

    @SpringBootApplication
    private class ResourcesRestControllerTestApplication
}
