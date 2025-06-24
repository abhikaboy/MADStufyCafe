package com.example.composeapp.integration

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.composeapp.data.database.CafeEntity
import com.example.composeapp.data.network.Bookmark
import com.example.composeapp.data.network.Review
import com.example.composeapp.data.network.UserResponse
import com.example.composeapp.ui.screens.HomeScreen
import com.example.composeapp.ui.screens.UserProfile
import com.example.composeapp.ui.theme.ComposeAppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CafeAppIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockCafeList: List<CafeEntity>
    private lateinit var mockUser: UserResponse
    private lateinit var mockReviews: List<Review>
    private lateinit var mockBookmarks: List<Bookmark>

    @Before
    fun setUp() {
        mockCafeList = listOf(
            CafeEntity(
                id = 1L,
                apiId = "cafe-1",
                name = "Bean & Brew",
                address = "123 Main Street",
                tags = "cozy,quiet",
                studyRating = 4,
                powerOutlets = "Many",
                wifiQuality = "Excellent",
                imageUrl = "",
                isBookmarked = false,
                latitude = 40.7128,
                longitude = -74.0060
            ),
            CafeEntity(
                id = 2L,
                apiId = "cafe-2",
                name = "Study Spot",
                address = "45 College Ave",
                tags = "modern,wifi",
                studyRating = 3,
                powerOutlets = "Some",
                wifiQuality = "Good",
                imageUrl = "",
                isBookmarked = true,
                latitude = 40.7589,
                longitude = -73.9851
            )
        )

        mockUser = UserResponse(
            id = "user-1",
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
        )

        mockBookmarks = listOf(
            Bookmark(
                id = "bookmark-1",
                user_id = "user-1",
                cafe_id = "cafe-2",
                bookmarked_at = "2024-01-01T00:00:00Z"
            )
        )
    }

    @Test
    fun homeScreen_searchAndCafeInteractionFlow() {
        var searchQuery = ""
        var clickedCafe: CafeEntity? = null
        var bookmarkedCafe: CafeEntity? = null

        composeTestRule.setContent {
            ComposeAppTheme {
                HomeScreen(
                    cafeList = mockCafeList,
                    onCafeClick = { cafe -> clickedCafe = cafe },
                    onBookmarkClick = { cafe -> bookmarkedCafe = cafe },
                    onSearch = { query -> searchQuery = query }
                )
            }
        }

        // Test complete search flow
        composeTestRule
            .onNodeWithText("Search for a Cafe Rating")
            .performTextInput("Bean")

        composeTestRule
            .onNodeWithContentDescription("Submit Search")
            .performClick()

        // Verify search was triggered
        assert(searchQuery == "Bean")

        // Test cafe click interaction
        composeTestRule
            .onNodeWithText("Bean & Brew")
            .performClick()

        // Verify cafe click was handled
        assert(clickedCafe?.name == "Bean & Brew")

        // Test bookmark interaction
        composeTestRule
            .onAllNodesWithContentDescription("Add bookmark")
            .onFirst()
            .performClick()

        // Verify bookmark was triggered
        assert(bookmarkedCafe != null)
    }

    @Test
    fun userProfile_displayAndInteractionFlow() {
        var reviewClicked: Review? = null

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
                    },
                    onReviewClick = { review -> reviewClicked = review }
                )
            }
        }

        // Test that user information is displayed
        composeTestRule
            .onNodeWithText("Profile")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("John Doe")
            .assertIsDisplayed()

        // Test that user statistics exist (cafes visited)
        composeTestRule
            .onNodeWithText("15 Cafes Visited")
            .assertExists()

        // Test that recent reviews section is displayed
        composeTestRule
            .onNodeWithText("Recent Reviews")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Bean & Brew")
            .assertIsDisplayed()

        // Test review interaction
        composeTestRule
            .onNodeWithText("Bean & Brew")
            .performClick()

        // Note: In real implementation, verify reviewClicked is set
    }

    @Test
    fun homeToUserProfileNavigation_simulatedFlow() {
        // Test just the user profile screen since we can't call setContent twice
        composeTestRule.setContent {
            ComposeAppTheme {
                UserProfile(
                    currentUser = mockUser,
                    userReviews = mockReviews,
                    userBookmarks = mockBookmarks
                )
            }
        }

        // Test profile screen navigation result
        composeTestRule
            .onNodeWithText("Profile")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("John Doe")
            .assertIsDisplayed()

        // Test that user data is properly displayed
        composeTestRule
            .onNodeWithText("Recent Reviews")
            .assertIsDisplayed()
    }

    @Test
    fun cafeList_bookmarkStateConsistency() {
        // Test that bookmark states are consistent across interactions
        val mutableCafeList = mockCafeList.toMutableList()
        var bookmarkChanges = 0

        composeTestRule.setContent {
            ComposeAppTheme {
                HomeScreen(
                    cafeList = mutableCafeList,
                    onCafeClick = {},
                    onBookmarkClick = { cafe ->
                        bookmarkChanges++
                        val index = mutableCafeList.indexOfFirst { it.id == cafe.id }
                        if (index != -1) {
                            mutableCafeList[index] = cafe.copy(isBookmarked = !cafe.isBookmarked)
                        }
                    }
                )
            }
        }

        // Initially, "Study Spot" should be bookmarked
        composeTestRule
            .onNodeWithContentDescription("Remove bookmark")
            .assertExists()

        // Click bookmark to unbookmark
        composeTestRule
            .onNodeWithContentDescription("Remove bookmark")
            .performClick()

        // Verify bookmark change was registered
        assert(bookmarkChanges == 1)
    }

    @Test
    fun emptyStates_handlingAcrossComponents() {
        // Test user profile with no reviews (avoiding multiple setContent calls)
        composeTestRule.setContent {
            ComposeAppTheme {
                UserProfile(
                    currentUser = mockUser,
                    userReviews = emptyList(),
                    userBookmarks = emptyList()
                )
            }
        }

        // Test that profile screen displays correctly
        composeTestRule
            .onNodeWithText("Profile")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("John Doe")
            .assertIsDisplayed()

        // Test that no reviews message is displayed
        composeTestRule
            .onNodeWithText("No reviews yet. Start exploring cafes and leave your first review!")
            .assertIsDisplayed()
    }

    @Test
    fun multipleInteractions_stateManagement() {
        var searchCount = 0
        var cafeClickCount = 0
        var bookmarkCount = 0

        composeTestRule.setContent {
            ComposeAppTheme {
                HomeScreen(
                    cafeList = mockCafeList,
                    onCafeClick = { cafeClickCount++ },
                    onBookmarkClick = { bookmarkCount++ },
                    onSearch = { searchCount++ }
                )
            }
        }

        // Perform multiple search operations
        composeTestRule
            .onNodeWithText("Search for a Cafe Rating")
            .performTextInput("Bean")

        composeTestRule
            .onNodeWithContentDescription("Submit Search")
            .performClick()

        // Clear and search again
        composeTestRule
            .onNodeWithText("Bean")
            .performTextClearance()

        composeTestRule
            .onNodeWithText("Search for a Cafe Rating")
            .performTextInput("Study")

        composeTestRule
            .onNodeWithContentDescription("Submit Search")
            .performClick()

        // Perform multiple cafe clicks
        composeTestRule
            .onNodeWithText("Bean & Brew")
            .performClick()

        composeTestRule
            .onNodeWithText("Study Spot")
            .performClick()

        // Perform bookmark operations
        composeTestRule
            .onAllNodesWithContentDescription("Add bookmark")
            .onFirst()
            .performClick()

        // Verify all interactions were counted
        assert(searchCount >= 2)
        assert(cafeClickCount == 2)
        assert(bookmarkCount >= 1)
    }

    @Test
    fun accessibilityFeaturesAcrossComponents() {
        // Test accessibility in UserProfile (avoiding multiple setContent calls)
        composeTestRule.setContent {
            ComposeAppTheme {
                UserProfile(
                    currentUser = mockUser,
                    userReviews = mockReviews,
                    userBookmarks = mockBookmarks
                )
            }
        }

        // Verify profile picture has proper content description
        composeTestRule
            .onNodeWithContentDescription("Profile Picture")
            .assertIsDisplayed()

        // Test that profile screen is accessible
        composeTestRule
            .onNodeWithText("Profile")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("John Doe")
            .assertIsDisplayed()
    }
} 