package ie.setu.mobileappdevelopmentca1.views.event

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import ie.setu.mobileappdevelopmentca1.R
import ie.setu.mobileappdevelopmentca1.databinding.ActivityMainBinding
import ie.setu.mobileappdevelopmentca1.models.EventModel
import timber.log.Timber

class EventView : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var presenter: EventPresenter
    var event = EventModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        val eventTypes = resources.getStringArray(R.array.event_types)
        val arrayAdapter = ArrayAdapter(this, R.layout.event_type_dropdown, eventTypes)
        val eventTypeTV = findViewById<AutoCompleteTextView>(R.id.eventType)
        eventTypeTV.setAdapter(arrayAdapter)

        binding.eventCapacity.maxValue = 500
        binding.eventCapacity.minValue = 2

        presenter = EventPresenter(this)

        binding.chooseImage.setOnClickListener {
            presenter.cacheEvent(binding.eventTitle.text.toString(), binding.eventDescription.text.toString())
            presenter.doSelectImage()
        }

        binding.eventLocation.setOnClickListener {
            presenter.cacheEvent(binding.eventTitle.text.toString(), binding.eventDescription.text.toString())
            presenter.doSetLocation()
        }

        var dateSelected = false

        binding.eventDate.setOnDateChangedListener { _, _, _, _ ->
            dateSelected = true
        }

        binding.btnAdd.setOnClickListener {
            if (binding.eventTitle.text.toString().isEmpty() ||
                binding.eventDescription.text.toString().isEmpty() ||
                binding.eventType.text.toString().isEmpty() ||
                binding.eventCapacity.value <= 0 ||
                binding.locationText.text.toString() == "No location selected" ||
                !dateSelected ||
                !imageSelected) {
                Snackbar.make(binding.root, R.string.enter_event_details , Snackbar.LENGTH_LONG)
                    .show()
            } else {
                // presenter.cachePlacemark(binding.eventTitle.text.toString(), binding.eventDescription.text.toString())
                presenter.doAddOrSave(binding.eventTitle.text.toString(),
                    binding.eventDescription.text.toString(),
                    binding.eventDate.year,
                    binding.eventDate.month,
                    binding.eventDate.dayOfMonth,
                    binding.eventType.text.toString(),
                    binding.eventCapacity.value)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_event, menu)
        val deleteMenu: MenuItem = menu.findItem(R.id.item_delete)
        deleteMenu.isVisible = presenter.edit
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_delete -> {
                AlertDialog.Builder(this)
                    .setTitle("Delete event")
                    .setMessage("Are you sure you want to delete this event?")
                    .setPositiveButton("Delete") { _, _ ->
                        presenter.doDelete()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
            R.id.item_cancel -> {
                presenter.doCancel()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun showEvent(event: EventModel) {
        binding.eventTitle.setText(event.title)
        binding.eventDescription.setText(event.description)
        binding.btnAdd.setText(R.string.save_event)
        Picasso.get()
            .load(event.image)
            .into(binding.eventImage)
        if (event.image.toUri() != Uri.EMPTY) {
            binding.chooseImage.setText(R.string.change_event_image)
        }
        binding.locationText.text = "${event.lat%.5f}, ${event.lng%.5f}"
    }

    private var imageSelected = false

    fun updateImage(image: Uri){
        Timber.i("Image updated")
        Picasso.get()
            .load(image)
            .into(binding.eventImage)
        binding.chooseImage.setText(R.string.change_event_image)
        imageSelected = true
    }

    fun updateLocationText(lat: Double, lng: Double) {
        binding.locationText.text = "Location: %.5f, %.5f".format(lat, lng)
    }
}