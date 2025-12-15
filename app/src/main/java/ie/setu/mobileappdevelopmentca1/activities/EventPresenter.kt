package ie.setu.mobileappdevelopmentca1.activities

import EventView
import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import ie.setu.mobileappdevelopmentca1.main.MainApp
import ie.setu.mobileappdevelopmentca1.models.EventModel
import ie.setu.mobileappdevelopmentca1.models.Location
import timber.log.Timber

class EventPresenter(private val view: EventView) {

    private val app: MainApp = view.application as MainApp

    var event = EventModel()
    private lateinit var imageIntentLauncher: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var mapIntentLauncher: ActivityResultLauncher<Intent>

    private var edit = false

    init {
        if (view.intent.hasExtra("event_edit")) {
            edit = true
            event = view.intent.extras?.getParcelable("event_edit")!!
            view.showEvent(event)
        }

        registerImagePickerCallback()
        registerMapCallback()
    }

    fun doAddOrSave(title: String, description: String) {
        event.title = title
        event.description = description

        if (edit) app.events.update(event) else app.events.create(event)

        view.setResult(RESULT_OK)
        view.finish()
    }

    fun doCancel() = view.finish()

    fun doDelete() {
        view.setResult(99)
        app.events.delete(event)
        view.finish()
    }

    fun doSelectImage() {
        val request = PickVisualMediaRequest.Builder()
            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
            .build()
        imageIntentLauncher.launch(request)
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

    private fun registerImagePickerCallback() {
        imageIntentLauncher = view.registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            if (uri == null) return@registerForActivityResult

            try {
                try {
                    view.contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (se: SecurityException) {
                    Timber.w(se, "Persistable permission not granted for this Uri (often OK).")
                }

                event.image = uri.toString()

                Timber.i("IMG :: ${event.image}")
                view.updateImage(uri)

            } catch (e: Exception) {
                Timber.e(e, "Image pick failed")
            }
        }
    }

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
                        }
                    }
                    AppCompatActivity.RESULT_CANCELED -> Unit
                    else -> Unit
                }
            }
    }
}
