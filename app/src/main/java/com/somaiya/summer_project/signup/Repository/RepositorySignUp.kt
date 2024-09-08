import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.somaiya.summer_project.signup.Model.DataSignUp

class RepositorySignUp {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun signUp(dataSignUp: DataSignUp, context: Context) {
        auth.createUserWithEmailAndPassword(dataSignUp.email, dataSignUp.password)
            .addOnCompleteListener {
                auth.currentUser?.sendEmailVerification()?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(
                            context,
                            "We have sent you a verification link via email. Please verify your account.",
                            Toast.LENGTH_SHORT
                        ).show();
                    } else {
                        Toast.makeText(
                            context, "Something went wrong " + (it.exception?.localizedMessage
                                ?: ""), Toast.LENGTH_SHORT
                        ).show();
                    }
                }
            }
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser!!.uid
                    val userRef = firestore.collection("USERS").document(userId)

                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(dataSignUp.firstName).build()

                    auth.currentUser?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { profileTask ->
                            if (profileTask.isSuccessful) {
                                // User profile updated successfully
                                Log.d("ProfileUpdateCheck", "User profile updated successfully")
                            } else {
                                // Handle failure
                                Log.d("ProfileUpdateCheck", "User profile update failed")
                            }

                            val data = hashMapOf(
                                "firstName" to dataSignUp.firstName,
                                "lastName" to dataSignUp.lastName,
                                "displayName" to dataSignUp.displayName,
                                "phoneNumber" to dataSignUp.phoneNumber,
                                "email" to dataSignUp.email,
                                "course" to dataSignUp.course,
                                "div" to dataSignUp.div,
                                "rollNo" to dataSignUp.rollNo,
                                "role" to dataSignUp.role,
                                "isactive" to false,
                                "canCreateNewReason" to true
                            )
                            userRef.set(data)
                        }
                }
            }
    }
}