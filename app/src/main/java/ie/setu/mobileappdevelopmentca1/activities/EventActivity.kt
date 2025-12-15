package ie.setu.mobileappdevelopmentca1.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.DatePicker
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import ie.setu.mobileappdevelopmentca1.R
import ie.setu.mobileappdevelopmentca1.databinding.ActivityMainBinding
import ie.setu.mobileappdevelopmentca1.main.MainApp
import ie.setu.mobileappdevelopmentca1.models.EventModel
import ie.setu.mobileappdevelopmentca1.models.Location
import timber.log.Timber.i
import java.util.Calendar

class EventActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var event = EventModel()
    lateinit var app : MainApp
    var edit = false
    private lateinit var imageIntentLauncher : ActivityResultLauncher<PickVisualMediaRequest>
    var image = ""
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerImagePickerCallback()
        registerMapCallback()

        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        val datePicker: DatePicker = findViewById(R.id.eventDate)
        val today = Calendar.getInstance()
        datePicker.init(
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        ) { view, year, month, day ->
            event.year = year
            event.month = month+1
            event.day = day
        }

        val eventTypes = resources.getStringArray(R.array.event_types)
        val arrayAdapter = ArrayAdapter(this, R.layout.event_type_dropdown, eventTypes)
        val eventTypeTV = findViewById<AutoCompleteTextView>(R.id.eventType)
        eventTypeTV.setAdapter(arrayAdapter)

        binding.eventCapacity.maxValue = 100
        binding.eventCapacity.minValue = 2

        app = application as MainApp
        i("Event Activity started...")

        if (intent.hasExtra("event_edit")) {
            edit = true
            event = intent.extras?.getParcelable("event_edit")!!
            binding.eventTitle.setText(event.title)
            binding.eventDescription.setText(event.description)
            binding.eventDate.updateDate(event.year, event.month-1, event.day)
            binding.eventType.setText(event.type, false) //https://stackoverflow.com/questions/29906928/setting-value-in-autocompletetextview
            binding.eventCapacity.value = event.capacity
            binding.btnAdd.setText(R.string.save_event)
            Picasso.get()
                .load(event.image)
                .into(binding.eventImage)
            if (event.image != Uri.EMPTY) {
                binding.chooseImage.setText(R.string.change_event_image)
            }
        }

        binding.btnAdd.setOnClickListener() {
            event.title = binding.eventTitle.text.toString()
            event.description = binding.eventDescription.text.toString()
            event.year = binding.eventDate.year
            event.month = binding.eventDate.month+1 //months start at 0
            event.day = binding.eventDate.dayOfMonth
            event.type = binding.eventType.text.toString()
            event.capacity = binding.eventCapacity.value

            if (event.title.isEmpty() || event.description.isEmpty() || event.year == 0 || event.type.isEmpty() || event.capacity == 0) {
                Snackbar.make(it,R.string.enter_event_details, Snackbar.LENGTH_LONG).show()
            }
            else {
                if (edit) {
                    app.events.update(event.copy())
                    setResult(RESULT_OK)
                    finish()
                } else {
                    app.events.create(event.copy())
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }

        binding.chooseImage.setOnClickListener {
            i("Select image")
        }

        binding.chooseImage.setOnClickListener {
            val request = PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                .build()
            imageIntentLauncher.launch(request)
        }

        binding.eventLocation.setOnClickListener {
            val location = Location(52.245696, -7.139102, 15f)
            if (event.zoom != 0f) {
                location.lat =  event.lat
                location.lng = event.lng
                location.zoom = event.zoom
            }
            val launcherIntent = Intent(this, MapActivity::class.java)
                .putExtra("location", location)
            mapIntentLauncher.launch(launcherIntent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_event, menu)
        if (edit) menu.getItem(0).isVisible = true
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_delete -> {
                setResult(99)
                app.events.delete(event)
                finish()
            }        R.id.item_cancel -> { finish() }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun registerImagePickerCallback() {
        imageIntentLauncher = registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) {
            try{
                contentResolver
                    .takePersistableUriPermission(it!!,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION )
                event.image = it // The returned Uri
                i("IMG :: ${event.image}")
                Picasso.get()
                    .load(event.image)
                    .into(binding.eventImage)
            }
            catch(e:Exception){
                e.printStackTrace()
            }
        }
    }

    private fun registerMapCallback() {
        mapIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when (result.resultCode) {
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Location ${result.data.toString()}")
                            val location = result.data!!.extras?.getParcelable<Location>("location")!!
                            i("Location == $location")
                            event.lat = location.lat
                            event.lng = location.lng
                            event.zoom = location.zoom
                        } // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }
}
