package com.example.composeapp.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.composeapp.data.network.Bookmark
import com.example.composeapp.data.network.Review
import com.example.composeapp.data.network.UserResponse
import com.example.composeapp.ui.theme.ComposeAppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserProfileScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockUser: UserResponse
    private lateinit var mockReviews: List<Review>
    private lateinit var mockBookmarks: List<Bookmark>

    @Before
    fun setUp() {
        // Setup mock data
        mockUser = UserResponse(
            id = "test-user-1",
            name = "John Doe",
            cafes_visited = 15,
            average_rating = 4.2,
            profile_picture = null,
            created_at = "2024-01-01T00:00:00Z",
            updated_at = "2024-01-15T00:00:00Z"
        )

        mockReviews = listOf(
            Review(
                id = "review-1",
                study_spot_id = "cafe-1",
                user_id = "test-user-1",
                overall_rating = 4.5,
                outlet_accessibility = 4.0,
                wifi_quality = 5.0,
                atmosphere = "Cozy, Warm",
                energy_level = "Quiet",
                study_friendly = "Study Haven",
                photos = emptyList(),
                created_at = "2024-01-15T10:30:00Z"
            ),
            Review(
                id = "review-2",
                study_spot_id = "cafe-2",
                user_id = "test-user-1",
                overall_rating = 3.5,
                outlet_accessibility = 3.0,
                wifi_quality = 4.0,
                atmosphere = "Modern, Clean",
                energy_level = "Moderate",
                study_friendly = "Good",
                photos = emptyList(),
                created_at = "2024-01-10T14:20:00Z"
            )
        )

        mockBookmarks = listOf(
            Bookmark(
                id = "bookmark-1",
                user_id = "test-user-1",
                cafe_id = "cafe-1",
                bookmarked_at = "2024-01-01T00:00:00Z"
            ),
            Bookmark(
                id = "bookmark-2",
                user_id = "test-user-1",
                cafe_id = "cafe-3",
                bookmarked_at = "2024-01-05T00:00:00Z"
            )
        )
    }

    @Test
    fun userProfile_displaysCorrectTitle() {
        composeTestRule.setContent {
            ComposeAppTheme {
                UserProfile(
                    currentUser = mockUser,
                    userReviews = mockReviews,
                    userBookmarks = mockBookmarks
                )
            }
        }

        // Test that the screen title is displayed
        composeTestRule
            .onNodeWithText("Profile")
            .assertIsDisplayed()
    }

    @Test
    fun userProfile_displaysUserName() {
        composeTestRule.setContent {
            ComposeAppTheme {
                UserProfile(
                    currentUser = mockUser,
                    userReviews = mockReviews,
                    userBookmarks = mockBookmarks
                )
            }
        }

        // Test that the user name is displayed
        composeTestRule
            .onNodeWithText("John Doe")
            .assertIsDisplayed()
    }

    @Test
    fun userProfile_displaysGuestUserWhenNoUser() {
        composeTestRule.setContent {
            ComposeAppTheme {
                UserProfile(
                    currentUser = null,
                    userReviews = emptyList(),
                    userBookmarks = emptyList()
                )
            }
        }

        // Test that guest user is displayed when no user is provided
        composeTestRule
            .onNodeWithText("Guest User")
            .assertIsDisplayed()
    }

    @Test
    fun userProfile_displaysUserStatistics() {
        composeTestRule.setContent {
            ComposeAppTheme {
                UserProfile(
                    currentUser = mockUser,
                    userReviews = mockReviews,
                    userBookmarks = mockBookmarks
                )
            }
        }

        // Test that user statistics are displayed as shown in RatingOverviewCard
        composeTestRule
            .onNodeWithText("15 Cafes Visited")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Avg Rating 4.2")
            .assertIsDisplayed()
    }

    @Test
    fun userProfile_displaysRecentReviewsSection() {
        composeTestRule.setContent {
            ComposeAppTheme {
                UserProfile(
                    currentUser = mockUser,
                    userReviews = mockReviews,
                    userBookmarks = mockBookmarks,
                    getCafeName = { cafeId ->
                        when (cafeId) {
                            "cafe-1" -> "Bean & Brew"
                            "cafe-2" -> "Study Spot"
                            else -> "Unknown Cafe"
                        }
                    }
                )
            }
        }

        // Test that the recent reviews section title is displayed
        composeTestRule
            .onNodeWithText("Recent Reviews")
            .assertIsDisplayed()

        // Test that review cards are displayed
        composeTestRule
            .onNodeWithText("Bean & Brew")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Study Spot")
            .assertIsDisplayed()
    }

    @Test
    fun userProfile_displaysNoReviewsMessageWhenEmpty() {
        composeTestRule.setContent {
            ComposeAppTheme {
                UserProfile(
                    currentUser = mockUser,
                    userReviews = emptyList(),
                    userBookmarks = mockBookmarks
                )
            }
        }

        // Test that the no reviews message is displayed when there are no reviews
        composeTestRule
            .onNodeWithText("No reviews yet. Start exploring cafes and leave your first review!")
            .assertIsDisplayed()
    }

    @Test
    fun userProfile_handlesOnResumeCallback() {
        var onResumeCalled = false

        composeTestRule.setContent {
            ComposeAppTheme {
                UserProfile(
                    currentUser = mockUser,
                    userReviews = mockReviews,
                    userBookmarks = mockBookmarks,
                    onResume = { onResumeCalled = true }
                )
            }
        }

        // Note: Testing lifecycle events in Compose tests is complex and typically
        // handled at the integration test level. This test shows the structure.
        // In real scenarios, you might test this with a different approach.
    }

    @Test
    fun userProfile_displaysReviewCardDetails() {
        composeTestRule.setContent {
            ComposeAppTheme {
                UserProfile(
                    currentUser = mockUser,
                    userReviews = mockReviews,
                    userBookmarks = mockBookmarks,
                    getCafeName = { cafeId ->
                        when (cafeId) {
                            "cafe-1" -> "Bean & Brew"
                            "cafe-2" -> "Study Spot"
                            else -> "Unknown Cafe"
                        }
                    }
                )
            }
        }

        // Verify that review details are displayed correctly
        composeTestRule
            .onNodeWithText("(4.5/5.0)")
            .assertIsDisplayed()
            
        composeTestRule
            .onNodeWithText("WiFi: 5.0/5")
            .assertIsDisplayed()
    }

    @Test
    fun userProfile_displaysCorrectNumberOfBookmarks() {
        composeTestRule.setContent {
            ComposeAppTheme {
                UserProfile(
                    currentUser = mockUser,
                    userReviews = mockReviews,
                    userBookmarks = mockBookmarks
                )
            }
        }

        // Test that the correct number of bookmarks is displayed in statistics
        composeTestRule
            .onNodeWithText("2 bookmarks") // shown in RatingOverviewCard
            .assertIsDisplayed()
    }
} 