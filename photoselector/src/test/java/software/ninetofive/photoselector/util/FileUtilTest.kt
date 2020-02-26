package software.ninetofive.photoselector.util

import android.content.Context
import android.os.Environment
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import software.ninetofive.photoselector.factory.FileUriFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class FileUtilTest @Inject constructor() {

    lateinit var factory: FileUriFactory

    lateinit var fileUtil: FileUtil

    @Before
    fun setUp() {
        factory = mock()
        fileUtil = FileUtil(factory)
    }

    @Test
    fun createImageFile_returnsNullIfErrorThrown() {
        val context: Context = mock()
        val dateString: String = "date"
        val file: File = mock()
        val exception: IOException = mock()
        Mockito.`when`(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)).thenReturn(file)
        Mockito.`when`(factory.createTempFile(any(), any(), any())).thenThrow(exception)

        val result = fileUtil.createImageFile(context, fileIdentifier = dateString)

        assertNull(result)
        verify(exception).printStackTrace()
    }

    @Test
    fun createImageFile_returnsfileIfNoError() {
        val context: Context = mock()
        val dateString: String = "date"
        val file: File = mock()
        Mockito.`when`(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)).thenReturn(file)
        Mockito.`when`(factory.createTempFile(any(), any(), any())).thenReturn(file)

        val result = fileUtil.createImageFile(context, fileIdentifier = dateString)

        assertEquals(file, result)
    }

    @Test
    fun persistBitmap_returnsNullIfErrorThrown() {
        val context: Context = mock()
        val file: File = mock()
        val exception: IOException = mock()
        Mockito.`when`(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)).thenReturn(file)
        Mockito.`when`(factory.createTempFile(any(), any(), any())).thenThrow(exception)

        val result = fileUtil.persistBitmap(context, mock(), "jpeg")

        assertNull(result)
        verify(exception).printStackTrace()
    }

    @Test
    fun persistBitmap_returnsNullIfCompressThrowsError() {
        val context: Context = mock()
        val file: File = mock()
        val exception: IOException = mock()
        val stream: FileOutputStream = mock()
        Mockito.`when`(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)).thenReturn(file)
        Mockito.`when`(factory.createTempFile(any(), any(), any())).thenReturn(file)
        Mockito.`when`(factory.createOutputStream(file)).thenReturn(stream)
        Mockito.`when`(stream.flush()).thenThrow(exception)

        val result = fileUtil.persistBitmap(context, mock(), "jpeg")

        assertNull(result)
        verify(exception).printStackTrace()
    }

    @Test
    fun persistBitmap_returnsFileAndCallsCloseAndSuch() {
        val context: Context = mock()
        val file: File = mock()
        val stream: FileOutputStream = mock()
        Mockito.`when`(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)).thenReturn(file)
        Mockito.`when`(factory.createTempFile(any(), any(), any())).thenReturn(file)
        Mockito.`when`(factory.createOutputStream(file)).thenReturn(stream)

        val result = fileUtil.persistBitmap(context, mock(), "jpeg")

        assertEquals(file, result)
        verify(stream).flush()
        verify(stream).close()
    }

}