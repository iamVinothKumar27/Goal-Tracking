package com.example.goal

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.goal.data.Goal
import com.example.goal.data.MainFlow
import com.example.goal.ui.theme.GoalTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var viewmodel: MainViewmodel
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewmodel = ViewModelProvider(this).get(MainViewmodel::class)
        initObservers()
        viewmodel.fetchUsername()
        viewmodel.fetchGoals()
        setContent {
            window.statusBarColor = resources.getColor(R.color.silver)
            var navController = rememberNavController()
            GoalTheme {
                    NavHost(navController, startDestination = if(viewmodel.firebaseAuth.currentUser!=null) "main" else "login"
                    ){
                        composable(route = "main") {
                            MainPage(viewmodel, navController)

                        }
                        composable("edit/{goalId}/{goal}/{habit}/{period}/{type}/{created}"){
                           val goalId  = it.arguments?.getString("goalId")
                           val goal  = it.arguments?.getString("goal")
                           val habit  = it.arguments?.getString("habit")
                           val period  = it.arguments?.getString("period")?.toIntOrNull()
                           val type  = it.arguments?.getString("type")
                           val created  = it.arguments?.getString("created")?.toLongOrNull()
                            CreateHabit(navController,Goal(goal,habit,period,type, created?:System.currentTimeMillis()),
                                onEdit = {goal ->
                                    if (goalId != null) {
                                        viewmodel.onUpdateGoal(navController, goal = goal.goal ?:"", habit = goal.habit?:"",
                                            period = goal.period?:0 , type = goal.type?: "", goalId = goalId,)
                                    }
                                }
                                )
                        }
                        composable("login") {
                            Login(navController){email, password ->
                              viewmodel.login( navController =  navController, email = email, password = password)
                            }
                        }
                        composable("signup"){
                            SignUp(navController){ name, email, password , confirmPass ->
                                viewmodel.signup(navController = navController, email = email, password =  password, name = name, confirmPass = confirmPass)
                            }
                        }
                        composable("yourGoals") {
                            YourGoalsPage(viewmodel,navController)
                        }
                        composable("yourHabits") {
                            YourHabitsPage(viewmodel, navController)
                        }
                        composable("onGoalDetails/{goal}/{habit}/{period}/{completedDates}/{type}/{created}/{isCompleted}") {
                            val goal  = it.arguments?.getString("goal")
                            val habit  = it.arguments?.getString("habit")
                            val dates  = it.arguments?.getString("completedDates")
                            val period  = it.arguments?.getString("period")?.toIntOrNull()
                            val type  = it.arguments?.getString("type")
                            val created  = it.arguments?.getString("created")?.toLongOrNull()
                            val isCompleted  = it.arguments?.getString("isCompleted").toBoolean() ?: false
                            val completedDates = dates?.split(",") ?: emptyList()
                            Log.d("pathris",completedDates.toString() )
                            JournalingPage(navController, Goal(goal,habit,period,type,created, completedDates = completedDates, completed = isCompleted))
                        }

                    }
            }
        }
    }

    private fun initObservers() {
        viewmodel.viewModelScope.launch {
            viewmodel.flow.collect { it ->
                when(it){
                     is MainFlow.toastSuccess->{
                         Toast.makeText(applicationContext, it.msg, Toast.LENGTH_SHORT).show()
                     }
                }
            }
        }
    }
}

