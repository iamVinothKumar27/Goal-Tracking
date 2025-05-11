package com.example.goal

import android.os.Build
import android.util.Patterns
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.goal.data.Goal
import com.example.goal.data.GoalWithId
import com.example.goal.data.MainFlow
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

class MainViewmodel : ViewModel() {
    val firebaseAuth: FirebaseAuth  = FirebaseAuth.getInstance()
    val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    var flow  = MutableSharedFlow<MainFlow>(0)
    private  var _goalsData = MutableStateFlow<List<GoalWithId>>(emptyList())
    val goalsData : StateFlow<List<GoalWithId>>  get() = _goalsData
    var completedTaskCount by  mutableStateOf(0)
    var username by mutableStateOf("")

     fun login(email : String, password :String, navController: NavController) {
         val p: Pattern = Patterns.EMAIL_ADDRESS
         val isValidEmail = p.matcher(email).matches()
         if (!isValidEmail) {
             viewModelScope.launch {
                 flow.emit(MainFlow.toastSuccess("enter the valid mail"))
             }
             return
         } else if (password.length < 8) {
             viewModelScope.launch {
                 flow.emit(MainFlow.toastSuccess("password must be atleast length of 8"))
             }
             return
         } else {
             firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                 OnCompleteListener { task ->
                     if (task.isSuccessful) {
                         navController.navigate("main")
                         viewModelScope.launch {
                             flow.emit(MainFlow.toastSuccess("success"))
                         }
                     } else {
                         viewModelScope.launch {
                             flow.emit(MainFlow.toastSuccess(task.exception.toString()))
                         }
                     }
                 }
             )
         }
     }
    fun fetchUsername() {
        firebaseAuth.currentUser?.uid?.let { uid ->
            db.collection("Users")
                .document(uid)
                .get()
                .addOnCompleteListener(OnCompleteListener {
                    if (it.isSuccessful) {
                        val documents = it.result
                        username = documents.data?.get("name").toString()
                        }
                    })
                }
        }


    fun signup(email : String, password :String, name :String, confirmPass :String, navController: NavController) {
        val p: Pattern = Patterns.EMAIL_ADDRESS
        val isValidEmail = p.matcher(email).matches()
        if (!isValidEmail) {
            viewModelScope.launch {
                flow.emit(MainFlow.toastSuccess("enter the valid mail"))
            }
            return
        } else if (password != confirmPass) {
            viewModelScope.launch {
                flow.emit(MainFlow.toastSuccess("password doesn't match"))
            }
            return
        } else if (password.length < 8) {
            viewModelScope.launch {
                flow.emit(MainFlow.toastSuccess("password must be atleast length of 8"))
            }
            return
        } else {



            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userData = hashMapOf(
                            "name" to name
                        )
                        firebaseAuth.currentUser?.uid?.let {uid->
                            db.collection("Users")
                                .document(uid)
                                .set(userData).addOnCompleteListener(
                                    OnCompleteListener {
                                        if(it.isSuccessful)
                                            navController.navigate("main")
                                        else{
                                            viewModelScope.launch {
                                                flow.emit(MainFlow.toastSuccess(it.exception.toString()))
                                            }
                                        }

                                    }
                                )
                        }

                    } else {
                        viewModelScope.launch {
                            flow.emit(MainFlow.toastSuccess(task.exception.toString()))
                        }
                    }
                }
            )
        }
    }
    fun addGoal(goal :Goal, onSucess : ()->Unit){
        if (goal.goal?.isEmpty() == true) {
            viewModelScope.launch {
                flow.emit(MainFlow.toastSuccess("Goal name cannot be empty"))
            }
            return
        }

        if (goal.type.isNullOrEmpty()) {
            viewModelScope.launch {
                flow.emit(MainFlow.toastSuccess("habit cannot be empty"))
            }
            return
        }
        firebaseAuth.currentUser?.uid?.let {uid->
            db.collection("goals")
                .document(uid)
                .collection("userGoals")
                .add(goal)
                .addOnCompleteListener(OnCompleteListener {task->
                    if(task.isSuccessful){
                        viewModelScope.launch {
                            flow.emit(MainFlow.toastSuccess("Goal Added"))
                        }
                        onSucess()
                    }
                    else{
                        viewModelScope.launch {
                            flow.emit(MainFlow.toastSuccess(task.exception.toString()))
                        }
                    }
                })
        }
    }
    fun fetchGoals() {
        firebaseAuth.currentUser?.uid?.let { uid ->
            db.collection("goals")
                .document(uid)
                .collection("userGoals")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val goals = task.result?.documents?.mapNotNull { document ->
                            try {
                                val goal = Goal(
                                    goal = document.getString("goal"),
                                    habit = document.getString("habit"),
                                    period = document.getLong("period")?.toInt(),
                                    type = document.getString("type"),
                                    created = document.getLong("created"),
                                    completedDates = document.get("completedDates") as? List<String>,
                                    completed = document.getBoolean("completed")
                                )
                                GoalWithId(document.id, goal)
                            } catch (e: Exception) {
                                null
                            }
                        }?.toMutableList()

                        _goalsData.value = goals ?: mutableListOf()
                    } else {
                        viewModelScope.launch {
                            flow.emit(MainFlow.toastSuccess(task.exception.toString()))
                        }
                    }
                }
        }

    }

     @RequiresApi(Build.VERSION_CODES.O)
     fun addCurrentDateToGoal(goalId: String) {
         val firestore = FirebaseFirestore.getInstance()
         val currentDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

         firebaseAuth.currentUser?.uid?.let { uid ->
             val goalRef = firestore.collection("goals")
                 .document(uid)
                 .collection("userGoals")
                 .document(goalId)

             goalRef.update("completedDates", FieldValue.arrayUnion(currentDate))
                 .addOnSuccessListener {
                     println("Current date added to completedDates.")

                     goalRef.get()
                         .addOnSuccessListener { documentSnapshot ->
                             if (documentSnapshot.exists()) {
                                 val completedDates = documentSnapshot.get("completedDates") as? List<*> ?: emptyList<String>()
                                 val period = documentSnapshot.getLong("period") ?: 0

                                 if (completedDates.size == period.toInt()) {
                                     goalRef.update("isCompleted", true)
                                         .addOnSuccessListener {
                                             println("Goal marked as completed.")
                                         }
                                         .addOnFailureListener { exception ->
                                             println("Error marking goal as completed: ${exception.message}")
                                         }
                                 }
                             } else {
                                 println("Goal document does not exist.")
                             }
                         }
                         .addOnFailureListener { exception ->
                             println("Error fetching goal document: ${exception.message}")
                         }
                 }
                 .addOnFailureListener { exception ->
                     println("Error adding current date: ${exception.message}")
                 }
         }
     }

    @RequiresApi(Build.VERSION_CODES.O)
    fun removeCurrentDateFromGoal(goalId: String) {
        val firestore = FirebaseFirestore.getInstance()
        val currentDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

        firebaseAuth.currentUser?.uid?.let { uid ->
            val goalRef = firestore.collection("goals")
                .document(uid)
                .collection("userGoals")
                .document(goalId)

            goalRef.update("completedDates", FieldValue.arrayRemove(currentDate))
                .addOnSuccessListener {
                    println("Current date removed from completedDates.")
                    goalRef.get()
                        .addOnSuccessListener { documentSnapshot ->
                            if (documentSnapshot.exists()) {
                                val completedDates = documentSnapshot.get("completedDates") as? List<*> ?: emptyList<String>()
                                val period = documentSnapshot.getLong("period") ?: 0

                                if (completedDates.size < period) {
                                    goalRef.update("isCompleted", false)
                                        .addOnSuccessListener {
                                            println("Goal marked as incomplete.")
                                        }
                                        .addOnFailureListener { exception ->
                                            println("Error marking goal as incomplete: ${exception.message}")
                                        }
                                }
                            } else {
                                println("Goal document does not exist.")
                            }
                        }
                        .addOnFailureListener { exception ->
                            println("Error fetching goal document: ${exception.message}")
                        }
                }
                .addOnFailureListener { exception ->
                    println("Error removing current date: ${exception.message}")
                }
        }
    }

    fun onDeleteGoal(goalId : String){

        firebaseAuth.currentUser?.uid?.let{ uid->
            db.collection("goals")
                .document(uid)
                .collection("userGoals")
                .document(goalId)
                .delete()
                .addOnCompleteListener(OnCompleteListener {task ->
                    if(task.isSuccessful){
                        viewModelScope.launch {
                            flow.emit(MainFlow.toastSuccess("Goal is Deleted"))
                            fetchGoals()
                        }

                    }
                    else{
                        viewModelScope.launch {
                            flow.emit(MainFlow.toastSuccess(task.exception.toString()))
                        }
                    }
                })

        }
    }
    fun onUpdateGoal(navController: NavController,goal: String, habit:String, period:Int, type :String, goalId: String){

        val updates = mapOf(
            "goal" to goal,
            "habit" to habit,
            "period" to period,
            "type" to type
        )
        firebaseAuth.currentUser?.uid?.let { uid ->

            db.collection("goals")
                .document(uid)
                .collection("userGoals")
                .document(goalId)
                .update(updates)
                .addOnCompleteListener(OnCompleteListener {
                    if(it.isSuccessful){
                        viewModelScope.launch {
                            flow.emit(MainFlow.toastSuccess("Successfully updated"))
                            fetchGoals()
                            navController.popBackStack()

                        }
                    }
                    else{
                        viewModelScope.launch {
                            flow.emit(MainFlow.toastSuccess(it.exception.toString()))
                        }
                    }
                })
        }
    }
    fun logout(){
        firebaseAuth.signOut()
    }

}