package software.ninetofive.tools

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerAppCompatActivity
import software.ninetofive.photoselector.PhotoSelector
import software.ninetofive.photoselector.exception.PhotoSelectorException
import software.ninetofive.photoselector.interfaces.PhotoSelectorListener
import software.ninetofive.review.AskForReview
import software.ninetofive.review.AskForReviewDialog
import software.ninetofive.review.conditions.EnabledCondition
import software.ninetofive.tools.databinding.ActivityTestBinding
import software.ninetofive.tools.util.ImageRotator
import java.io.File
import javax.inject.Inject

class TestActivity : DaggerAppCompatActivity(), PhotoSelectorListener, AskForReviewDialog.OnRatingClicked {

    private lateinit var model: TestActivityViewModel
    private lateinit var photoSelector: PhotoSelector

    @Inject
    lateinit var imageRotator: ImageRotator
    @Inject
    lateinit var askForReview: AskForReview

    private var askForReviewDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return modelClass.newInstance()
            }
        }).get(TestActivityViewModel::class.java)
        photoSelector = PhotoSelector.newInstance()

        val binding: ActivityTestBinding = DataBindingUtil.setContentView(this, R.layout.activity_test)
        binding.model = model
        binding.lifecycleOwner = this

        binding.startButton.setOnClickListener(this::onClickStartButton)
        binding.selectImageButton.setOnClickListener(this::onClickSelectImageButton)
        binding.takePictureButton.setOnClickListener(this::onClickTakePictureButton)
        binding.askForReview.setOnClickListener(this::onClickShowDialog)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        photoSelector.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        photoSelector.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    // Listeners

    private fun onClickStartButton(view: View) {
        val authority = "$packageName.fileprovider"
        photoSelector.start(activity = this, options = mapOf(
            PhotoSelector.Options.FILE_PROVIDER_AUTHORITY_NAME to authority,
            PhotoSelector.Options.RATIONALE_HANDLER to { photoSelector.allowedRationale() }
        ), listener = this)
    }

    private fun onClickSelectImageButton(view: View) {
        val authority = "$packageName.fileprovider"
        photoSelector.startSelectImage(activity = this, options = mapOf(
            PhotoSelector.Options.FILE_PROVIDER_AUTHORITY_NAME to authority,
            PhotoSelector.Options.RATIONALE_HANDLER to { photoSelector.allowedRationale() }
        ), listener = this)
    }

    private fun onClickTakePictureButton(view: View) {
        val authority = "$packageName.fileprovider"
        photoSelector.startTakePicture(activity = this, options = mapOf(
            PhotoSelector.Options.FILE_PROVIDER_AUTHORITY_NAME to authority,
            PhotoSelector.Options.RATIONALE_HANDLER to { photoSelector.allowedRationale() }
        ), listener = this)
    }

    private fun onClickShowDialog(view: View) {
        askForReview.initialize(this, listOf(EnabledCondition(true)))
        val dialog = AskForReviewDialog(this, R.style.AppTheme, onRatingClicked = this)
        askForReview.showDialog(dialog) {
            askForReviewDialog = it
            it.show()
        }
    }

    override fun onSuccessPhotoSelected(imageFile: File) {
        model.image.value = imageRotator.rotateImage(imageFile.path)
    }

    override fun onFailurePhotoSelected(exception: PhotoSelectorException) {
        val view = findViewById<View>(android.R.id.content)
        Snackbar.make(view, "Something went wrong: ${exception.message}", Snackbar.LENGTH_LONG).show()
    }

    override fun onNegativeRating(dialog: AskForReviewDialog, rating: Int) {
        val contentView = findViewById<View>(android.R.id.content)
        Snackbar.make(contentView, "Negative rating selected", Snackbar.LENGTH_LONG).show()
        askForReviewDialog?.dismiss()
    }

    override fun onPositiveRating(dialog: AskForReviewDialog, rating: Int) {
        val contentView = findViewById<View>(android.R.id.content)
        Snackbar.make(contentView, "Positive rating selected", Snackbar.LENGTH_LONG).show()
        askForReviewDialog?.dismiss()
    }

}