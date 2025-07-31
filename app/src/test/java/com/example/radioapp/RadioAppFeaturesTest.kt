package com.example.radioapp

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for the radio app new functionality
 */
class RadioAppFeaturesTest {

    @Test
    fun testRadioStationDataStructure() {
        // Test that radio stations data is properly structured
        val station = radioStationsData.first()
        
        assertNotNull("Station name should not be null", station.name)
        assertNotNull("Station URL should not be null", station.url)
        assertNotNull("Station country should not be null", station.country)
        assertNotNull("Station genre should not be null", station.genre)
        
        assertTrue("Station name should not be empty", station.name.isNotEmpty())
        assertTrue("Station URL should not be empty", station.url.isNotEmpty())
        assertTrue("Station country should not be empty", station.country.isNotEmpty())
        assertTrue("Station genre should not be empty", station.genre.isNotEmpty())
    }

    @Test
    fun testGetCountries() {
        val countries = getCountries()
        
        assertTrue("Should have multiple countries", countries.size > 1)
        assertTrue("Should contain Argentina", countries.contains("Argentina"))
        assertTrue("Should contain España", countries.contains("España"))
        
        // Test that countries are sorted
        val sortedCountries = countries.sorted()
        assertEquals("Countries should be sorted", sortedCountries, countries)
    }

    @Test
    fun testGetGenres() {
        val genres = getGenres()
        
        assertTrue("Should have multiple genres", genres.size > 1)
        assertTrue("Should contain Rock", genres.contains("Rock"))
        assertTrue("Should contain Pop", genres.contains("Pop"))
        
        // Test that genres are sorted
        val sortedGenres = genres.sorted()
        assertEquals("Genres should be sorted", sortedGenres, genres)
    }

    @Test
    fun testGetStationsByCountry() {
        val stationsByCountry = getStationsByCountry()
        
        assertTrue("Should have stations for multiple countries", stationsByCountry.size > 1)
        assertTrue("Should have stations for Argentina", stationsByCountry.containsKey("Argentina"))
        assertTrue("Should have stations for España", stationsByCountry.containsKey("España"))
        
        // Test that Argentina has multiple stations
        val argentinaStations = stationsByCountry["Argentina"]
        assertNotNull("Argentina should have stations", argentinaStations)
        assertTrue("Argentina should have multiple stations", argentinaStations!!.size > 1)
    }

    @Test
    fun testGetStationsByGenre() {
        val stationsByGenre = getStationsByGenre()
        
        assertTrue("Should have stations for multiple genres", stationsByGenre.size > 1)
        assertTrue("Should have Rock stations", stationsByGenre.containsKey("Rock"))
        assertTrue("Should have Pop stations", stationsByGenre.containsKey("Pop"))
        
        // Test that Rock has stations
        val rockStations = stationsByGenre["Rock"]
        assertNotNull("Rock should have stations", rockStations)
        assertTrue("Rock should have at least one station", rockStations!!.isNotEmpty())
    }

    @Test
    fun testRadioStationsMapCompatibility() {
        // Test that the original radioStations map is still compatible
        assertTrue("Should have multiple stations", radioStations.size > 10)
        
        // Test specific stations exist
        assertTrue("Should contain Los 40", radioStations.containsKey("Los 40"))
        assertTrue("Should contain VORTERIX", radioStations.containsKey("VORTERIX"))
        
        // Test URL format
        radioStations.values.forEach { url ->
            assertTrue("URL should start with http", url.startsWith("http"))
        }
    }

    @Test
    fun testDataConsistency() {
        // Test that radioStations map and radioStationsData are consistent
        assertEquals("Both data structures should have same size", 
                    radioStations.size, radioStationsData.size)
        
        // Test that all stations in map exist in data
        radioStations.forEach { (name, url) ->
            val stationData = radioStationsData.find { it.name == name }
            assertNotNull("Station $name should exist in data", stationData)
            assertEquals("URLs should match for $name", url, stationData!!.url)
        }
    }

    @Test
    fun testStationNamesUnique() {
        val names = radioStationsData.map { it.name }
        val uniqueNames = names.toSet()
        
        assertEquals("All station names should be unique", names.size, uniqueNames.size)
    }
}