package ie.setu.mobileappdevelopmentca1.models

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import timber.log.Timber

class EventFirebaseMemStore {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val cache = mutableMapOf<String, EventModel>() //https://www.geeksforgeeks.org/kotlin/kotlin-mutablemapof/

    private fun eventsRef() = db.collection("users")
        .document(requireNotNull(auth.currentUser?.uid) { "Not signed in" })
        .collection("events")

    fun findById(id: String): EventModel? = cache[id]
    fun findAll(): List<EventModel> = cache.values.toList()
    fun findAllPairs(): List<Pair<String, EventModel>> = cache.map { (id, event) -> id to event }
    fun findByTitlePairs(query: String): List<Pair<String, EventModel>> = cache.filter { (_, event) -> event.title.contains(query, ignoreCase = true) }.map { (id, event) -> id to event }

    fun create(
        event: EventModel,
        onDone: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        eventsRef()
            .add(event)
            .addOnSuccessListener { ref ->
                Timber.i("Event created with ID: ${ref.id}")
                cache[ref.id] = event
                onDone(ref.id)
            }
            .addOnFailureListener {
                Timber.e(it, "Failed to create event")
                onError(it) }
    }

    fun update(eventId: String, event: EventModel, onDone: () -> Unit = {}, onError: (Exception) -> Unit = {}) {
        eventsRef()
            .document(eventId)
            .set(event)
            .addOnSuccessListener {
                cache[eventId] = event
                onDone()
            }
            .addOnFailureListener { onError(it) }
    }

    fun update(event: EventModel) {
        val entry = cache.entries.firstOrNull { it.value == event } ?: return
        update(entry.key, event)
    } //convert from JSON

    fun delete(eventId: String, onDone: () -> Unit = {}, onError: (Exception) -> Unit = {}) {
        eventsRef()
            .document(eventId)
            .delete()
            .addOnSuccessListener {
                cache.remove(eventId)
                onDone()
            }
            .addOnFailureListener { onError(it) }
    }

    fun listenAll(
        onData: (List<Pair<String, EventModel>>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {
        return eventsRef().addSnapshotListener { snap, e ->
            if (e != null) {
                onError(e)
                return@addSnapshotListener
            }
            if (snap == null) {
                onData(emptyList())
                return@addSnapshotListener
            }
            cache.clear()
            val list = snap.documents.mapNotNull { doc ->
                val event = doc.toObject(EventModel::class.java)
                if (event != null) {
                    cache[doc.id] = event
                    doc.id to event
                } else null
            }

            onData(list)
        }
    } //https://firebase.google.com/docs/firestore/query-data/listen, https://medium.com/firebase-tips-tricks/how-to-effortlessly-get-real-time-updates-from-firestore-on-android-bcb823f45f20,
    // https://medium.com/@ncubes1999/how-to-use-firebase-firestore-with-kotlin-coroutines-8d1f498f9c94
}