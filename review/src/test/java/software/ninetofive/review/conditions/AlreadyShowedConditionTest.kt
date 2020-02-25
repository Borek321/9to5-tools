package software.ninetofive.review.conditions

import android.content.Context
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import software.ninetofive.review.util.AskForReviewSharedPreferences

class AlreadyShowedConditionTest {

    lateinit var context: Context
    lateinit var preferences: AskForReviewSharedPreferences

    lateinit var condition: AlreadyShowedCondition

    @Before
    fun setUp() {
        context = mock()
        preferences = mock()

        condition = AlreadyShowedCondition(context, preferences)
    }

    @Test
    fun hasConditionBeenMade_returnsSameAsPreferences() {
        val expected = true
        Mockito.`when`(preferences.getAlreadyShowed()).doReturn(expected)
        val result = condition.hasConditionBeenMade()

        assertEquals(expected, result)
    }

}