package ie.setu.mobileappdevelopmentca1.views.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import ie.setu.mobileappdevelopmentca1.databinding.ActivityAuthBinding
import ie.setu.mobileappdevelopmentca1.views.eventList.EventListView

class AuthView : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var presenter: AuthPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = AuthPresenter(this)

        binding.SignIn.setOnClickListener {
            presenter.signIn(
                binding.email.text.toString(),
                binding.password.text.toString()
            )
        }
        binding.SignUp.setOnClickListener {
            presenter.signUp(
                binding.email.text.toString(),
                binding.password.text.toString()
            )
        }
    }

    fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    fun navigateToApp() {
        startActivity(Intent(this, EventListView::class.java))
        finish()
    }
}