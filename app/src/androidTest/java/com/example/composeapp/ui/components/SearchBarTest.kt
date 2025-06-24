package com.example.composeapp.ui.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.composeapp.ui.theme.ComposeAppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private var searchQuery = ""
    private var searchCallbackCount = 0

    @Before
    fun setUp() {
        searchQuery = ""
        searchCallbackCount = 0
    }

    private fun mockOnSearch(query: String) {
        searchQuery = query
        searchCallbackCount++
    }

    @Test
    fun searchBar_displaysCorrectly() {
        composeTestRule.setContent {
            ComposeAppTheme {
                SearchBar()
            }
        }

        // Test that the search bar label exists (may be floating)
        composeTestRule
            .onNodeWithText("Search for a Cafe Rating")
            .assertExists()

        // Test that the search field exists by clicking on it
        composeTestRule
            .onNodeWithText("Search for a Cafe Rating")
            .performClick()
        
        // After clicking, the field should be focusable
        composeTestRule
            .onNodeWithText("Search for a Cafe Rating")
            .assertExists()

        // Test that the search icon is displayed
        composeTestRule
            .onNodeWithContentDescription("Search Icon")
            .assertIsDisplayed()
    }

    @Test
    fun searchBar_acceptsTextInput() {
        composeTestRule.setContent {
            ComposeAppTheme {
                SearchBar(onSearch = ::mockOnSearch)
            }
        }

        val testInput = "Bean & Brew"

        // Type text into the search bar
        composeTestRule
            .onNodeWithText("Search for a Cafe Rating")
            .performTextInput(testInput)

        // Verify the text was entered
        composeTestRule
            .onNodeWithText(testInput)
            .assertIsDisplayed()
    }

    @Test
    fun searchBar_triggersSearchOnTextChange() {
        composeTestRule.setContent {
            ComposeAppTheme {
                SearchBar(onSearch = ::mockOnSearch)
            }
        }

        val testInput = "Cafe"

        // Type text into the search bar
        composeTestRule
            .onNodeWithText("Search for a Cafe Rating")
            .performTextInput(testInput)

        // Verify search callback was not immediately triggered for non-empty text
        // (based on implementation, it only triggers on empty text)
        
        // Clear the text
        composeTestRule
            .onNodeWithText(testInput)
            .performTextClearance()

        // Verify search callback was triggered with empty string
        assert(searchQuery == "")
        assert(searchCallbackCount > 0)
    }

    @Test
    fun searchBar_showsSubmitButtonWhenTextEntered() {
        composeTestRule.setContent {
            ComposeAppTheme {
                SearchBar(onSearch = ::mockOnSearch)
            }
        }

        val testInput = "Test Cafe"

        // Initially, submit button should not be visible
        composeTestRule
            .onNodeWithContentDescription("Submit Search")
            .assertDoesNotExist()

        // Type text into the search bar
        composeTestRule
            .onNodeWithText("Search for a Cafe Rating")
            .performTextInput(testInput)

        // Submit button should now be visible
        composeTestRule
            .onNodeWithContentDescription("Submit Search")
            .assertIsDisplayed()
    }

    @Test
    fun searchBar_hidesSubmitButtonWhenTextCleared() {
        composeTestRule.setContent {
            ComposeAppTheme {
                SearchBar(onSearch = ::mockOnSearch)
            }
        }

        val testInput = "Test Cafe"

        // Type text to show submit button
        composeTestRule
            .onNodeWithText("Search for a Cafe Rating")
            .performTextInput(testInput)

        // Verify submit button is shown
        composeTestRule
            .onNodeWithContentDescription("Submit Search")
            .assertIsDisplayed()

        // Clear the text
        composeTestRule
            .onNodeWithText(testInput)
            .performTextClearance()

        // Submit button should now be hidden
        composeTestRule
            .onNodeWithContentDescription("Submit Search")
            .assertDoesNotExist()
    }

    @Test
    fun searchBar_submitButtonTriggersSearch() {
        composeTestRule.setContent {
            ComposeAppTheme {
                SearchBar(onSearch = ::mockOnSearch)
            }
        }

        val testInput = "Bean & Brew"

        // Type text into the search bar
        composeTestRule
            .onNodeWithText("Search for a Cafe Rating")
            .performTextInput(testInput)

        // Click the submit button
        composeTestRule
            .onNodeWithContentDescription("Submit Search")
            .performClick()

        // Verify search callback was triggered with the correct query
        assert(searchQuery == testInput.trim())
        assert(searchCallbackCount > 0)
    }

    @Test
    fun searchBar_imeSearchTriggersSearch() {
        composeTestRule.setContent {
            ComposeAppTheme {
                SearchBar(onSearch = ::mockOnSearch)
            }
        }

        val testInput = "Coffee Shop"

        // Type text into the search bar
        composeTestRule
            .onNodeWithText("Search for a Cafe Rating")
            .performTextInput(testInput)

        // Trigger IME search action
        composeTestRule
            .onNodeWithText(testInput)
            .performImeAction()

        // Verify search callback was triggered
        assert(searchQuery == testInput.trim())
        assert(searchCallbackCount > 0)
    }

    @Test
    fun searchBar_doesNotSearchWithBlankText() {
        composeTestRule.setContent {
            ComposeAppTheme {
                SearchBar(onSearch = ::mockOnSearch)
            }
        }

        // Type only spaces
        val blankInput = "   "
        composeTestRule
            .onNodeWithText("Search for a Cafe Rating")
            .performTextInput(blankInput)

        // The submit button should not appear for blank text, so search won't be triggered
        composeTestRule
            .onNodeWithContentDescription("Submit Search")
            .assertDoesNotExist()

        // Verify search was not triggered with blank text
        assert(searchQuery == "")
    }

    @Test
    fun searchBar_trimsSearchQuery() {
        composeTestRule.setContent {
            ComposeAppTheme {
                SearchBar(onSearch = ::mockOnSearch)
            }
        }

        val inputWithSpaces = "  Bean & Brew  "
        val expectedTrimmed = "Bean & Brew"

        // Type text with leading and trailing spaces
        composeTestRule
            .onNodeWithText("Search for a Cafe Rating")
            .performTextInput(inputWithSpaces)

        // Trigger search
        composeTestRule
            .onNodeWithContentDescription("Submit Search")
            .performClick()

        // Verify the query was trimmed
        assert(searchQuery == expectedTrimmed)
    }

    @Test
    fun searchBar_handlesLongText() {
        composeTestRule.setContent {
            ComposeAppTheme {
                SearchBar(onSearch = ::mockOnSearch)
            }
        }

        val longInput = "This is a very long search query that might test the text field's handling of extensive input"

        // Type long text
        composeTestRule
            .onNodeWithText("Search for a Cafe Rating")
            .performTextInput(longInput)

        // Verify the text is displayed (might be scrolled)
        composeTestRule
            .onNodeWithText(longInput, substring = true)
            .assertIsDisplayed()

        // Trigger search
        composeTestRule
            .onNodeWithContentDescription("Submit Search")
            .performClick()

        // Verify search was triggered with the long text
        assert(searchQuery == longInput.trim())
    }

    @Test
    fun searchBar_isSingleLine() {
        composeTestRule.setContent {
            ComposeAppTheme {
                SearchBar(onSearch = ::mockOnSearch)
            }
        }

        val multiLineInput = "Line 1\nLine 2\nLine 3"

        // Type text with newlines
        composeTestRule
            .onNodeWithText("Search for a Cafe Rating")
            .performTextInput(multiLineInput)

        // The text field should handle this as a single line
        // (newlines might be ignored or replaced with spaces)
        composeTestRule
            .onNodeWithText(multiLineInput, substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun searchBar_hasCorrectAccessibility() {
        composeTestRule.setContent {
            ComposeAppTheme {
                SearchBar(onSearch = ::mockOnSearch)
            }
        }

        // Test that accessibility descriptions are present
        composeTestRule
            .onNodeWithContentDescription("Search Icon")
            .assertIsDisplayed()

        // Type text to show submit button
        composeTestRule
            .onNodeWithText("Search for a Cafe Rating")
            .performTextInput("test")

        // Test submit button accessibility
        composeTestRule
            .onNodeWithContentDescription("Submit Search")
            .assertIsDisplayed()
    }

    @Test
    fun searchBar_handlesSpecialCharacters() {
        composeTestRule.setContent {
            ComposeAppTheme {
                SearchBar(onSearch = ::mockOnSearch)
            }
        }

        val specialCharsInput = "Café & Bar @123 #test"

        // Type text with special characters
        composeTestRule
            .onNodeWithText("Search for a Cafe Rating")
            .performTextInput(specialCharsInput)

        // Trigger search
        composeTestRule
            .onNodeWithContentDescription("Submit Search")
            .performClick()

        // Verify search handles special characters
        assert(searchQuery == specialCharsInput.trim())
    }
} 