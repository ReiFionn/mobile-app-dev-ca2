package ie.setu.mobileappdevelopmentca1.views.event

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import ie.setu.mobileappdevelopmentca1.views.editLocation.EditLocationView
import ie.setu.mobileappdevelopmentca1.main.MainApp
import ie.setu.mobileappdevelopmentca1.models.EventModel
import ie.setu.mobileappdevelopmentca1.models.Location
import timber.log.Timber
import java.io.File

class EventPresenter(private val view: EventView) {

    private val app: MainApp = view.application as MainApp

    var event = EventModel()
    private lateinit var mapIntentLauncher: ActivityResultLauncher<Intent>

    var edit = false
    private lateinit var galleryLauncher: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var cameraImageUri: Uri

    init {
        if (view.intent.hasExtra("event_edit")) {
            edit = true
            event = view.intent.extras?.getParcelable("event_edit")!!
            view.showEvent(event)
        }

        registerMapCallback()
        registerGalleryPicker()
        registerCameraPicker()
    }

    fun doAddOrSave(title: String, description: String, year: Int, month: Int, day: Int, type: String, capacity: Int) {
        event.title = title
        event.description = description
        event.year = year
        event.month = month
        event.day = day
        event.type = type
        event.capacity = capacity

        if (edit) app.events.update(event) else app.events.create(event)

        view.setResult(Activity.RESULT_OK)
        view.finish()
    }

    fun doCancel() = view.finish()

    fun doDelete() {
        view.setResult(99)
        app.events.delete(event)
        view.finish()
    }

//    fun doSelectImage() {
//        val request = PickVisualMediaRequest.Builder()
//            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
//            .build()
//        imageIntentLauncher.launch(request)
//    }

    private fun registerGalleryPicker() {
        galleryLauncher = view.registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            if (uri == null) return@registerForActivityResult

            try {
                try {
                    view.contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: SecurityException) {
                    Timber.w(e)
                }

                event.image = uri.toString()
                view.updateImage(uri)

            } catch (e: Exception) {
                Timber.e(e, "Gallery image pick failed")
            }
        }
    }

    private fun registerCameraPicker() {
        cameraLauncher = view.registerForActivityResult(
            ActivityResultContracts.TakePicture() //https://developer.android.com/reference/androidx/activity/result/contract/ActivityResultContracts.TakePicture
        ) { success ->
            if (success) {
                event.image = cameraImageUri.toString()
                view.updateImage(cameraImageUri)
            }
        }
    }

    private fun createImageUri(): Uri {
        val imageFile = File(
            view.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "camera_image_${System.currentTimeMillis()}.jpg"
        )
        return FileProvider.getUriForFile(
            view,
            "${view.packageName}.provider",
            imageFile
        )
    }

    fun doSelectImage() {
        val options = arrayOf("Choose from Gallery", "Take Photo")

        AlertDialog.Builder(view)
            .setTitle("Add Event Image")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> launchGallery()
                    1 -> launchCamera()
                }
            }
            .show()
    }

    private fun launchGallery() {
        val request = PickVisualMediaRequest(
            ActivityResultContracts.PickVisualMedia.ImageOnly
        )
        galleryLauncher.launch(request)
    }

    private fun launchCamera() {
        cameraImageUri = createImageUri()
        cameraLauncher.launch(cameraImageUri)
    }

    fun doSetLocation() {
        val location = Location(52.245696, -7.139102, 15f).apply {
            if (event.zoom != 0f) {
                lat = event.lat
                lng = event.lng
                zoom = event.zoom
            }
        }

        val launcherIntent = Intent(view, EditLocationView::class.java)
            .putExtra("location", location)

        mapIntentLauncher.launch(launcherIntent)
    }

    fun cacheEvent(title: String, description: String) {
        event.title = title
        event.description = description
    }

//    private fun registerImagePickerCallback() {
//        imageIntentLauncher = view.registerForActivityResult(
//            ActivityResultContracts.PickVisualMedia()
//        ) { uri ->
//            if (uri == null) return@registerForActivityResult
//
//            try {
//                try {
//                    view.contentResolver.takePersistableUriPermission(
//                        uri,
//                        Intent.FLAG_GRANT_READ_URI_PERMISSION
//                    )
//                } catch (se: SecurityException) {
//                    Timber.w(se, "Persistable permission not granted for this Uri (often OK).")
//                }
//
//                event.image = uri.toString()
//
//                Timber.i("IMG :: ${event.image}")
//                view.updateImage(uri)
//
//            } catch (e: Exception) {
//                Timber.e(e, "Image pick failed")
//            }
//        }
//    }

    private fun registerMapCallback() {
        mapIntentLauncher =
            view.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                when (result.resultCode) {
                    AppCompatActivity.RESULT_OK -> {
                        val location = result.data?.extras?.getParcelable<Location>("location")
                        if (location != null) {
                            Timber.i("Location == $location")
                            event.lat = location.lat
                            event.lng = location.lng
                            event.zoom = location.zoom

                            view.updateLocationText(event.lat, event.lng)
                        }
                    }
                    AppCompatActivity.RESULT_CANCELED -> Unit
                    else -> Unit
                }
            }
    }
}