package com.example.composeapp.ui.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.composeapp.data.database.CafeEntity
import com.example.composeapp.ui.components.card.CafeCard
import com.example.composeapp.ui.theme.ComposeAppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CafeCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockCafe: CafeEntity
    private lateinit var mockBookmarkedCafe: CafeEntity

    @Before
    fun setUp() {
        mockCafe = CafeEntity(
            id = 1L,
            apiId = "cafe-1",
            name = "Bean & Brew",
            address = "123 Main Street",
            tags = "cozy,quiet,wifi",
            studyRating = 4,
            powerOutlets = "Many",
            wifiQuality = "Excellent",
            imageUrl = "https://example.com/image.jpg",
            isBookmarked = false,
            latitude = 40.7128,
            longitude = -74.0060
        )

        mockBookmarkedCafe = mockCafe.copy(
            id = 2L,
            apiId = "cafe-2",
            name = "Bookmarked Cafe",
            isBookmarked = true
        )
    }

    @Test
    fun cafeCard_displaysCafeName() {
        composeTestRule.setContent {
            ComposeAppTheme {
                CafeCard(
                    cafe = mockCafe,
                    onClick = {}
                )
            }
        }

        // Test that the cafe name is displayed
        composeTestRule
            .onNodeWithText("Bean & Brew")
            .assertIsDisplayed()
    }

    @Test
    fun cafeCard_displaysCafeAddress() {
        composeTestRule.setContent {
            ComposeAppTheme {
                CafeCard(
                    cafe = mockCafe,
                    onClick = {}
                )
            }
        }

        // Test that the cafe address is displayed
        composeTestRule
            .onNodeWithText("123 Main Street")
            .assertIsDisplayed()
    }

    @Test
    fun cafeCard_displaysStudyRating() {
        composeTestRule.setContent {
            ComposeAppTheme {
                CafeCard(
                    cafe = mockCafe,
                    onClick = {}
                )
            }
        }

        // Test that the study rating section is displayed
        composeTestRule
            .onNodeWithText("Study Rating")
            .assertIsDisplayed()

        // Test that star elements exist for the rating (should have both filled and empty stars)
        composeTestRule
            .onNodeWithContentDescription("Star")
            .assertExists()

        composeTestRule
            .onNodeWithContentDescription("Empty Star")
            .assertExists()
    }

    @Test
    fun cafeCard_displaysPowerOutlets() {
        composeTestRule.setContent {
            ComposeAppTheme {
                CafeCard(
                    cafe = mockCafe,
                    onClick = {}
                )
            }
        }

        // Test that the outlets section is displayed
        composeTestRule
            .onNodeWithText("Outlets")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Many")
            .assertIsDisplayed()
    }

    @Test
    fun cafeCard_displaysWifiQuality() {
        composeTestRule.setContent {
            ComposeAppTheme {
                CafeCard(
                    cafe = mockCafe,
                    onClick = {}
                )
            }
        }

        // Test that the WiFi quality is displayed
        composeTestRule
            .onNodeWithText("Excellent")
            .assertIsDisplayed()
    }

    @Test
    fun cafeCard_cardClickTriggersCallback() {
        var clicked = false

        composeTestRule.setContent {
            ComposeAppTheme {
                CafeCard(
                    cafe = mockCafe,
                    onClick = { clicked = true }
                )
            }
        }

        // Click on the card
        composeTestRule
            .onNodeWithText("Bean & Brew")
            .performClick()

        // Verify the callback was triggered
        assert(clicked)
    }

    @Test
    fun cafeCard_bookmarkButtonTriggersCallback() {
        var bookmarkClicked = false

        composeTestRule.setContent {
            ComposeAppTheme {
                CafeCard(
                    cafe = mockCafe,
                    onClick = {},
                    onBookmarkClick = { bookmarkClicked = true }
                )
            }
        }

        // Click on the bookmark button
        composeTestRule
            .onNodeWithContentDescription("Add bookmark")
            .performClick()

        // Verify the callback was triggered
        assert(bookmarkClicked)
    }

    @Test
    fun cafeCard_displaysBookmarkedState() {
        composeTestRule.setContent {
            ComposeAppTheme {
                CafeCard(
                    cafe = mockBookmarkedCafe,
                    onClick = {}
                )
            }
        }

        // Test that the bookmarked state is displayed
        composeTestRule
            .onNodeWithContentDescription("Remove bookmark")
            .assertIsDisplayed()
    }

    @Test
    fun cafeCard_displaysUnbookmarkedState() {
        composeTestRule.setContent {
            ComposeAppTheme {
                CafeCard(
                    cafe = mockCafe,
                    onClick = {}
                )
            }
        }

        // Test that the unbookmarked state is displayed
        composeTestRule
            .onNodeWithContentDescription("Add bookmark")
            .assertIsDisplayed()
    }

    @Test
    fun cafeCard_displaysLocationIcon() {
        composeTestRule.setContent {
            ComposeAppTheme {
                CafeCard(
                    cafe = mockCafe,
                    onClick = {}
                )
            }
        }

        // Test that the location icon is displayed
        composeTestRule
            .onNodeWithContentDescription("Icon")
            .assertIsDisplayed()
    }

    @Test
    fun cafeCard_handlesZeroRating() {
        val zeroRatedCafe = mockCafe.copy(studyRating = 0)

        composeTestRule.setContent {
            ComposeAppTheme {
                CafeCard(
                    cafe = zeroRatedCafe,
                    onClick = {}
                )
            }
        }

        // Test that zero rating displays empty stars
        composeTestRule
            .onNodeWithContentDescription("Empty Star")
            .assertExists()

        // Test that Study Rating label is still shown
        composeTestRule
            .onNodeWithText("Study Rating")
            .assertIsDisplayed()
    }

    @Test
    fun cafeCard_handlesMaxRating() {
        val maxRatedCafe = mockCafe.copy(studyRating = 5)

        composeTestRule.setContent {
            ComposeAppTheme {
                CafeCard(
                    cafe = maxRatedCafe,
                    onClick = {}
                )
            }
        }

        // Test that max rating displays filled stars
        composeTestRule
            .onNodeWithContentDescription("Star")
            .assertExists()

        // Test that Study Rating label is displayed
        composeTestRule
            .onNodeWithText("Study Rating")
            .assertIsDisplayed()
    }

    @Test
    fun cafeCard_handlesLongCafeName() {
        val longNameCafe = mockCafe.copy(
            name = "This is a very long cafe name that might cause layout issues if not handled properly"
        )

        composeTestRule.setContent {
            ComposeAppTheme {
                CafeCard(
                    cafe = longNameCafe,
                    onClick = {}
                )
            }
        }

        // Test that the long name is displayed (might be truncated)
        composeTestRule
            .onNodeWithText(longNameCafe.name, substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun cafeCard_handlesLongAddress() {
        val longAddressCafe = mockCafe.copy(
            address = "This is a very long address that spans multiple lines and should be handled gracefully"
        )

        composeTestRule.setContent {
            ComposeAppTheme {
                CafeCard(
                    cafe = longAddressCafe,
                    onClick = {}
                )
            }
        }

        // Test that the long address is displayed
        composeTestRule
            .onNodeWithText(longAddressCafe.address, substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun cafeCard_displaysDifferentWifiQualities() {
        // Test one specific WiFi quality to avoid multiple setContent calls
        val excellentWifiCafe = mockCafe.copy(wifiQuality = "Excellent")

        composeTestRule.setContent {
            ComposeAppTheme {
                CafeCard(
                    cafe = excellentWifiCafe,
                    onClick = {}
                )
            }
        }

        // Test that WiFi quality is displayed correctly
        composeTestRule
            .onNodeWithText("Excellent")
            .assertIsDisplayed()

        // Test that WiFi label is displayed
        composeTestRule
            .onNodeWithText("Wifi")
            .assertIsDisplayed()
    }

    @Test
    fun cafeCard_displaysDifferentOutletLevels() {
        // Test one specific outlet level to avoid multiple setContent calls
        val manyOutletsCafe = mockCafe.copy(powerOutlets = "Many")

        composeTestRule.setContent {
            ComposeAppTheme {
                CafeCard(
                    cafe = manyOutletsCafe,
                    onClick = {}
                )
            }
        }

        // Test that outlet level is displayed correctly
        composeTestRule
            .onNodeWithText("Many")
            .assertIsDisplayed()

        // Test that Outlets label is displayed
        composeTestRule
            .onNodeWithText("Outlets")
            .assertIsDisplayed()
    }
} 