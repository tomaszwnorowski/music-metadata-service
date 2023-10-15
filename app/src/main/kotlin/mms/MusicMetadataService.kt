package mms

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MusicMetadataService

fun main(args: Array<String>) {
    runApplication<MusicMetadataService>(*args)
}
