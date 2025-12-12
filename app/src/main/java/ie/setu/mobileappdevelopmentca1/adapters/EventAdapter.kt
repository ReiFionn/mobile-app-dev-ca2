package ie.setu.mobileappdevelopmentca1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ie.setu.mobileappdevelopmentca1.databinding.CardEventBinding
import ie.setu.mobileappdevelopmentca1.models.EventModel

interface EventListener {
    fun onEventClick(event: EventModel)
    fun onDeleteButtonClicked(event: EventModel)
}

class EventAdapter (private var events: List<EventModel>, private val listener: EventListener) :
    RecyclerView.Adapter<EventAdapter.MainHolder>() {

        private val displayedEvents = events.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardEventBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val event = displayedEvents[holder.adapterPosition]
        holder.bind(event, listener)
    }

    override fun getItemCount(): Int = displayedEvents.size

    fun submitList(newList: List<EventModel>) {
        displayedEvents.clear()
        displayedEvents.addAll(newList)
        notifyDataSetChanged()
    }

    class MainHolder(private val binding: CardEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(event: EventModel, listener: EventListener) {
            val dateText = "${event.day}/${event.month}/${event.year}"
            val capacityText = "Capacity: ${event.capacity}"

            binding.eventTitle.text = event.title
            binding.eventDescription.text = event.description
            binding.eventDate.text = dateText
            binding.eventType.text = event.type
            binding.eventCapacity.text = capacityText
            binding.btnDelete.setOnClickListener { listener.onDeleteButtonClicked(event) }
            Picasso.get().load(event.image).resize(200,200).into(binding.imageIcon)
            binding.root.setOnClickListener { listener.onEventClick(event) }
        }
    }
}