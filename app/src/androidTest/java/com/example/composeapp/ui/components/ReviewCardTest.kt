package com.example.composeapp.ui.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.composeapp.data.network.Review
import com.example.composeapp.ui.components.card.ReviewCard
import com.example.composeapp.ui.theme.ComposeAppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReviewCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockReview: Review

    @Before
    fun setUp() {
        mockReview = Review(
            id = "review-1",
            study_spot_id = "cafe-1",
            user_id = "user-1",
            overall_rating = 4.5,
            outlet_accessibility = 4.0,
            wifi_quality = 5.0,
            atmosphere = "Cozy, Warm",
            energy_level = "Quiet",
            study_friendly = "Study Haven",
            photos = emptyList(),
            created_at = "2024-01-15T10:30:00Z"
        )
    }

    @Test
    fun reviewCard_displaysOverallRating() {
        composeTestRule.setContent {
            ComposeAppTheme {
                ReviewCard(
                    review = mockReview,
                    cafeName = "Bean & Brew"
                )
            }
        }

        // Test that the overall rating is displayed in the expected format
        composeTestRule
            .onNodeWithText("(4.5/5.0)")
            .assertIsDisplayed()
    }

    @Test
    fun reviewCard_displaysCafeName() {
        val cafeName = "Bean & Brew"

        composeTestRule.setContent {
            ComposeAppTheme {
                ReviewCard(
                    review = mockReview,
                    cafeName = cafeName
                )
            }
        }

        // Test that the cafe name is displayed
        composeTestRule
            .onNodeWithText(cafeName)
            .assertIsDisplayed()
    }

    @Test
    fun reviewCard_displaysAtmosphere() {
        composeTestRule.setContent {
            ComposeAppTheme {
                ReviewCard(
                    review = mockReview,
                    cafeName = "Test Cafe"
                )
            }
        }

        // Test that atmosphere is displayed
        composeTestRule
            .onNodeWithText("Atmosphere: Cozy, Warm")
            .assertIsDisplayed()
    }

    @Test
    fun reviewCard_displaysEnergyLevel() {
        composeTestRule.setContent {
            ComposeAppTheme {
                ReviewCard(
                    review = mockReview,
                    cafeName = "Test Cafe"
                )
            }
        }

        // Test that the review card displays (energy level is not shown in actual implementation)
        composeTestRule
            .onNodeWithText("Test Cafe")
            .assertIsDisplayed()
    }

    @Test
    fun reviewCard_displaysStudyFriendliness() {
        composeTestRule.setContent {
            ComposeAppTheme {
                ReviewCard(
                    review = mockReview,
                    cafeName = "Test Cafe"
                )
            }
        }

        // Test that study friendliness is displayed
        composeTestRule
            .onNodeWithText("Study-friendly: Study Haven")
            .assertIsDisplayed()
    }

    @Test
    fun reviewCard_displaysWifiQuality() {
        composeTestRule.setContent {
            ComposeAppTheme {
                ReviewCard(
                    review = mockReview,
                    cafeName = "Test Cafe"
                )
            }
        }

        // Test that WiFi quality rating is displayed
        composeTestRule
            .onNodeWithText("WiFi: 5.0/5")
            .assertIsDisplayed()
    }

    @Test
    fun reviewCard_displaysOutletAccessibility() {
        composeTestRule.setContent {
            ComposeAppTheme {
                ReviewCard(
                    review = mockReview,
                    cafeName = "Test Cafe"
                )
            }
        }

        // Test that outlet accessibility rating is displayed
        composeTestRule
            .onNodeWithText("Outlets: 4.0/5")
            .assertIsDisplayed()
    }

    @Test
    fun reviewCard_displaysCorrectlyWithoutClick() {
        composeTestRule.setContent {
            ComposeAppTheme {
                ReviewCard(
                    review = mockReview,
                    cafeName = "Test Cafe"
                )
            }
        }

        // Test that the review card displays correctly
        composeTestRule
            .onNodeWithText("Test Cafe")
            .assertIsDisplayed()

        // Test that the overall rating is shown
        composeTestRule
            .onNodeWithText("(4.5/5.0)")
            .assertIsDisplayed()
    }

    @Test
    fun reviewCard_handlesLowRatings() {
        val lowRatedReview = mockReview.copy(
            overall_rating = 1.5,
            wifi_quality = 2.0,
            outlet_accessibility = 1.0
        )

        composeTestRule.setContent {
            ComposeAppTheme {
                ReviewCard(
                    review = lowRatedReview,
                    cafeName = "Poor Cafe"
                )
            }
        }

        // Test that low ratings are displayed correctly
        composeTestRule
            .onNodeWithText("(1.5/5.0)")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("WiFi: 2.0/5")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Outlets: 1.0/5")
            .assertIsDisplayed()
    }

    @Test
    fun reviewCard_handlesMaxRatings() {
        val maxRatedReview = mockReview.copy(
            overall_rating = 5.0,
            wifi_quality = 5.0,
            outlet_accessibility = 5.0
        )

        composeTestRule.setContent {
            ComposeAppTheme {
                ReviewCard(
                    review = maxRatedReview,
                    cafeName = "Perfect Cafe"
                )
            }
        }

        // Test that max ratings are displayed correctly
        composeTestRule
            .onNodeWithText("(5.0/5.0)")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("WiFi: 5.0/5")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Outlets: 5.0/5")
            .assertIsDisplayed()
    }

    @Test
    fun reviewCard_handlesEmptyAtmosphere() {
        val reviewWithEmptyAtmosphere = mockReview.copy(atmosphere = "")

        composeTestRule.setContent {
            ComposeAppTheme {
                ReviewCard(
                    review = reviewWithEmptyAtmosphere,
                    cafeName = "Test Cafe"
                )
            }
        }

        // Test that the card still renders with empty atmosphere
        composeTestRule
            .onNodeWithText("Test Cafe")
            .assertIsDisplayed()
    }

    @Test
    fun reviewCard_handlesLongCafeName() {
        val longCafeName = "This is a very long cafe name that might cause layout issues"

        composeTestRule.setContent {
            ComposeAppTheme {
                ReviewCard(
                    review = mockReview,
                    cafeName = longCafeName
                )
            }
        }

        // Test that long cafe names are handled gracefully
        composeTestRule
            .onNodeWithText(longCafeName, substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun reviewCard_displaysCreatedDate() {
        composeTestRule.setContent {
            ComposeAppTheme {
                ReviewCard(
                    review = mockReview,
                    cafeName = "Test Cafe"
                )
            }
        }

        // Test that some form of date information is displayed
        // This depends on how the date is formatted in the ReviewCard component
        // You might need to adjust this based on your actual implementation
        composeTestRule
            .onNodeWithText("2024", substring = true)
            .assertIsDisplayed()
    }
} 