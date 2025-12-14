package ie.setu.mobileappdevelopmentca1.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ie.setu.mobileappdevelopmentca1.databinding.ActivityEventMapsBinding

class PlacemarkMapsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEventMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
    }
}