package com.example.goal

import android.os.Build
import android.util.Log

import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.goal.data.Goal
import com.example.goal.data.GoalWithId
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitItem(goal : Goal?=null ,
              onCheckboxClick: ((String, Long) -> Unit)? = null
){

    val currentDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
    var containsToday = false
    if(goal?.completedDates?.contains(currentDate) == true){
        containsToday = true
    }
    var checked by remember { mutableStateOf(containsToday) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (checked)
                    Color(0xFFEDFFF4)
                else Color(0xFFfbfbfb)
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ){

        Text(
            goal?.habit ?:"",
            modifier = Modifier
                .wrapContentHeight()
                .weight(0.8f)
            ,
            color = if(!checked) Color.Black else Color(0xFF37C871),
            fontSize = 16.sp,
            fontWeight = FontWeight.W500,
        )

        CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
            Checkbox(
                checked = checked,
                onCheckedChange = {
                    checked = !checked
                    if(checked ) onCheckboxClick?.let {
                        it1 ->
                        goal?.created?.let { created ->
                            it1("add", created
                            )
                        }

                    }
                    else onCheckboxClick?.let { it1 -> it1("minus", goal?.created ?:0L) }
                },
                enabled = true,
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF37C871))
            )
        }


    }
}


@Composable
fun SettingsItem(title :String , onClick: (() -> Unit)?=null){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(9.dp))
            .background(Color(0xFFfbfbfb))
            .clickable (indication = null, interactionSource = remember { MutableInteractionSource() }){
                onClick?.invoke()
            }
        ,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(title,
            modifier = Modifier
                .padding(12.dp)
                .weight(0.8f),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
            )
        Icon(
            Icons.Filled.KeyboardArrowRight,
            contentDescription = null
        )

    }
}

@Composable
fun SettingsPage(viewmodel: MainViewmodel, navController: NavController){
    var showAlertDialog by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(color = colorResource(R.color.silver))
            .padding(10.dp)
            .padding(vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ){
        Text("Settings", fontSize = 29.sp, fontWeight = FontWeight.Bold)
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color.White)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)){
                SettingsItem("Account")
                SettingsItem("Terms and Condition")
                SettingsItem("Policy")
                SettingsItem("About App")
                SettingsItem("Logout"){
                    showAlertDialog = true

                }


        }
        if(showAlertDialog){
            AlertDialog(
                onDismissRequest = {
                    showAlertDialog  = false
                },
                confirmButton = {
                    Button(
                        onClick = {
                           viewmodel.logout()
                            navController.navigate("login")
                        }
                    ) {
                        Text("Logout")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showAlertDialog = false
                        }
                    ) {
                        Text("cancel")
                    }
                },
                title = {
                    Text("Are you sure you want to logout?")
                }
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun JournalingPage(navController: NavController,goal: Goal){
    val textboxGradient = Brush.horizontalGradient(
        listOf(
            Color(0xFF37C871),
            Color(0xff5FE394)
        )
    )
    val s = SimpleDateFormat("dd/MM/yyyy")
    val date = goal.created?.let { Date(it) }
    val created =s.format(date)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.silver))
            .padding(15.dp)
            .padding(top = 30.dp)
            .verticalScroll(rememberScrollState())
    ){
        Text("${LocalDate.now().dayOfMonth} ${LocalDate.now().month} ${LocalDate.now().year}")
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier.padding(top =15.dp)
        ){
            Icon(Icons.Filled.ArrowBack, contentDescription = null,
                modifier = Modifier.clickable (indication = null, interactionSource = remember {
                    MutableInteractionSource()
                } ){
                    navController.popBackStack()
                }
                )
            Text("Goals: ${goal.goal}", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
        }
        if(goal.completedDates?.isNotEmpty() == true) {
            Column(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
            ) {
                if (goal.completedDates != null)
                    CalendarView(goal.completedDates)
            }
        }
        Column(
            modifier = Modifier
                .padding(top = 20.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(11.dp))
                    .background(Color(0xfffbfbfb))
                    .padding(12.dp)
            ){
                Row (verticalAlignment =Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(15.dp)
                ){

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier =Modifier.weight(0.7f)
                    ) {
                        Text("${goal.goal}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Text(if(goal.completed == true)"Achieved" else "Unachieved",
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(35.dp))
                            .background( if(goal.completed == true )  Color(0xffd7ffe7) else Color.LightGray)
                            .padding(10.dp),
                        style = if(goal.completed == true )TextStyle(textboxGradient) else TextStyle())

                }


                    Row(modifier = Modifier.padding(top = 20.dp)) {
                        Text(
                            "Habit Name",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            modifier = Modifier.weight(0.8f)
                        )
                        Text("${goal.habit}", fontWeight = FontWeight.Normal, fontSize = 16.sp)
                    }
                    Row(modifier = Modifier.padding(top = 20.dp)) {
                        Text(
                            "Target:",
                            fontSize = 16.sp,
                            modifier = Modifier.weight(0.8f)
                        )
                        Text("${goal.period} days", fontWeight = FontWeight.Normal, fontSize = 16.sp)
                    }
                    Row(modifier = Modifier.padding(top = 20.dp)) {
                        Text(
                            "Days Complete:",
                            fontSize = 16.sp,
                            modifier = Modifier.weight(0.8f)
                        )
                        val completedDates = goal.completedDates?.filter {
                            it.isNotEmpty() && it != "null"
                        }
                        Text(
                            if(completedDates?.isEmpty() ==true )
                                "0 day"
                            else
                                "${completedDates?.size ?:0 } days",

                            fontWeight = FontWeight.Normal, fontSize = 16.sp)
                    }

                    Row(modifier = Modifier.padding(top = 20.dp)) {
                        Text(
                            "Habit Type",
                            fontSize = 16.sp,
                            modifier = Modifier.weight(0.8f)
                        )
                        Text("${goal.type}", fontWeight = FontWeight.Normal, fontSize = 16.sp)
                    }
                Row(modifier = Modifier.padding(top = 20.dp)) {
                    Text(
                        "Created On",
                        fontSize = 16.sp,
                        modifier = Modifier.weight(0.8f)
                    )
                    Text(created.toString(), fontWeight = FontWeight.Normal, fontSize = 16.sp)
                }
                }
            }
        }
    }



@Composable
fun CalendarView(completedDates: List<String>?) {
    val currentMonth = remember { mutableStateOf(Calendar.getInstance()) }
    val calendar = currentMonth.value
    fun changeMonth(delta: Int) {
        val newCalendar = Calendar.getInstance().apply {
            time = calendar.time
            add(Calendar.MONTH, delta) // Change the month by delta
        }
        currentMonth.value = newCalendar // Set the new Calendar instance
    }

    val gradient = Brush.horizontalGradient(
        listOf(
            Color(0xffFFA450),
            colorResource(R.color.orange)

        ))
    Column(
        modifier = Modifier
            .background(Color.White)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(25.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.KeyboardArrowLeft, null,
                modifier = Modifier.clickable {
                   // changeMonth(calendar, -1)
                    changeMonth(-1)

                },
            )

            Text(
                text = SimpleDateFormat("MMMM\n  yyyy").format(calendar.time),
                style = TextStyle(gradient)
            )
            Icon(
                Icons.Filled.KeyboardArrowRight, null,
                modifier = Modifier.clickable {
                    changeMonth(1)
                },
            )
        }

        // Days of the week header
        Row {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach {
                Text(
                    it,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
        val completedDatesFormatted = completedDates
            ?.takeIf { it.isNotEmpty() }
            ?.filter { it.isNotEmpty() }
            ?.mapNotNull {
                try {
                    SimpleDateFormat("yyyy-MM-dd").parse(it)?.let { date ->
                        SimpleDateFormat("yyyy-MM-dd").format(date)
                    }
                } catch (e: ParseException) {
                    null // Handle invalid date format gracefully
                }
            }
            ?.toSet() ?: emptySet()
        // Days grid
        val daysInMonth = getDaysInMonth(calendar)
        LazyVerticalGrid(

            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            verticalArrangement = Arrangement.spacedBy(25.dp),
            horizontalArrangement = Arrangement.spacedBy(25.dp)
        ) {
            daysInMonth.forEach { day ->
                val dateString = if (day != 0) {
                    val date = Calendar.getInstance().apply {
                        set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), day)
                    }
                    SimpleDateFormat("yyyy-MM-dd").format(date.time)
                } else {
                    ""
                }

                val isCompleted = dateString in completedDatesFormatted
                item {
                    Text(
                        text = if (day != 0) day.toString() else "",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .background(if (isCompleted) Color(0xff5FE394) else Color.Transparent)
                    )
                }
            }
        }
    }
    }

fun changeMonth(calendar: Calendar, delta: Int) {
    calendar.add(Calendar.MONTH, delta)
}

fun getDaysInMonth(calendar: Calendar): List<Int> {
    val daysInMonth = mutableListOf<Int>()
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
    val maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    // Add empty spaces for the days before the 1st day of the month
    repeat(firstDayOfWeek) {
        daysInMonth.add(0) // 0 means empty space
    }

    // Add days of the month
    for (day in 1..maxDays) {
        daysInMonth.add(day)
    }

    return daysInMonth
}
@Composable
fun ProgressReportPage(viewmodel: MainViewmodel, onClickProgress: ((Goal) -> Unit)?=null){
    val goalsData = viewmodel.goalsData.collectAsState()
    var dropdownPeriod by remember { mutableStateOf(false) }
    val gradient = Brush.horizontalGradient(
        listOf(
            Color(0xffFFA450),
            colorResource(R.color.orange)

        )
    )
    val completedTaskCount = goalsData.value ?.filter { goal ->
        goal.goal.completed ?:false
    }?.size ?: 0

    var progress:Float =0f
    if(goalsData.value.isNotEmpty())
        progress =( completedTaskCount.toFloat()/goalsData.value.size.toFloat() )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.silver))
            .padding(20.dp)
    )
    {
        Text("Progress", fontSize = 29.sp, fontWeight = FontWeight.Bold)
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ){
                Text("Progress Report", fontSize = 21.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.8f))
                Box(contentAlignment = Alignment.Center,
                modifier = Modifier
                    .wrapContentHeight()
                    .width(150.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0xFFe7e7e7))
                    .padding(vertical = 5.dp)
                    .padding(horizontal = 15.dp)
                    .clickable {
                        dropdownPeriod = true
                    }
            ) {

                    Row {
                        Text("This Month", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Icon(
                            Icons.Filled.ArrowDropDown,
                            contentDescription = null,
                        )
                    }
                    DropdownMenu(
                        expanded = dropdownPeriod,
                        onDismissRequest = {
                            dropdownPeriod = false
                        }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text("30 days")
                            },
                            onClick = {
                                dropdownPeriod = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text("7 days")
                            },
                            onClick = {
                                dropdownPeriod = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text("100 days")
                            },
                            onClick = {
                                dropdownPeriod = false
                            }
                        )
                    }
                }

        }
        Column(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 10.dp)
            .clip(RoundedCornerShape(10.dp))

            .background(Color.White)
            .padding(12.dp)
        ){
            Text("Your Goals", fontSize = 21.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = progress,
                    color = colorResource(R.color.orange),
                    strokeWidth = 10.dp,
                    strokeCap = StrokeCap.Round,
                    trackColor = colorResource(R.color.silver),
                    modifier = Modifier.size(80.dp)
                )
                Text(
                    text = "${(progress * 100).toInt()}%", // Convert progress to percentage
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.orange)
                    )
                )
            }
            Text("${completedTaskCount} Habits of Goals Acheived",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                style = TextStyle(
                    gradient
                )
            )
            Text("${goalsData.value.size - completedTaskCount} Habits goal has'nt acheived",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 40.dp),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                color = Color(0xFFa2a2a2)
            )
            Column(verticalArrangement = Arrangement.spacedBy(15.dp)){
                goalsData.value.forEach {
                    ProgressItem(it.goal){
                        onClickProgress?.invoke(it.goal)
                    }
                }
            }

        }


    }
}

@Composable
fun ProgressItem(goal: Goal,onClick:()->Unit){
    val textboxGradient = Brush.horizontalGradient(
        listOf(
            Color(0xFF37C871),
            Color(0xff5FE394)
        )
    )
    var progress:Float =0f
    val completed = goal.completed
    if((goal.period ?: 0) > 0)
        progress =( (goal.completedDates?.size?.toFloat() ?:0f)/ (goal.period?.toFloat() ?:1f) )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(11.dp))
            .background(Color(0xfffbfbfb))
            .padding(12.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }) {
                onClick()
            }
    ){
        Row (verticalAlignment =Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(15.dp)
            ){
            CircularProgressIndicator(
                progress = progress,
                color = Color(0xFF37c671),
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round,
                trackColor = colorResource(R.color.silver),
                modifier = Modifier
                    .size(50.dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier =Modifier.weight(0.7f)
            ) {
                Text(goal.goal ?:"", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("${goal.completedDates?.size ?:0} from ${goal.period} days target", fontSize = 14.sp, color = colorResource(R.color.font_black))
            }

            Text(if(completed ?:false) "Achieved" else "Unachieved",
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(35.dp))
                    .background(if (completed ?:false) Color(0xffd7ffe7) else Color(0xFF959595))
                    .padding(10.dp),
                style = if(completed ?:false) TextStyle(textboxGradient) else TextStyle ())
        }
    }
}

@Composable
fun DeleteSuccess(onClick: () -> Unit){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(6.dp))
                .background(Color.White)
                .padding(horizontal = 20.dp)
                .padding(bottom = 30.dp)
            ,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ){
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.weight(0.9f))
                Icon(
                    Icons.Filled.Close,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .clickable {
                            // onClickClose()
                        }
                )
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = Color(0xFFe0e0e0))
            )
            Box (Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ){
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(vertical = 20.dp)
                        .size(60.dp),
                    tint = Color.DarkGray
                )
                Box(contentAlignment = Alignment.Center,

                    modifier = Modifier
                        .padding(start = 60.dp)
                        .size(25.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black)
                    ){
                Icon(
                    Icons.Filled.Done,
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp),
                    tint = Color.Green
                )
                }

            }
            Text("List has been Deleted",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            ButtonGradient("Ok"){
                onClick()
            }

        }
    }

@Composable
fun DeletePage(onDeleteClick: (() -> Unit)? =null, onCancelClick:()->Unit){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White)
            .padding(horizontal = 20.dp)
        ,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ){
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.weight(0.9f))
            Icon(
                Icons.Filled.Close,
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .clickable {
                        onCancelClick()
                    }
            )
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(color = Color(0xFFe0e0e0))
        )
        Box (Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ){
            Icon(
                Icons.Filled.Delete,
                contentDescription = null,
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .size(60.dp),
                tint = Color.DarkGray
            )

        }
        Text("Are you sure you want to delete?",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        ButtonGradient("Delete"){
            onDeleteClick?.invoke()
        }
        Text("Cancel",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 50.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {
                    onCancelClick()
                }
            ,
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun GoalItem(goal : Goal?= null, onEditClick : ()->Unit, onDeleteClick: ()->Unit){
    var clickMore by remember{
        mutableStateOf(false)
    }
    var  progress =0f
    progress =(goal?.completedDates?.size?.toFloat() ?:0f) / (goal?.period?.toFloat()?:0f)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xfffbfbfb))
            .padding(vertical = 11.dp)
            .padding(horizontal = 15.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
        Row {
            Text(
                goal?.goal ?:"",
                color = colorResource(R.color.font_black),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(0.9f)
            )
            Row{
            Icon(
                Icons.Filled.MoreVert,
                contentDescription = null,
                modifier = Modifier
                    .size(18.dp)
                    .clickable {
                        clickMore = true
                    }
            )
            if(clickMore){
                DropdownMenu(
                    expanded = clickMore,
                    onDismissRequest = {
                        clickMore = !clickMore
                    }
                ) {
                    DropdownMenuItem(
                        text = {
                            Text("Edit")
                        },
                        onClick = {
                            clickMore = false
                            onEditClick()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text("Delete")
                        },
                        onClick = {
                            clickMore = false
                            onDeleteClick()
                        }
                    )
                }
                }
            }

        }
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .padding(end = 20.dp)
                .fillMaxWidth()
                .height(14.dp),
            color  = colorResource(R.color.orange),
            strokeCap = StrokeCap.Round,
            trackColor = colorResource(R.color.sandal)
        )

        Text(
            "${goal?.completedDates?.size ?:0} from ${goal?.period} days target",
                color = colorResource(R.color.font_black),
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.nunito_bolod)),
            fontWeight = FontWeight.Medium
        )
        Text(
            goal?.type ?:"",
                color = Color(0xFFff5c00),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }

}

@Composable
fun CreateHabit(navController: NavController,updateGoal :Goal?=null, onClickClose : (()->Unit)?=null, onCreateHabit :((String, String, Int, String)->Unit)?= null,
                onEdit :((Goal)->Unit)?=null){
    var showDropdownPeriod by remember { mutableStateOf(false) }
    var showDropdownHabit by remember { mutableStateOf(false) }
    var isEdit = false

    var selectedPeriod by remember { mutableStateOf("${updateGoal?.period ?: 7} days" ) }
    var selectedPeriodInDays by remember { mutableStateOf(updateGoal?.period?:7) }
    var selectedHabitType by remember { mutableStateOf(updateGoal?.type ?:"Everyday") }
    var habit by remember { mutableStateOf(updateGoal?.habit ?:"") }
    var goal by remember { mutableStateOf(updateGoal?.goal ?:"") }
    if(updateGoal !=null){
        isEdit  = true
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(4.dp))
            .background(colorResource(R.color.silver))
            .padding(15.dp)
            .padding(top = 40.dp)
        ,
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ){
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if(isEdit) "Edit a Habit" else "Create New Habit Goal",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(0.8f)
            )
            Icon(
                Icons.Filled.Close,
                contentDescription = null,
                modifier =  Modifier.clickable {
                    if(isEdit) navController.popBackStack()
                    else
                    onClickClose?.invoke()
                }
            )
        }
        Spacer(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(Color.Gray)
                .padding(bottom = 20.dp)
        )
        TextBox("Your Goal", text = goal){
            goal =it
        }
        TextBox("Your Habit", text =  habit){
            habit = it
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text("Period", fontWeight = FontWeight.SemiBold, fontSize = 14.sp ,
                fontFamily = FontFamily(Font(R.font.nunito_bolod)),
                modifier = Modifier.weight(0.8f)
            )
            Box(contentAlignment = Alignment.Center,
                modifier = Modifier
                    .wrapContentHeight()
                    .width(150.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0xFFe7e7e7))
                    .padding(vertical = 5.dp)
                    .padding(horizontal = 15.dp)
                    .clickable {
                        showDropdownPeriod = true
                    }
            ) {
                Row (verticalAlignment = Alignment.CenterVertically){
                    Text(selectedPeriod, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Icon(
                        Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                    )
                }
                DropdownMenu(
                    expanded = showDropdownPeriod,
                    onDismissRequest = {
                        showDropdownPeriod = false
                    }
                ) {

                    DropdownMenuItem(
                        text = {
                            Text("7 days")
                        },
                        onClick = {
                            selectedPeriod = "7 days"
                            selectedPeriodInDays = 7
                            showDropdownPeriod = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text("21 days")
                        },
                        onClick = {
                            selectedPeriod = "21 days"
                            selectedPeriodInDays = 7
                            showDropdownPeriod = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text("30 days")
                        },
                        onClick = {
                            selectedPeriod = "30 days"
                            selectedPeriodInDays = 30
                            showDropdownPeriod = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text("90 days")
                        },
                        onClick = {
                            selectedPeriod = "90 days"
                            selectedPeriodInDays = 90
                            showDropdownPeriod = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text("100 days")
                        },
                        onClick = {
                            selectedPeriod = "100 days"
                            selectedPeriodInDays = 100
                            showDropdownPeriod = false
                        }
                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text("Habit Type", fontWeight = FontWeight.SemiBold, fontSize = 14.sp ,
                fontFamily = FontFamily(Font(R.font.nunito_bolod)),
                modifier = Modifier.weight(0.8f)
            )
            Box(contentAlignment = Alignment.Center,
                modifier = Modifier
                    .wrapContentHeight()
                    .width(150.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0xFFe7e7e7))
                    .padding(vertical = 5.dp)
                    .padding(horizontal = 15.dp)
                    .clickable {
                        showDropdownHabit = true
                    }
            ) {
                Row (verticalAlignment = Alignment.CenterVertically){
                    Text(selectedHabitType, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Icon(
                        Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                    )
                }
                DropdownMenu(
                    expanded = showDropdownHabit,
                    onDismissRequest = {
                        showDropdownHabit = false
                    }
                ) {
                    DropdownMenuItem(
                        text = {
                            Text("Everyday")
                        },
                        onClick = {
                            selectedHabitType = "Everyday"
                            showDropdownHabit = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text("Weekdays")
                        },
                        onClick = {
                            selectedHabitType = "Weekdays"
                            showDropdownHabit = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text("Weekends")
                        },
                        onClick = {
                            selectedHabitType = "Weekends"
                            showDropdownHabit = false
                        }
                    )
                }
            }
        }
        ButtonGradient( if(!isEdit)"Create New" else "Update"){
            if(isEdit)
                onEdit?.invoke(Goal(goal, habit, selectedPeriodInDays, selectedHabitType,updateGoal?.created))
            else
                onCreateHabit?.invoke(goal, habit, selectedPeriodInDays, selectedHabitType)
        }

    }
}
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(viewmodel: MainViewmodel, navController: NavController){
    val pagerState = rememberPagerState(initialPage = 0, pageCount = {3})
    val coroutineScope = rememberCoroutineScope()
    var showCreateHabitDialog by remember { mutableStateOf(false) }

    Scaffold (
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showCreateHabitDialog = true
                }
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = null
                )
            }
        },
        bottomBar = {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(bottom = 10.dp),
                indicator = {
                    listtabs->
                    Box(
                        Modifier
                            .tabIndicatorOffset(listtabs[pagerState.currentPage]) // Indicator offset based on selected tab
                            .background(color = colorResource(R.color.orange)) // Change to the color you want for the indicator
                            .height(4.dp)

                    )
                }
            ) {
                val tabs = listOf(Icons.Filled.Home, Icons.Filled.Star, Icons.Filled.Settings)
                tabs.forEachIndexed { index, icon ->
                    Tab(
                        modifier = Modifier.size(50.dp),
                        selected = index ==pagerState.currentPage,
                        onClick = {
                                coroutineScope.launch {
                                    pagerState.scrollToPage(index)
                                }
                        },
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = if(index == pagerState.currentPage) colorResource(R.color.orange) else Color.LightGray
                        )
                    }
                }

            }
        }
    ){padding->
        val goalsData= viewmodel.goalsData.collectAsState().value

        Column(modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.silver))){
            HorizontalPager(
            state = pagerState,
                modifier = Modifier.padding( padding)
            ) {currentPage->
                when(currentPage){
                    0->{
                        HomePage(navController,viewmodel, goalsData)
                        if(showCreateHabitDialog){
                            AlertDialog(
                                onDismissRequest = {
                                    showCreateHabitDialog = false
                                }
                            ){
                                CreateHabit(
                                    navController,
                                    onClickClose = {
                                        showCreateHabitDialog = false
                                    },
                                    onCreateHabit = { goal, habit, period, type ->
                                        viewmodel.addGoal(
                                            goal = Goal(
                                                goal,
                                                habit,
                                                period,
                                                type,
                                                System.currentTimeMillis()
                                            ),
                                            onSucess = {
                                                viewmodel.fetchGoals()
                                                showCreateHabitDialog = false
                                            }
                                        )
                                    })
                            }
                        }

                    }
                    1->{
                        ProgressReportPage(viewmodel){
                            val completedDatesString = it.completedDates?.joinToString(",")
                            navController.navigate("onGoalDetails/${it.goal}/${it.habit}/${it.period}/${completedDatesString}/${it.type}/${it.created}/${it.completed}"  )
                        }
                    }
                    2->{
                        SettingsPage(viewmodel,navController)
                    }
                }

            }
          }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
fun getTodayCompletedTasks(goalsData: List<GoalWithId>): Int {
    val currentDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

    return goalsData
        .map{ it.goal}
        .filter { it.completed == false }
        .mapNotNull { it.completedDates } // Extract the completedDates lists
        .flatten()
        .count { it == currentDate }
}
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomePage(navController: NavController,viewmodel: MainViewmodel, goalsData: List<GoalWithId>){
    viewmodel.completedTaskCount = getTodayCompletedTasks(goalsData)
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDeleteSuccessDialog by remember { mutableStateOf(false) }
    var deleteId by remember { mutableStateOf("") }
    val currentDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

    val incompleteGoalsData = goalsData.filter {
        it.goal.completed == false
    }
    Log.d("pathris", goalsData.toString())

    val gradient = Brush.horizontalGradient(
        listOf(
            Color(0xffFFA450),
            colorResource(R.color.orange)
        )
    )

       var progress:Float =0f
      if(goalsData.isNotEmpty())
            progress =( viewmodel.completedTaskCount.toFloat()/incompleteGoalsData.size.toFloat() )
    val date = LocalDate.now()
    val weekdays =listOf( "Mon", "Tue", "Wed", "Thu", "Fri", "Sat","Sun")

    AnimatedVisibility(showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            }
        ) {
            DeletePage(
                onDeleteClick = {
                    showDeleteDialog = false
                    showDeleteSuccessDialog = true
                    viewmodel.onDeleteGoal(deleteId)

                },
                onCancelClick = {
                    showDeleteDialog = false

                }
            )
        }
    }
    AnimatedVisibility (showDeleteSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteSuccessDialog = false
            }
        ) {
            DeleteSuccess() {
                showDeleteSuccessDialog = false
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.silver))
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ){
        Text(
            "${weekdays[date.dayOfWeek.value-1]}, ${date.dayOfMonth} ${date.month} ${date.year}",
            fontSize = 16.sp,
            color = colorResource(R.color.font_black),
            fontWeight = FontWeight.SemiBold
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)){
            Text(
                "Hello,",
                fontSize = 28.sp,
                fontFamily = FontFamily(Font(R.font.nunito_bolod)),
                color = colorResource(R.color.font_black),
                fontWeight = FontWeight.SemiBold
            )
            Text(
                viewmodel.username,
                fontSize = 28.sp,
                fontFamily = FontFamily(Font(R.font.nunito_bolod)),
                color = colorResource(R.color.orange),
                fontWeight = FontWeight.SemiBold
            )
        }
        Box(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.Transparent),
        ) {
            Box (modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(gradient),
                contentAlignment = Alignment.Center
            ){
                Row(
                    horizontalArrangement = Arrangement.spacedBy(30.dp),
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Box(contentAlignment = Alignment.CenterStart) {
                        CircularProgressIndicator(
                            progress = progress,
                            color = Color.White,
                            strokeWidth = 7.dp,
                            strokeCap = StrokeCap.Round,
                            trackColor = colorResource(R.color.sandal ),
                            modifier = Modifier
                                .size(80.dp)
                        )
                        Text(
                            text = "${(progress*100).toInt()}%",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(R.color.white),
                            modifier = Modifier.padding(start = 25.dp)
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            "${viewmodel.completedTaskCount}  of  ${incompleteGoalsData.size} habits",
                            fontSize = 20.sp,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text("Completed Today", fontSize = 20.sp, color = Color.White)
                    }
                }

            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 15.dp, end = 10.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Image(
                    painter = painterResource(R.drawable.banner_logo),
                    contentDescription = null,
                )
            }
        }

        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.White)
                .padding(14.dp)
        )
        {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            )
            {
                Text(
                    "Today Habit",
                    fontSize = 21.sp,
                    fontFamily = FontFamily(Font(R.font.nunito_bolod)),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.8f)
                )
                if(incompleteGoalsData.size>3) {
                    Text(
                        "see all",
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.nunito_bolod)),
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(
                            brush = gradient
                        ),
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            navController.navigate("yourHabits")
                        }
                    )
                }


            }
            Column(
                modifier = Modifier.padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                incompleteGoalsData.take(3).forEach {
                        it1 -> HabitItem(it1.goal){action, id->
                            if(action =="add"){
                                viewmodel.completedTaskCount +=1
                                viewmodel.addCurrentDateToGoal(it1.id)

                            }
                        else{
                            viewmodel.completedTaskCount -=1
                                viewmodel.removeCurrentDateFromGoal(it1.id)
                            }
                    viewmodel.fetchGoals()
                    }
                    }
                }

            }


            Column(
                modifier = Modifier
                    .padding(top = 30.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(Color.White)
                    .padding(14.dp)
            )
            {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                )
                {
                    Text(
                        "Your Goals",
                        fontSize = 21.sp,
                        fontFamily = FontFamily(Font(R.font.nunito_bolod)),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(0.8f)
                    )
                    if(goalsData.size>3) {
                        Text(
                            "see all",
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.nunito_bolod)),
                            fontWeight = FontWeight.Bold,
                            style = TextStyle(
                                brush = gradient
                            ),
                            modifier = Modifier.clickable {
                                navController.navigate("yourGoals")
                            }
                        )
                    }

                }

                Column(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    incompleteGoalsData.take(3).forEach {
                        GoalItem(it.goal,
                            onDeleteClick = {
                                showDeleteDialog = true
                                deleteId = it.id
                            },
                            onEditClick = {
                                navController.navigate("edit/${it.id}/${it.goal.goal}/${it.goal.habit}/${it.goal.period}/${it.goal.type}/${it.goal.created}")
                            }
                        )
                    }
                }
                }

            }
        }




@RequiresApi(Build.VERSION_CODES.O)
fun generateDates(): List<Pair<Pair<String,String>, LocalDate>> {
    val today = LocalDate.now()
    return (0 until 364).map { offset ->
        val date = today.minusDays(offset.toLong())
        val day = date.dayOfMonth.toString()
        val month = date.month.toString().substring(0, 3)
        Pair(Pair(day, month), date)
    }
}

    @Composable
    fun DateItem(
        index: Int,
        selectedIndex: Int,
        date: LocalDate,
        day: String,
        month: String,
        onClick: (Int) -> Unit
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .size(65.dp)
                .background(if (index != selectedIndex) Color.White else Color(0xFFffeddc))
                .border(
                    width = 1.dp,
                    color = if (index == selectedIndex) Color(0xffd9b6) else Color.LightGray,
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(10.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onClick(index)
                },

            ) {
            val gradient = Brush.horizontalGradient(
                listOf(
                    Color(0xffFFA450),
                    colorResource(R.color.orange)
                )
            )
            Text(
                day,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 21.sp,
                style = if (index == selectedIndex) TextStyle(
                    brush = gradient
                )
                else TextStyle(color = colorResource(R.color.font_black)),
                fontWeight = FontWeight.W600
            )
            Text(
                month,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                style = if (index == selectedIndex) TextStyle(
                    brush = gradient
                )
                else TextStyle(color = colorResource(R.color.font_black)),
                fontWeight = FontWeight.W500
            )
        }
    }



    @Composable
    fun YourGoalsPage(viewmodel: MainViewmodel, navController: NavHostController) {
        val goalsData = viewmodel.goalsData.collectAsState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(R.color.silver))
                .padding(20.dp)
        ) {


            Column(
                modifier = Modifier
                    .padding(top = 30.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(Color.White)
                    .padding(14.dp)
            )
            {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                )
                {
                    Icon(
                        Icons.Filled.ArrowBack, contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                navController.popBackStack()
                            }
                    )
                    Text(
                        "Your Goals",
                        fontSize = 21.sp,
                        fontFamily = FontFamily(Font(R.font.nunito_bolod)),
                        fontWeight = FontWeight.W500,
                        color = colorResource(R.color.font_black)
                    )

                }

                Column(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    goalsData.value.forEach {
                        GoalItem(it.goal,
                            onDeleteClick = {
                                viewmodel.onDeleteGoal(it.id)
                            },
                            onEditClick = {
                                navController.navigate("edit/${it.id}/${it.goal.goal}/${it.goal.habit}/${it.goal.period}/${it.goal.type}/${it.goal.created}")
                            })

                    }
                }

            }

        }
    }



@RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun YourHabitsPage(viewmodel: MainViewmodel, navController: NavHostController) {
        val goalsData = viewmodel.goalsData.collectAsState()
        val gradient = Brush.horizontalGradient(
            listOf(
                Color(0xffFFA450),
                colorResource(R.color.orange)

            )
        )
        var selectedDateIndex by remember { mutableStateOf(0) }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(R.color.silver))
                .padding(20.dp)
                .padding(top = 40.dp),
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Filled.ArrowBack, contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(indication = null,
                            interactionSource = remember { MutableInteractionSource() }) {
                            navController.popBackStack()
                        }
                )
                Text(
                    "Your Habits",
                    fontWeight = FontWeight.W500,
                    fontSize = 21.sp,
                    color = colorResource(R.color.font_black)
                )
            }
            val dates = generateDates()
            LazyRow(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            )
            {
                itemsIndexed(dates) { index, data ->
                    DateItem(
                        index = index,
                        selectedIndex = selectedDateIndex,
                        day = data.first.first,
                        month = data.first.second,
                        date = data.second
                    ) {
                        selectedDateIndex = it
                    }
                }
            }
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(Color.White)
                    .padding(14.dp)
            )
            {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                )
                {
                    Text(
                        "Today Habit",
                        fontSize = 21.sp,
                        fontFamily = FontFamily(Font(R.font.nunito_bolod)),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(0.8f)
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    goalsData.value.forEach {
                        HabitItem(it.goal) { action, id ->
                            if (action == "add") {
                                viewmodel.completedTaskCount += 1
                                viewmodel.addCurrentDateToGoal(it.id)
                            } else {
                                viewmodel.completedTaskCount -= 1
                                viewmodel.removeCurrentDateFromGoal(it.id)
                            }
                            viewmodel.fetchGoals()
                        }
                    }

                }
            }
        }
    }
