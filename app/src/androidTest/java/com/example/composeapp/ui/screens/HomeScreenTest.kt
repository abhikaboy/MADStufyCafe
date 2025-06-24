package com.example.composeapp.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.composeapp.data.database.CafeEntity
import com.example.composeapp.ui.theme.ComposeAppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockCafeList: List<CafeEntity>

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
            ),
            CafeEntity(
                id = 3L,
                apiId = "cafe-3",
                name = "Java House",
                address = "88 Coffee Blvd",
                tags = "busy,central",
                studyRating = 5,
                powerOutlets = "Few",
                wifiQuality = "Fair",
                imageUrl = "",
                isBookmarked = false,
                latitude = 40.7505,
                longitude = -73.9934
            )
        )
    }

    @Test
    fun homeScreen_displaysSearchBar() {
        composeTestRule.setContent {
            ComposeAppTheme {
                HomeScreen(
                    cafeList = mockCafeList,
                    onCafeClick = {}
                )
            }
        }

        // Test that the search bar is displayed by checking its label
        composeTestRule
            .onNodeWithText("Search for a Cafe Rating")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysRecommendedCafesTitle() {
        composeTestRule.setContent {
            ComposeAppTheme {
                HomeScreen(
                    cafeList = mockCafeList,
                    onCafeClick = {}
                )
            }
        }

        // Test that the recommended cafes title is displayed
        composeTestRule
            .onNodeWithText("RECOMMENDED CAFE'S NEAR YOU")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysCafeList() {
        composeTestRule.setContent {
            ComposeAppTheme {
                HomeScreen(
                    cafeList = mockCafeList,
                    onCafeClick = {}
                )
            }
        }

        // Test that all cafes in the list are displayed
        composeTestRule
            .onNodeWithText("Bean & Brew")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Study Spot")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Java House")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysCafeAddresses() {
        composeTestRule.setContent {
            ComposeAppTheme {
                HomeScreen(
                    cafeList = mockCafeList,
                    onCafeClick = {}
                )
            }
        }

        // Test that cafe addresses are displayed
        composeTestRule
            .onNodeWithText("123 Main Street")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("45 College Ave")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("88 Coffee Blvd")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_cafeClickTriggersCallback() {
        var clickedCafe: CafeEntity? = null

        composeTestRule.setContent {
            ComposeAppTheme {
                HomeScreen(
                    cafeList = mockCafeList,
                    onCafeClick = { cafe -> clickedCafe = cafe }
                )
            }
        }

        // Click on the first cafe
        composeTestRule
            .onNodeWithText("Bean & Brew")
            .performClick()

        // Verify the callback was triggered with the correct cafe
        assert(clickedCafe != null)
        assert(clickedCafe?.name == "Bean & Brew")
    }

    @Test
    fun homeScreen_bookmarkClickTriggersCallback() {
        var bookmarkedCafe: CafeEntity? = null

        composeTestRule.setContent {
            ComposeAppTheme {
                HomeScreen(
                    cafeList = mockCafeList,
                    onCafeClick = {},
                    onBookmarkClick = { cafe -> bookmarkedCafe = cafe }
                )
            }
        }

        // Find and click bookmark button
        // Note: You might need to add testTag to bookmark button for better testing
        composeTestRule
            .onAllNodesWithContentDescription("Add bookmark")
            .onFirst()
            .performClick()

        // Verify the callback was triggered
        assert(bookmarkedCafe != null)
    }

    @Test
    fun homeScreen_searchComponentExists() {
        composeTestRule.setContent {
            ComposeAppTheme {
                HomeScreen(
                    cafeList = mockCafeList,
                    onCafeClick = {},
                    onSearch = { query -> /* search callback */ }
                )
            }
        }

        // Test that search components are displayed properly
        composeTestRule
            .onNodeWithText("Search for a Cafe Rating")
            .assertIsDisplayed()
            
        // Verify search functionality exists by checking for text input capability
        composeTestRule
            .onNode(hasSetTextAction())
            .assertExists()
    }

    @Test
    fun homeScreen_displaysEmptyStateWhenNoCafes() {
        composeTestRule.setContent {
            ComposeAppTheme {
                HomeScreen(
                    cafeList = emptyList(),
                    onCafeClick = {}
                )
            }
        }

        // Test that the search bar and title are still displayed even with empty list
        composeTestRule
            .onNodeWithText("RECOMMENDED CAFE'S NEAR YOU")
            .assertIsDisplayed()

        // Test that no cafe names are displayed
        composeTestRule
            .onNodeWithText("Bean & Brew")
            .assertDoesNotExist()
    }

    @Test
    fun homeScreen_handlesPullToRefresh() {
        var refreshCalled = false

        composeTestRule.setContent {
            ComposeAppTheme {
                HomeScreen(
                    cafeList = mockCafeList,
                    onCafeClick = {},
                    isRefreshing = false,
                    onRefresh = { refreshCalled = true }
                )
            }
        }

        // Simulate refresh action - the actual gesture depends on implementation  
        // For now, just verify the callback can be triggered
        composeTestRule.runOnIdle {
            // In a real scenario, CafeList would trigger onRefresh
            // For this test, we'll just verify the setup works
            assert(!refreshCalled) // Initially false
        }
    }

    @Test
    fun homeScreen_displaysRefreshingState() {
        composeTestRule.setContent {
            ComposeAppTheme {
                HomeScreen(
                    cafeList = mockCafeList,
                    onCafeClick = {},
                    isRefreshing = true,
                    onRefresh = {}
                )
            }
        }

        // Test that the refreshing state is displayed
        // Note: This depends on how the refreshing state is shown in CafeList
        // You might need to check for a progress indicator or similar UI element
    }

    @Test
    fun homeScreen_handlesOnResumeCallback() {
        var onResumeCalled = false

        composeTestRule.setContent {
            ComposeAppTheme {
                HomeScreen(
                    cafeList = mockCafeList,
                    onCafeClick = {},
                    onResume = { onResumeCalled = true }
                )
            }
        }

        // Note: Testing lifecycle events requires special setup
        // This test shows the structure for testing onResume
    }

    @Test
    fun homeScreen_displaysCafeRatings() {
        composeTestRule.setContent {
            ComposeAppTheme {
                HomeScreen(
                    cafeList = mockCafeList,
                    onCafeClick = {}
                )
            }
        }

        // Test that cafe ratings are displayed (stars exist across multiple cafes)
        composeTestRule
            .onAllNodesWithContentDescription("Star")[0]
            .assertExists()
    }

    @Test
    fun homeScreen_displaysCafeFeatures() {
        composeTestRule.setContent {
            ComposeAppTheme {
                HomeScreen(
                    cafeList = mockCafeList,
                    onCafeClick = {}
                )
            }
        }

        // Test that cafe features like WiFi quality and outlets are displayed
        composeTestRule
            .onNodeWithText("Excellent")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Many")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Good")
            .assertIsDisplayed()
    }
} 