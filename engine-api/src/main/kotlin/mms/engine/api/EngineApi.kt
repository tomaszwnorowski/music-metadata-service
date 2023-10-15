package mms.engine.api

import mms.core.ResourceId
import java.time.LocalDate

interface EngineApi {
    // since it might be different date in different time zone this should be passed from the client or derived
    // from environment (geolocation)
    fun getArtistOfTheDay(date: LocalDate = LocalDate.now()): ResourceId?
}
