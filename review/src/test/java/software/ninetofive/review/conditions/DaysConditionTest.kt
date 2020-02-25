package software.ninetofive.review.conditions

import android.content.Context
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import software.ninetofive.review.util.AskForReviewSharedPreferences

class DaysConditionTest {

    lateinit var context: Context
    lateinit var preferences: AskForReviewSharedPreferences
    val daysAmount = 1

    lateinit var condition: DaysCondition

    @Before
    fun setUp() {
        context = mock()
        preferences = mock()

        condition = DaysCondition(context, daysAmount, preferences)
    }

    @Test
    fun hasConditionBeenMade_returnsTrueIfDifferenceIsHigher() {
        Mockito.`when`(preferences.getDaysTimeStampInMilliseconds()).doReturn(System.currentTimeMillis() - (daysAmount + 1) * 1000L * 60L * 60L * 24L)
        val result = condition.hasConditionBeenMade()

        assertTrue(result)
    }

}