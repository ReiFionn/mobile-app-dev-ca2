package ie.setu.mobileappdevelopmentca1.views.auth

import com.google.firebase.auth.FirebaseAuth

class AuthPresenter(private val view: AuthView) {

    private val auth = FirebaseAuth.getInstance()

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            view.showError("Email and password required")
            return
        }
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            view.navigateToApp()
        }
        .addOnFailureListener {
            view.showError(it.message ?: "Sign in failed")
        }
    } //https://firebase.google.com/docs/auth/android/password-auth

    fun signUp(email: String, password: String) {
        if (email.length < 8 || password.length < 8) {
            view.showError("Email and Password must be at least 8 characters")
            return
        }
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            view.navigateToApp()
        }
        .addOnFailureListener {
            view.showError(it.message ?: "Sign up failed")
        }
    }
}