package com.example.composeapp.data.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.composeapp.data.database.fakes.FakeCafeDao
import com.example.composeapp.utils.TestDataFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

/**
 * Tests for FakeCafeDao using TestDataFactory
 */
class FakeCafeDaoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private lateinit var underTest: FakeCafeDao

    // setup
    @Before
    fun setUp() {
        underTest = FakeCafeDao()
    }

    // insert cafe - DONE
    // insert cafes - DONE
    // get cafe by id - DONE
    // get all cafes - DONE
    // get all cafes sync - DONE
    // delete all cafes - DONE
    // update cafe - DONE
    // delete cafe - DONE
    // search cafes - DONE
    // search cafes sync - DONE
    // get bookmarked cafe - DONE
    // update bookmark status - DONE
    // get cafes in bound - DONE
    // get cafes by min rating - DONE
    // get cafes by amenity - DONE
    // get cafe count - DONE

    /**
     * Testing insert cafe with the productive cup, tests that the specific details with
     * that cafe have been added
     */
    @Test
    fun whenInsertTheProductiveCupThenCafeIsAddedCorrectly() = runTest {
        // Arrange
        val productiveCup = TestDataFactory.createTheProductiveCup()
        // Act
        val insertedId = underTest.insertCafe(productiveCup)
        // Assert
        assertEquals(productiveCup.id, insertedId)
        val retrievedCafe = underTest.getCafeById(productiveCup.id)
        assertEquals("The Productive Cup", retrievedCafe?.name)
        assertEquals("082 Barbara Ferry, Palo Alto, CA 41855", retrievedCafe?.address)
        assertEquals(4, retrievedCafe?.averageRating)
        assertEquals("Good", retrievedCafe?.wifiQuality)
    }

    /**
     * Testing insert cafes, inserting the test cafe list and checking that each is added
     */
    @Test
    fun whenInsertMultipleCafesThenAllAreAdded() = runTest {
        // Arrange
        val realCafes = TestDataFactory.createTestCafeList()
        // Act
        underTest.insertCafes(realCafes)
        // Assert
        val allCafes = underTest.getAllCafesSync()
        assertEquals(4, allCafes.size)
        // Look for specific cafes
        val productiveCup = allCafes.find { it.name == "The Productive Cup" }
        val coffeeAndCode = allCafes.find { it.name == "Coffee & Code" }
        val espressoExpress = allCafes.find { it.name == "Espresso Express" }
        val codeAndCoffee = allCafes.find { it.name == "Code & Coffee Co." }
        assertNotNull(productiveCup)
        assertNotNull(coffeeAndCode)
        assertNotNull(espressoExpress)
        assertNotNull(codeAndCoffee)
    }

    /**
     * Testing that get cafe by id works with a cafe taken from a list of cafes
     */
    @Test
    fun whenGetCafeByIdWithValidIdThenCorrectCafeReturned() = runTest {
        // Arrange
        val espressoExpress = TestDataFactory.createTestCafeList().find {
            it.name == "Espresso Express"
        }!!
        underTest.insertCafe(espressoExpress)
        // Act
        val retrievedCafe = underTest.getCafeById(espressoExpress.id)
        // Assert
        assertNotNull(retrievedCafe)
        assertEquals("Espresso Express", retrievedCafe?.name)
        assertEquals(5, retrievedCafe?.averageRating)
        assertEquals("Excellent", retrievedCafe?.wifiQuality)
        assertTrue(retrievedCafe?.tags?.contains("Quiet zone") == true)
    }

    /**
     * Testing that get all cafes works
     */
    @Test
    fun whenGetAllCafesCalledThenAllCafesReturnedAsFlow() = runTest {
        // Arrange
        val allCafes = TestDataFactory.createTestCafeList()
        underTest.insertCafes(allCafes)
        // Act
        val retrievedAllCafesAsFlow = underTest.getAllCafes()
        val retrievedAllCafesAsList = retrievedAllCafesAsFlow.first()

        // Assert
        assertNotNull(retrievedAllCafesAsList)
        val productiveCup = retrievedAllCafesAsList.find { it.name == "The Productive Cup" }
        val coffeeAndCode = retrievedAllCafesAsList.find { it.name == "Coffee & Code" }
        val espressoExpress = retrievedAllCafesAsList.find { it.name == "Espresso Express" }
        val codeAndCoffee = retrievedAllCafesAsList.find { it.name == "Code & Coffee Co." }
        assertNotNull(productiveCup)
        assertNotNull(coffeeAndCode)
        assertNotNull(espressoExpress)
        assertNotNull(codeAndCoffee)
    }

    /**
     * Testing that get all cafes sync works
     */
    @Test
    fun whenGetAllCafesSyncCalledThenAllCafesReturnedAsList() = runTest {
        // Arrange
        val allCafes = TestDataFactory.createTestCafeList()
        underTest.insertCafes(allCafes)
        // Act
        val retrievedAllCafesAsList = underTest.getAllCafesSync()

        // Assert
        assertNotNull(retrievedAllCafesAsList)
        val productiveCup = retrievedAllCafesAsList.find { it.name == "The Productive Cup" }
        val coffeeAndCode = retrievedAllCafesAsList.find { it.name == "Coffee & Code" }
        val espressoExpress = retrievedAllCafesAsList.find { it.name == "Espresso Express" }
        val codeAndCoffee = retrievedAllCafesAsList.find { it.name == "Code & Coffee Co." }
        assertNotNull(productiveCup)
        assertNotNull(coffeeAndCode)
        assertNotNull(espressoExpress)
        assertNotNull(codeAndCoffee)
    }

    /**
     * Testing that delete cafe works with a specific cafe
     */
    @Test
    fun whenDeleteCafeCalledThenCafeDeleted() = runTest {
        // Arrange
        val allCafes = TestDataFactory.createTestCafeList()
        underTest.insertCafes(allCafes)
        val initialCafes = underTest.getAllCafesSync()
        assertEquals(4, initialCafes.size)

        // Act
        val cafeToDelete = initialCafes.find { it.name == "The Productive Cup" }!!
        underTest.deleteCafe(cafeToDelete)

        // Assert
        val remainingCafes = underTest.getAllCafesSync()
        assertEquals(3, remainingCafes.size)
        val productiveCup = remainingCafes.find { it.name == "The Productive Cup" }
        assertNull(productiveCup)
        val coffeeAndCode = remainingCafes.find { it.name == "Coffee & Code" }
        val espressoExpress = remainingCafes.find { it.name == "Espresso Express" }
        val codeAndCoffee = remainingCafes.find { it.name == "Code & Coffee Co." }
        assertNotNull(coffeeAndCode)
        assertNotNull(espressoExpress)
        assertNotNull(codeAndCoffee)
    }

    /**
     * Testing that delete all cafe works
     */
    @Test
    fun whenDeleteAllCafeCalledThenAllCafeDeleted() = runTest {
        // Arrange
        val allCafes = TestDataFactory.createTestCafeList()
        underTest.insertCafes(allCafes)
        val initialCafes = underTest.getAllCafesSync()
        assertEquals(4, initialCafes.size)

        // Act
        underTest.deleteAllCafes()

        // Assert
        val remainingCafes = underTest.getAllCafesSync()
        assertEquals(0, remainingCafes.size)
        val productiveCup = remainingCafes.find { it.name == "The Productive Cup" }
        assertNull(productiveCup)
        val coffeeAndCode = remainingCafes.find { it.name == "Coffee & Code" }
        val espressoExpress = remainingCafes.find { it.name == "Espresso Express" }
        val codeAndCoffee = remainingCafes.find { it.name == "Code & Coffee Co." }
        assertNull(coffeeAndCode)
        assertNull(espressoExpress)
        assertNull(codeAndCoffee)
    }

    /**
     * Testing that update cafe works with a specific cafe
     */
    @Test
    fun whenUpdateCafeCalledThenCafeUpdated() = runTest {
        // Arrange
        val allCafes = TestDataFactory.createTestCafeList()
        underTest.insertCafes(allCafes)
        val initialCafes = underTest.getAllCafesSync()
        // update
        val cafeToUpdate = initialCafes.first { it.name == "The Productive Cup" }
        val updatedCafe = cafeToUpdate.copy(
            name = "The Updated Productive Cup",
            studyRating = 5,
            averageRating = 5,
            isBookmarked = true,
            wifiAccess = 5
        )

        // Act
        underTest.updateCafe(updatedCafe)

        // Assert
        val remainingCafes = underTest.getAllCafesSync()
        assertEquals(allCafes.size, remainingCafes.size)
        val updatedCafeFromDb = remainingCafes.find { it.id == cafeToUpdate.id }
        assertNotNull(updatedCafeFromDb)
        assertEquals("The Updated Productive Cup", updatedCafeFromDb!!.name)
        assertEquals(5, updatedCafeFromDb.studyRating)
        assertEquals(5, updatedCafeFromDb.averageRating)
        assertEquals(true, updatedCafeFromDb.isBookmarked)
        assertEquals(5, updatedCafeFromDb.wifiAccess)

        // verify other cafes remain unchanged
        val coffeeAndCode = remainingCafes.find { it.name == "Coffee & Code" }
        val espressoExpress = remainingCafes.find { it.name == "Espresso Express" }
        val codeAndCoffee = remainingCafes.find { it.name == "Code & Coffee Co." }
        assertNotNull(coffeeAndCode)
        assertNotNull(espressoExpress)
        assertNotNull(codeAndCoffee)
    }


    /**
     * Testing search results with a specific cafe
     */
    @Test
    fun whenSearchForCoffeeThenCoffeeRelatedCafesReturned() = runTest {
        // Arrange
        val realCafes = TestDataFactory.createTestCafeList()
        underTest.insertCafes(realCafes)
        // Act
        val searchResults = underTest.searchCafesSync("Coffee")
        // Assert
        assertEquals(2, searchResults.size)
        assertTrue(searchResults.any { it.name == "Coffee & Code" })
        assertTrue(searchResults.any { it.name == "Code & Coffee Co." })
    }

    /**
     * Testing that searching for a location works
     */
    @Test
    fun whenSearchForPaloAltoThenProductiveCupReturned() = runTest {
        // Arrange
        val realCafes = TestDataFactory.createTestCafeList()
        underTest.insertCafes(realCafes)
        // Act
        val searchResults = underTest.searchCafesSync("Palo Alto")
        // Assert
        assertEquals(1, searchResults.size)
        assertEquals("The Productive Cup", searchResults[0].name)
    }

    /**
     * Testing search results when they don't exist
     */
    @Test
    fun whenSearchForNonexistentTermThenNoResultsReturned() = runTest {
        // Arrange
        val realCafes = TestDataFactory.createTestCafeList()
        underTest.insertCafes(realCafes)
        // Act
        val searchResults = underTest.searchCafesSync("Pizza")
        // Assert
        assertTrue(searchResults.isEmpty())
    }

    /**
     * Testing updating bookmark status after a change
     */
    @Test
    fun whenUpdateBookmarkStatusThenStatusIsChanged() = runTest {
        // Arrange
        val productiveCup = TestDataFactory.createTheProductiveCup(isBookmarked = false)
        underTest.insertCafe(productiveCup)
        // Act
        underTest.updateBookmarkStatus(productiveCup.id, true)
        // Assert
        val updatedCafe = underTest.getCafeById(productiveCup.id)
        assertTrue(updatedCafe?.isBookmarked == true)
    }

    /**
     * Testing that get bookmarked cafes work
     */
    @Test
    fun whenGetBookmarkedCafesThenOnlyBookmarkedOnesReturned() = runTest {
        // Arrange
        val realCafes = TestDataFactory.createTestCafeList() // Has 2 bookmarked cafes
        underTest.insertCafes(realCafes)
        // Act
        val bookmarkedCafes = underTest.getBookmarkedCafes().first()
        // Assert
        assertEquals(2, bookmarkedCafes.size)
        assertTrue(bookmarkedCafes.all { it.isBookmarked })
        assertTrue(bookmarkedCafes.any { it.name == "Coffee & Code" })
        assertTrue(bookmarkedCafes.any { it.name == "Code & Coffee Co." })
    }

    /**
     * Testing get cafe in bounds with cafes that have coordinates
     */
    @Test
    fun whenGetCafesInBoundsCalledThenOnlyCafesInBoundsReturned() = runTest {
        // Arrange
        val cafesWithCoordinates = listOf(
            TestDataFactory.createSimpleTestCafe(
                id = 1L,
                name = "North Cafe",
                latitude = 40.7128,
                longitude = -74.0060
            ),
            TestDataFactory.createSimpleTestCafe(
                id = 2L,
                name = "South Cafe",
                latitude = 25.7617,
                longitude = -80.1918
            ),
        )
        underTest.insertCafes(cafesWithCoordinates)
        // Act
        val cafesInBounds = underTest.getCafesInBounds(
            minLat = 40.0,
            maxLat = 41.0,
            minLng = -75.0,
            maxLng = -73.0
        )
        // Assert
        assertEquals(1, cafesInBounds.size)
        assertEquals("North Cafe", cafesInBounds[0].name)
    }

    /**
     * Testing get all cafes for flow
     */
    @Test
    fun whenGetAllCafesFlowThenFlowEmitsCorrectData() = runTest {
        // Arrange
        val realCafes = TestDataFactory.createTestCafeList()
        // Act
        underTest.insertCafes(realCafes)
        val allCafes = underTest.getAllCafes().first()
        // Assert
        assertEquals(4, allCafes.size)
        assertTrue(allCafes.any { it.name == "The Productive Cup" })
        assertTrue(allCafes.any { it.name == "Espresso Express" })
    }

    /**
     * Testing insert cafe with the same id, that it replaces the original cafe
     */
    @Test
    fun whenInsertCafeWithSameIdThenOldCafeIsReplaced() = runTest {
        // Arrange
        val originalCafe = TestDataFactory.createSimpleTestCafe(id = 1L, name = "Original")
        val replacementCafe = TestDataFactory.createSimpleTestCafe(id = 1L, name = "Replacement")
        // Act
        underTest.insertCafe(originalCafe)
        underTest.insertCafe(replacementCafe)
        // Assert
        val retrievedCafe = underTest.getCafeById(1L)
        assertEquals("Replacement", retrievedCafe?.name)
        assertEquals(1, underTest.getAllCafesSync().size)
    }

    /**
     * Testing delete all cafes, checks database
     */
    @Test
    fun whenDeleteAllCafesThenDatabaseIsEmpty() = runTest {
        // Arrange
        val realCafes = TestDataFactory.createTestCafeList()
        underTest.insertCafes(realCafes)
        // Act
        underTest.deleteAllCafes()
        // Assert
        val allCafes = underTest.getAllCafesSync()
        assertTrue(allCafes.isEmpty())
        assertEquals(0, underTest.getCafeCount())
    }

    /** Testing get cafes by min ratings with different study ratings
    */
    @Test
    fun whenGetCafesByMinRatingCalledThenOnlyCafesWithMinRatingReturned() = runTest {
        // Arrange
        val cafesWithRatings = listOf(
            TestDataFactory.createSimpleTestCafe(id = 1L, name = "Low Rating", studyRating = 2),
            TestDataFactory.createSimpleTestCafe(id = 2L, name = "Medium Rating", studyRating = 3),
            TestDataFactory.createSimpleTestCafe(id = 3L, name = "High Rating", studyRating = 5)
        )
        underTest.insertCafes(cafesWithRatings)
        // Act
        val highRatedCafes = underTest.getCafesByMinRating(4)
        // Assert
        assertEquals(1, highRatedCafes.size)
        assertEquals("High Rating", highRatedCafes[0].name)
        assertEquals(5, highRatedCafes[0].studyRating)
    }

    /**
     * Testing get cafes by amenities with different tags
     */
    @Test
    fun whenGetCafesByAmenityCalledThenOnlyCafesWithAmenityReturned() = runTest {
        // Arrange
        val cafesWithAmenities = listOf(
            TestDataFactory.createSimpleTestCafe(
                id = 1L,
                name = "WiFi Cafe",
                tags = "Free WiFi, Quiet"
            ),
            TestDataFactory.createSimpleTestCafe(
                id = 2L,
                name = "Parking Cafe",
                tags = "Parking, Outdoor seating"
            ),
            TestDataFactory.createSimpleTestCafe(
                id = 3L,
                name = "Both Cafe",
                tags = "Free WiFi, Parking, Study area"
            )
        )
        underTest.insertCafes(cafesWithAmenities)
        // Act
        val wifiCafes = underTest.getCafesByAmenity("WiFi")
        // Assert
        assertEquals(2, wifiCafes.size)
        assertTrue(wifiCafes.any { it.name == "WiFi Cafe" })
        assertTrue(wifiCafes.any { it.name == "Both Cafe" })
    }

    /**
     * Testing get cafe count functionality
     */
    @Test
    fun whenGetCafeCountCalledThenCorrectCountReturned() = runTest {
        // Arrange
        assertEquals(0, underTest.getCafeCount())
        // Act
        val testCafes = TestDataFactory.createTestCafeList()
        underTest.insertCafes(testCafes)
        // Assert
        assertEquals(4, underTest.getCafeCount())
        // Act
        val cafeToDelete = underTest.getAllCafesSync().first()
        underTest.deleteCafe(cafeToDelete)
        // Assert
        assertEquals(3, underTest.getCafeCount())
    }
}