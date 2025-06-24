package com.example.composeapp.utils

import com.example.composeapp.data.database.CafeEntity

object TestDataFactory {
    fun createTestCafeEntity(
        id: Long = 1L,
        name: String = "The Productive Cup",
        street: String = "082 Barbara Ferry",
        city: String = "Palo Alto",
        state: String = "CA",
        zipCode: String = "41855",
        studyRating: Int = 2,
        latitude: Double? = 37.78502907244787,
        longitude: Double? = -122.02845225960077,
        phone: String = "001-590-586-9810x696",
        website: String = "https://www.theproductivecup.com",
        amenities: List<String> = listOf(
            "Wheelchair accessible",
            "Group study rooms",
            "Pet friendly",
            "Outdoor seating",
            "Meeting rooms"
        ),
        wifiAccess: Int = 2,
        outletAccessibility: Int = 2,
        averageRating: Int = 4,
        isBookmarked: Boolean = false
    ): CafeEntity {
        return CafeEntity(
            id = id,
            apiId = "68571e8e0018c15b87afe82$id",
            name = name,
            address = "$street, $city, $state $zipCode",
            description = "A great place to study and work",
            latitude = latitude,
            longitude = longitude,
            phone = phone,
            website = website,
            hours = when (id % 4) {
                0L -> "Monday: 7AM - 8PM, Tuesday: 8AM - 9PM, Wednesday: 8AM - 9PM, Thursday: 6AM - 10PM, Friday: 6AM - 9PM, Saturday: 8AM - 9PM, Sunday: 9AM - 8PM"
                1L -> "Monday: 7AM - 8PM, Tuesday: 7AM - 8PM, Wednesday: 6AM - 9PM, Thursday: 8AM - 8PM, Friday: 6AM - 10PM, Saturday: 9AM - 7PM, Sunday: 8AM - 8PM"
                2L -> "Monday: 8AM - 9PM, Tuesday: 6AM - 8PM, Wednesday: 8AM - 10PM, Thursday: 8AM - 9PM, Friday: 7AM - 9PM, Saturday: 8AM - 7PM, Sunday: 9AM - 7PM"
                else -> "Monday: 6AM - 8PM, Tuesday: 7AM - 9PM, Wednesday: 6AM - 8PM, Thursday: 6AM - 9PM, Friday: 7AM - 9PM, Saturday: 8AM - 8PM, Sunday: 7AM - 9PM"
            },
            priceRange = "$$",
            tags = amenities.joinToString(", "),
            studyRating = studyRating, // Fixed: was using averageRating
            ambianceRating = averageRating,
            averageRating = averageRating,
            wifiAccess = wifiAccess,
            outletAccessibility = outletAccessibility,
            wifiQuality = when (wifiAccess) {
                0 -> "None"
                1 -> "Poor"
                2 -> "Good"
                3 -> "Excellent"
                else -> "Unknown"
            },
            powerOutlets = when (outletAccessibility) {
                0 -> "None"
                1 -> "Limited"
                2 -> "Some"
                3 -> "Many"
                else -> "Unknown"
            },
            noiseLevel = "Medium",
            seatingCapacity = null,
            hasParking = amenities.any { it.contains("Parking", ignoreCase = true) },
            hasFood = true,
            hasCoffee = true,
            hasStudyArea = amenities.any {
                it.contains("study", ignoreCase = true) ||
                        it.contains("meeting", ignoreCase = true) ||
                        it.contains("quiet", ignoreCase = true)
            },
            imageUrl = "https://picsum.photos/800/600",
            thumbnailUrl = "https://picsum.photos/800/600",
            atmosphereTags = "",
            energyLevelTags = "",
            studyFriendlyTags = "",
            ratingImageUrls = "",
            createdAt = "2025-06-21T21:05:18.525Z",
            updatedAt = "2025-06-21T21:05:18.525Z",
            isBookmarked = isBookmarked
        )
    }

    fun createTestCafeList(): List<CafeEntity> {
        return listOf(
            createTestCafeEntity(
                id = 1L,
                name = "The Productive Cup",
                street = "082 Barbara Ferry",
                city = "Palo Alto",
                state = "CA",
                zipCode = "41855",
                studyRating = 2, // Fixed: explicitly set studyRating
                latitude = 37.78502907244787,
                longitude = -122.02845225960077,
                phone = "001-590-586-9810x696",
                website = "https://www.theproductivecup.com",
                amenities = listOf("Wheelchair accessible", "Group study rooms", "Pet friendly", "Outdoor seating", "Meeting rooms"),
                wifiAccess = 2, // Good
                outletAccessibility = 2, // Some
                averageRating = 4,
                isBookmarked = false
            ),
            createTestCafeEntity(
                id = 2L,
                name = "Coffee & Code",
                street = "71851 Richard Flats",
                city = "Oakland",
                state = "CA",
                zipCode = "57525",
                studyRating = 3, // Fixed: set different studyRating
                latitude = 37.76115298322381,
                longitude = -122.4787779091634,
                phone = "+1-788-854-0638x00522",
                website = "https://www.coffeeandcode.com",
                amenities = listOf("Power outlets", "Parking available", "Meeting rooms", "Lockers", "Pet friendly", "Group study rooms"),
                wifiAccess = 1,
                outletAccessibility = 2,
                averageRating = 4,
                isBookmarked = true // different for testing
            ),

            createTestCafeEntity(
                id = 3L,
                name = "Espresso Express",
                street = "47130 Cobb Valley",
                city = "Oakland",
                state = "CA",
                zipCode = "13057",
                studyRating = 5, // Fixed: set different studyRating
                latitude = 37.75283253786676,
                longitude = -122.3346873375079,
                phone = "001-406-899-7056x1986",
                website = "https://www.espressoexpress.com",
                amenities = listOf("Outdoor seating", "Quiet zone", "24/7 access", "Power outlets", "Wheelchair accessible"),
                wifiAccess = 3,
                outletAccessibility = 2,
                averageRating = 5,
                isBookmarked = false
            ),

            createTestCafeEntity(
                id = 4L,
                name = "Code & Coffee Co.",
                street = "6736 George Via Suite 561",
                city = "Oakland",
                state = "CA",
                zipCode = "33601",
                studyRating = 4, // Fixed: set different studyRating
                latitude = 37.47633438941805,
                longitude = -122.41092352755216,
                phone = "2599708855",
                website = "https://www.codeandcoffeeco.com", // Fixed: removed extra dot
                amenities = listOf("Outdoor seating", "Quiet zone", "Free WiFi", "Power outlets"),
                wifiAccess = 1,
                outletAccessibility = 2,
                averageRating = 5,
                isBookmarked = true
            )
        )
    }

    // Creates "The Productive Cup"
    fun createTheProductiveCup(isBookmarked: Boolean = false): CafeEntity {
        return createTestCafeEntity(
            id = 1L,
            name = "The Productive Cup",
            street = "082 Barbara Ferry",
            city = "Palo Alto",
            state = "CA",
            zipCode = "41855",
            studyRating = 2, // Fixed: explicitly set
            latitude = 37.78502907244787,
            longitude = -122.02845225960077,
            phone = "001-590-586-9810x696",
            website = "https://www.theproductivecup.com",
            amenities = listOf("Wheelchair accessible", "Group study rooms", "Pet friendly", "Outdoor seating", "Meeting rooms"),
            wifiAccess = 2,
            outletAccessibility = 2,
            averageRating = 4,
            isBookmarked = isBookmarked
        )
    }

    /**
     * Creates cafes for searches
     */
    fun createSearchTestCafes(): List<CafeEntity> {
        return listOf(
            // Search for coffee
            createTestCafeEntity(
                id = 10L,
                name = "Coffee & Code",
                city = "Oakland",
                studyRating = 3 // Fixed: set studyRating
            ),

            // Search for express
            createTestCafeEntity(
                id = 11L,
                name = "Espresso Express",
                city = "Oakland",
                studyRating = 4 // Fixed: set studyRating
            ),

            // Search for alto
            createTestCafeEntity(
                id = 12L,
                name = "The Productive Cup",
                city = "Palo Alto",
                studyRating = 2 // Fixed: set studyRating
            )
        )
    }

    // Creates cafes for rating/wifi testing using different combinations
    fun createRatingTestCafes(): List<CafeEntity> {
        return listOf(
            // Poor WiFi, Rating 4
            createTestCafeEntity(
                id = 20L,
                name = "Coffee & Code",
                studyRating = 3, // Fixed: set studyRating different from averageRating
                wifiAccess = 1,
                averageRating = 4
            ),

            // Good WiFi, Rating 4
            createTestCafeEntity(
                id = 21L,
                name = "The Productive Cup",
                studyRating = 3, // Fixed: set studyRating
                wifiAccess = 2,
                averageRating = 4
            ),

            // Excellent WiFi, Rating 5
            createTestCafeEntity(
                id = 22L,
                name = "Espresso Express",
                studyRating = 5, // Fixed: set studyRating
                wifiAccess = 3,
                averageRating = 5
            )
        )
    }

    // Creates cafes with specific amenities for filtering tests
    fun createAmenityTestCafes(): List<CafeEntity> {
        return listOf(
            // Has parking
            createTestCafeEntity(
                id = 30L,
                name = "Coffee & Code",
                studyRating = 3, // Fixed: set studyRating
                amenities = listOf("Power outlets", "Parking available", "Meeting rooms")
            ),

            // Has study rooms
            createTestCafeEntity(
                id = 31L,
                name = "The Productive Cup",
                studyRating = 2, // Fixed: set studyRating
                amenities = listOf("Group study rooms", "Meeting rooms", "Wheelchair accessible")
            ),

            // Has quiet zone
            createTestCafeEntity(
                id = 32L,
                name = "Espresso Express",
                studyRating = 4, // Fixed: set studyRating
                amenities = listOf("Quiet zone", "24/7 access", "Power outlets")
            )
        )
    }

    // Fixed: Added all missing parameters that tests expect
    fun createSimpleTestCafe(
        id: Long = 99L,
        name: String = "Test Cafe",
        address: String = "123 Test Street, Test City, CA 12345",
        latitude: Double? = 37.76115298322381,
        longitude: Double? = -122.4787779091634,
        studyRating: Int = 2,
        averageRating: Int = 3,
        wifiAccess: Int = 2,
        outletAccessibility: Int = 2,
        tags: String = "",
        isBookmarked: Boolean = false
    ): CafeEntity {
        return CafeEntity(
            id = id,
            apiId = "test-api-$id",
            name = name,
            address = address,
            description = "A test cafe",
            latitude = latitude,
            longitude = longitude,
            phone = "555-1234",
            website = "https://test.com",
            hours = "Monday-Friday: 7AM-9PM",
            priceRange = "$$",
            tags = tags,
            studyRating = studyRating,
            ambianceRating = averageRating,
            averageRating = averageRating,
            wifiAccess = wifiAccess,
            outletAccessibility = outletAccessibility,
            wifiQuality = when (wifiAccess) {
                0 -> "None"
                1 -> "Poor"
                2 -> "Good"
                3 -> "Excellent"
                else -> "Unknown"
            },
            powerOutlets = when (outletAccessibility) {
                0 -> "None"
                1 -> "Limited"
                2 -> "Some"
                3 -> "Many"
                else -> "Unknown"
            },
            noiseLevel = "Medium",
            seatingCapacity = null,
            hasParking = false,
            hasFood = true,
            hasCoffee = true,
            hasStudyArea = true,
            imageUrl = "https://picsum.photos/400/300",
            thumbnailUrl = "https://picsum.photos/200/150",
            atmosphereTags = "",
            energyLevelTags = "",
            studyFriendlyTags = "",
            ratingImageUrls = "",
            createdAt = "2025-06-21T12:00:00.000Z",
            updatedAt = "2025-06-21T12:00:00.000Z",
            isBookmarked = isBookmarked
        )
    }
}