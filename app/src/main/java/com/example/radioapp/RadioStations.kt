package com.example.radioapp

data class RadioStation(
    val name: String,
    val url: String,
    val country: String,
    val genre: String
)

val radioStationsData = listOf(
    RadioStation("Galaxxy France", "https://eu8.fastcast4u.com/proxy/rockfmgm?mp=/1", "Francia", "Rock"),
    RadioStation("LOCA Melodic", "https://s2.we4stream.com/listen/loca_melodic_techno/live", "España", "Electrónica"),
    RadioStation("LOCA Techno", "https://s2.we4stream.com/listen/loca_techo/live", "España", "Electrónica"),
    RadioStation("LOCA House", "https://s2.we4stream.com/listen/loca_house/live", "España", "Electrónica"),
    RadioStation("Los 40", "https://edge02.radiohdvivo.com/stream/los40", "España", "Pop"),
    RadioStation("Aspen", "https://27413.live.streamtheworld.com/ASPEN.mp3", "Argentina", "Rock"),
    RadioStation("VORTERIX", "https://ice2.edge-apps.net/radio1_high-20057.audio", "Argentina", "Rock"),
    RadioStation("Boing", "https://streaming.redboing.com/radio/8000/radio.aac", "España", "Infantil"),
    RadioStation("Del Siglo", "https://stream.lt8.com.ar:8443/delsiglo995.mp3", "Argentina", "Pop"),
    RadioStation("88.7", "https://streaming.redboing.com/radio/8010/radio.aac", "España", "Pop"),
    RadioStation("Crystal FM", "https://radio02.ferozo.com/proxy/ra02001330?mp=/stream?ver%3D468915", "Argentina", "Pop"),
    RadioStation("UNR", "https://cdn.instream.audio/:9202/stream", "Argentina", "Universitaria"),
    RadioStation("Cadena 3", "https://26683.live.streamtheworld.com/RADIO3_SC", "Argentina", "Noticias"),
    RadioStation("House Nation UK", "https://streaming.radio.co/s06bd9d805/listen", "Reino Unido", "Electrónica"),
    RadioStation("La Red", "https://27353.live.streamtheworld.com/LA_RED_AM910AAC_SC", "Argentina", "Noticias"),
    RadioStation("FM Vida", "https://streaming450tb.locucionar.com/proxy/fmvida979?mp=/stream", "Argentina", "Cristiana")
)

// Keep the original map for compatibility
val radioStations = radioStationsData.associate { it.name to it.url }

// Helper functions for categorization
fun getStationsByCountry(): Map<String, List<RadioStation>> {
    return radioStationsData.groupBy { it.country }
}

fun getStationsByGenre(): Map<String, List<RadioStation>> {
    return radioStationsData.groupBy { it.genre }
}

fun getCountries(): List<String> {
    return radioStationsData.map { it.country }.distinct().sorted()
}

fun getGenres(): List<String> {
    return radioStationsData.map { it.genre }.distinct().sorted()
}
