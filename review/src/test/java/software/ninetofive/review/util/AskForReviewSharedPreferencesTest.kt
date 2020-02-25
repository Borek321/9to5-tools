package software.ninetofive.review.util

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class AskForReviewSharedPreferencesTest {

    lateinit var context: Context
    lateinit var sharedPreferences: SharedPreferences
    val version: String = "423"

    lateinit var preferences: AskForReviewSharedPreferences

    @Before
    fun setUp() {
        sharedPreferences = mock()
        val packageInfo: PackageInfo = PackageInfo().apply { versionName = version }
        val packageManagerMock: PackageManager = mock { on { this.getPackageInfo("name", 0) } doReturn packageInfo }
        context = mock {
            on { this.packageName } doReturn "name"
            on { this.packageManager } doReturn packageManagerMock
            on { this.getSharedPreferences(any(), any()) } doReturn sharedPreferences
        }
        preferences = AskForReviewSharedPreferences(context)
    }

    @Test
    fun getLaunchCount_returnsLaunchCount() {
        val launchCount = 43
        Mockito.`when`(sharedPreferences.getInt(any(), eq(0))).doReturn(launchCount)
        val result = preferences.getLaunchCount()

        assertEquals(launchCount, result)
    }

    @Test
    fun getDaysTimeStampInMilliseconds_returnsTimeStamp() {
        val timesTamp = 43513513925L
        Mockito.`when`(sharedPreferences.getLong(any(), eq(-1L))).doReturn(timesTamp)
        val result = preferences.getDaysTimeStampInMilliseconds()

        assertEquals(timesTamp, result)
    }

    @Test
    fun getAlreadyShowed_returnsAlreadyShowed() {
        val alreadyShowed = true
        Mockito.`when`(sharedPreferences.getBoolean(any(), eq(false))).doReturn(alreadyShowed)
        val result = preferences.getAlreadyShowed()

        assertEquals(alreadyShowed, result)
    }

    @Test
    fun incrementLaunchCount_incrementsValueInPreferences() {
        val editor: SharedPreferences.Editor = mock()
        val launchCount = 4
        Mockito.`when`(sharedPreferences.edit()).doReturn(editor)
        Mockito.`when`(editor.putInt(any(), eq(launchCount + 1))).doReturn(editor)
        Mockito.`when`(sharedPreferences.getInt(any(), eq(0))).doReturn(launchCount)

        preferences.incrementLaunchCount()

        verify(editor).apply()
        verify(editor).putInt(any(), eq(launchCount + 1))
        verify(sharedPreferences).edit()
    }

    @Test
    fun setDays_doesNothingWhenAlreadyHasDays() {
        val now = 3243L
        val timesTamp = 43513513925L
        Mockito.`when`(sharedPreferences.getLong(any(), eq(-1L))).doReturn(timesTamp)
        preferences.setDays(now)

        verify(sharedPreferences, times(0)).edit()
    }

    @Test
    fun setDays_setsNewDaysIfNotAlreadyHasDays() {
        val now = 3243L
        val editor: SharedPreferences.Editor = mock()

        Mockito.`when`(sharedPreferences.getLong(any(), eq(-1L))).doReturn(-1L)
        Mockito.`when`(sharedPreferences.edit()).doReturn(editor)
        Mockito.`when`(editor.putLong(any(), eq(now))).doReturn(editor)
        preferences.setDays(now)

        verify(editor).apply()
        verify(editor).putLong(any(), eq(now))
        verify(sharedPreferences).edit()
    }

    @Test
    fun setAlreadyShowed_putsTrueAsBooleanInPreferences() {
        val editor: SharedPreferences.Editor = mock()

        Mockito.`when`(sharedPreferences.edit()).doReturn(editor)
        Mockito.`when`(editor.putBoolean(any(), eq(true))).doReturn(editor)

        preferences.setAlreadyShowed()

        verify(editor).apply()
        verify(editor).putBoolean(any(), eq(true))
        verify(sharedPreferences).edit()
    }


}