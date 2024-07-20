import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.somaiya.summer_project.signup.Model.DataSignUp

class RepositorySignUp {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun signUp(dataSignUp: DataSignUp) {
        auth.createUserWithEmailAndPassword(dataSignUp.email, dataSignUp.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser!!.uid
                    val userRef = firestore.collection("USERS").document(userId)


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
                        "isactive" to true
                    )

                    userRef.set(data)
                }
            }
    }
}