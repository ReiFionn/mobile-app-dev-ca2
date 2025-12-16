package ie.setu.mobileappdevelopmentca1.views.eventList

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ie.setu.mobileappdevelopmentca1.R
import ie.setu.mobileappdevelopmentca1.adapters.EventAdapter
import ie.setu.mobileappdevelopmentca1.adapters.EventListener
import ie.setu.mobileappdevelopmentca1.databinding.ActivityEventListBinding
import ie.setu.mobileappdevelopmentca1.main.MainApp
import ie.setu.mobileappdevelopmentca1.models.EventModel

class EventListView : AppCompatActivity(), EventListener {

    lateinit var app: MainApp
    private lateinit var binding: ActivityEventListBinding
    lateinit var presenter: EventListPresenter
    private var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)
        binding.eventSV.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String?): Boolean {
                handleSearch(newText)
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                handleSearch(query)
                return true
            }
        })

        presenter = EventListPresenter(this)
        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        loadEvents()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> { presenter.doAddEvent() }
            R.id.item_map -> { presenter.doShowEventsMap() }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onEventClick(event: EventModel, position: Int) {
        this.position = position
        presenter.doEditEvent(event, this.position)
    }

    override fun onDeleteButtonClicked(event: EventModel) {
        AlertDialog.Builder(this)
            .setTitle("Delete event")
            .setMessage("Are you sure you want to delete this event?")
            .setPositiveButton("Delete") { _, _ ->
                presenter.doDeleteEvent(event)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun loadEvents() {
        binding.recyclerView.adapter = EventAdapter(presenter.getEvents(), this)
        onRefresh()
    }

    fun onRefresh() {
        (binding.recyclerView.adapter as EventAdapter).submitList(presenter.getEvents().toList())
    }

    fun handleSearch(query: String?) {
        val filtered = presenter.searchByTitle(query)
        (binding.recyclerView.adapter as EventAdapter)
            .submitList(filtered.toList())
    }
}
