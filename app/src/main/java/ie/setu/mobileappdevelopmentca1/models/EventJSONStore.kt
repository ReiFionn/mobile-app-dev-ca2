package ie.setu.mobileappdevelopmentca1.models

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

import ie.setu.mobileappdevelopmentca1.helpers.*
import java.util.*

val JSON_FILE = "events.json"
val gsonBuilder = GsonBuilder().setPrettyPrinting().create()
val listType = object : TypeToken<ArrayList<EventModel>>() {}.type

fun generateRandomId(): Long {
    return Random().nextLong()
}

class EventJSONStore(val context: Context) : EventStore {

    var events = mutableListOf<EventModel>()

    init {
        if (exists(context, JSON_FILE)) {
            deserialize()
        }
    }

    override fun findAll(): MutableList<EventModel> {
        return events
    }

    fun findOne(id: Long) : EventModel? {
        val foundEvent: EventModel? = events.find { e -> e.id == id }
        return foundEvent
    }

    fun findByTitle(title: String): List<EventModel> {
        return events.filter {it.title.contains(title, ignoreCase = true)}
    }

    override fun create(event: EventModel) {
        event.id = generateRandomId()
        events.add(event)
        serialize()
    }

    override fun update(event: EventModel) {
        val foundEvent = findOne(event.id)
        if (foundEvent != null) {
            foundEvent.title = event.title
            foundEvent.description = event.description
            foundEvent.year = event.year
            foundEvent.month = event.month
            foundEvent.day = event.day
            foundEvent.type = event.type
            foundEvent.capacity = event.capacity
            foundEvent.image = event.image
            logAll()
        }
        serialize()
    }

    override fun delete(event: EventModel) {
        val foundEvent = findOne(event.id)
        if (foundEvent != null) {
            events.remove(event)
            logAll()
        }
        serialize()
    }

    internal fun logAll() {
        events.forEach { println("$it") }
    }

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(events, listType)
        write(context,JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context,JSON_FILE)
        events = Gson().fromJson(jsonString, listType)
    }
}