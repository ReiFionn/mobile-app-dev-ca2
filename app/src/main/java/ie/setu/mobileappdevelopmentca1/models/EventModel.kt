package ie.setu.mobileappdevelopmentca1.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EventModel (
    var id: Long = 0,
    var title: String = "",
    var description: String = "",
    var year: Int = 0,
    var month: Int = 0,
    var day: Int = 0,
    var type: String = "",
    var capacity: Int = 0,
    var image: Uri = Uri.EMPTY
) : Parcelable