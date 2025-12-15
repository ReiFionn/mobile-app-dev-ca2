package ie.setu.mobileappdevelopmentca1.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import ie.setu.mobileappdevelopmentca1.R
import ie.setu.mobileappdevelopmentca1.adapters.EventAdapter
import ie.setu.mobileappdevelopmentca1.adapters.EventListener
import ie.setu.mobileappdevelopmentca1.databinding.ActivityEventListBinding
import ie.setu.mobileappdevelopmentca1.main.MainApp
import ie.setu.mobileappdevelopmentca1.models.EventModel
class EventListActivity : AppCompatActivity(), EventListener {
    lateinit var app: MainApp
    private lateinit var binding: ActivityEventListBinding
    private lateinit var adapter: EventAdapter
    private var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)

        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        adapter = EventAdapter(app.events.findAll(), this)
        binding.recyclerView.adapter = adapter

        binding.eventSV.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                handleSearch(newText)
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                handleSearch(query)
                return true
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> {
                val launcherIntent = Intent(this, EventActivity::class.java)
                getResult.launch(launcherIntent)
            }
            R.id.item_map -> {
                val launcherIntent = Intent(this, EventMapsActivity::class.java)
                mapIntentLauncher.launch(launcherIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val mapIntentLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        )    { }

    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            adapter.submitList(app.events.findAll().toList())
        }
    }

    override fun onEventClick(event: EventModel, pos: Int) {
        val launcherIntent = Intent(this, EventActivity::class.java)
        launcherIntent.putExtra("placemark_edit", event)
        position = pos
        getClickResult.launch(launcherIntent)
    }

    override fun onDeleteButtonClicked(event: EventModel) {
        app.events.delete(event)
        (binding.recyclerView.adapter)?.notifyItemRangeChanged(0, app.events.findAll().size)
        adapter.submitList(app.events.findAll().toList())
    }

    private val getClickResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK) {
                (binding.recyclerView.adapter)?.
                notifyItemRangeChanged(0,app.events.findAll().size)
            }
            else // Deleting
                if (it.resultCode == 99)     (binding.recyclerView.adapter)?.notifyItemRemoved(position)
        }

    private fun handleSearch(query: String?) {
        if (query.isNullOrEmpty()) adapter.submitList (app.events.findAll())
        else filterEvents(query)
    }

    private fun filterEvents(query: String?) {
        val filteredList = if (!query.isNullOrBlank())
            app.events.findByTitle(query)
        else app.events.findAll()
        adapter.submitList(filteredList)
    }
}