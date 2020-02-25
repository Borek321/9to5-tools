package software.ninetofive.review

import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import software.ninetofive.review.conditions.AskForReviewCondition
import software.ninetofive.review.util.AskForReviewSharedPreferences

class AskForReviewTest {

    lateinit var preferences: AskForReviewSharedPreferences

    lateinit var askForReview: AskForReview

    @Before
    fun setUp() {
        preferences = mock()
        askForReview = AskForReview()
    }

    @Test
    fun initialize_callsCorrectPreferenceFunctions() {
        val conditions: List<AskForReviewCondition> = listOf(mock())
        askForReview.initialize(conditions, preferences)

        verify(preferences).incrementLaunchCount()
        verify(preferences).setDays(any())
    }

    @Test
    fun canShowDialog_returnsTrueIfAllConditionsHaveBeenMade() {
        askForReview.initialize(listOf(
            conditionMock(true),
            conditionMock(true),
            conditionMock(true),
            conditionMock(true),
            conditionMock(true)
        ), preferences)
        val result = askForReview.canShowDialog()

        assertTrue(result)
    }

    @Test
    fun canShowDialog_returnsTrueIfNoConditions() {
        askForReview.initialize(listOf(), preferences)
        val result = askForReview.canShowDialog()

        assertTrue(result)
    }

    @Test
    fun canShowDialog_returnsFalseIfAConditionIsNotMet() {
        askForReview.initialize(listOf(
            conditionMock(true),
            conditionMock(true),
            conditionMock(false),
            conditionMock(true),
            conditionMock(true)
        ), preferences)
        askForReview.initialize(listOf(), preferences)
        val result = askForReview.canShowDialog()

        assertTrue(result)
    }

    @Test
    fun showDialog_doesNothingIfAConditionIsNotMet() {
        askForReview.initialize(listOf(
            conditionMock(true),
            conditionMock(true),
            conditionMock(false),
            conditionMock(true),
            conditionMock(true)
        ), preferences)
        askForReview.showDialog(mock()) { fail() }

        verify(preferences, times(0)).setAlreadyShowed()
    }

    private fun conditionMock(made: Boolean): AskForReviewCondition {
        return object : AskForReviewCondition {
            override fun hasConditionBeenMade(): Boolean {
                return made
            }
        }
    }

}