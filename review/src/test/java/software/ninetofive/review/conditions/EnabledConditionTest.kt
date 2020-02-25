package software.ninetofive.review.conditions

import org.junit.Assert.assertEquals
import org.junit.Test

class EnabledConditionTest {

    @Test
    fun hasConditionsBeenMade_returnsIsEnabled() {
        val isEnabled = false
        val condition = EnabledCondition(isEnabled)

        assertEquals(isEnabled, condition.hasConditionBeenMade())
    }

}