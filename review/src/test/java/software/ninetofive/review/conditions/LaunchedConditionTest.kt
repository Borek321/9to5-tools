package software.ninetofive.review.conditions

import android.content.Context
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import software.ninetofive.review.util.AskForReviewSharedPreferences

class LaunchedConditionTest {

    lateinit var context: Context
    val launchAmount = 10
    lateinit var preferences: AskForReviewSharedPreferences

    lateinit var condition: LaunchedCondition

    @Before
    fun setUp() {
        context = mock()
        preferences = mock()

        condition = LaunchedCondition(context, launchAmount, preferences)
    }

    @Test
    fun hasConditionBeenMade_returnsTrueIfLaunchCountIsSameAsLauncAmount() {
        val launchCount = 10
        Mockito.`when`(preferences.getLaunchCount()).doReturn(launchCount)
        val result = condition.hasConditionBeenMade()

        assertTrue(result)
    }

    @Test
    fun hasConditionBeenMade_returnsFalseIfLaunchCountIsNotSameAsLauncAmount() {
        val launchCount = 2
        Mockito.`when`(preferences.getLaunchCount()).doReturn(launchCount)
        val result = condition.hasConditionBeenMade()

        assertFalse(result)
    }


}