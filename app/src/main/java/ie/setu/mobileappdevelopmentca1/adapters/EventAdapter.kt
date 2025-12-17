package ie.setu.mobileappdevelopmentca1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ie.setu.mobileappdevelopmentca1.databinding.CardEventBinding
import ie.setu.mobileappdevelopmentca1.models.EventModel
import androidx.core.net.toUri
import ie.setu.mobileappdevelopmentca1.R

interface EventListener {
    fun onEventClick(id: String, event: EventModel)
    fun onDeleteButtonClicked(id: String, event: EventModel)
}

class EventAdapter (events: List<Pair<String,EventModel>>, private val listener: EventListener) : //https://www.baeldung.com/kotlin/pair-class
    RecyclerView.Adapter<EventAdapter.MainHolder>() {

        private val displayedEvents = events.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardEventBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val (id, event) = displayedEvents[holder.adapterPosition]
        holder.bind(id, event, listener)
    }

    override fun getItemCount(): Int = displayedEvents.size

    fun submitList(newList: List<Pair<String, EventModel>>) {
        displayedEvents.clear()
        displayedEvents.addAll(newList)
        notifyDataSetChanged()
    }

    class MainHolder(private val binding: CardEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(id: String, event: EventModel, listener: EventListener) {
            val dateText = "${event.day}/${event.month}/${event.year}"
            val capacityText = "Capacity: ${event.capacity}"
            val locationText = String.format("%.5f, %.5f", event.lat, event.lng)

            binding.eventTitle.text = event.title
            binding.eventDescription.text = event.description
            binding.eventDate.text = dateText
            binding.eventType.text = event.type
            binding.eventCapacity.text = capacityText
            binding.btnDelete.setOnClickListener { listener.onDeleteButtonClicked(id, event) }
            Picasso.get()
                .load(event.image.toUri())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .resize(200,200)
                .into(binding.imageIcon)
            binding.eventLocation.text = locationText
            binding.root.setOnClickListener { listener.onEventClick(id, event) }
        }
    }
}