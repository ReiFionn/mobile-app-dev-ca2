package ie.setu.mobileappdevelopmentca1.views.event

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import ie.setu.mobileappdevelopmentca1.R
import ie.setu.mobileappdevelopmentca1.views.event.EventPresenter
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

        binding.btnAdd.setOnClickListener {
            if (binding.eventTitle.text.toString().isEmpty()) {
                Snackbar.make(binding.root, R.string.hint_eventTitle , Snackbar.LENGTH_LONG)
                    .show()
            } else {
                // presenter.cachePlacemark(binding.eventTitle.text.toString(), binding.eventDescription.text.toString())
                presenter.doAddOrSave(binding.eventTitle.text.toString(), binding.eventDescription.text.toString())
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
                presenter.doDelete()
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

    }

    fun updateImage(image: Uri){
        Timber.i("Image updated")
        Picasso.get()
            .load(image)
            .into(binding.eventImage)
        binding.chooseImage.setText(R.string.change_event_image)
    }
}