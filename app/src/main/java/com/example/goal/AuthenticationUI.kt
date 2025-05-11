package com.example.goal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun SignUp(navController: NavHostController , onSignupClick : (String, String, String, String) ->Unit={_,_,_, _, ->}){
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.silver))
            .padding(horizontal = 40.dp)
            .padding(top =40.dp)
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
        ) {
            Box(modifier = Modifier.weight(0.8f)) {
                TitleField("Sign Up")
            }
            Row(verticalAlignment = Alignment.CenterVertically , modifier = Modifier.clickable { navController.navigate("login") }) {
                Text(
                    "Login",
                    fontWeight = FontWeight.W700,
                    fontSize = 14.sp,
                    color = colorResource(R.color.orange),
                    modifier = Modifier,

                    )
                Icon(
                    Icons.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = colorResource(R.color.orange)
                )
            }
        }
        Column (
            modifier = Modifier.padding(top=30.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)){
            TextBox("Name", text = name){
                name = it
            }
            TextBox("Email",email){
                email  = it
            }
            TextBox("Password", password){
                password = it
            }
            TextBox("Password Confirmation",confirmPassword){
                confirmPassword = it
            }
        }
        Spacer(
            modifier = Modifier
                .height(40.dp)
                .fillMaxWidth()
        )
        ButtonGradient("Sign Up"){
            onSignupClick(name, email, password,confirmPassword )
        }

        Text(
            "Or sign up with:",
            color = Color.DarkGray,
            modifier = Modifier
                .padding(top = 50.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Box(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(
                    RoundedCornerShape(4.dp)
                )
                .background(Color.White),
            contentAlignment = Alignment.Center

        ) {
            Image(
                painter = painterResource(R.drawable.google_icon),
                contentDescription = null,
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .size(20.dp)
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(navController: NavHostController, onLoginClick: (String, String) -> Unit) {
    var email by remember{ mutableStateOf("") }
    var password by remember{ mutableStateOf("") }

    var checked by remember { mutableStateOf(false) }
        Column (
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(R.color.silver))
                .padding(  40.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 40.dp)
            ) {
                Box(modifier = Modifier.weight(0.8f)) {
                    TitleField("Log In")
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
                    navController.navigate("signup")
                }) {
                    Text(
                        "Sign Up",
                        fontWeight = FontWeight.W700,
                        fontSize = 14.sp,
                        color = colorResource(R.color.orange),
                        modifier = Modifier,

                        )
                    Icon(
                        Icons.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = colorResource(R.color.orange)
                    )
                }
            }
            Column(
                modifier = Modifier.padding(top = 50.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                TextBox("Email" , email){
                    email = it
                }
                TextBox("Password", password){
                    password =it
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(top=20.dp)
            ) {
                Row(modifier = Modifier.weight(0.6f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                        Checkbox(
                            checked,
                            onCheckedChange = {
                                checked = !checked
                            },
                            enabled = true
                        )
                    }

                CustomText("Remember me")
                    }
                Text(
                    "Forgot Password?",
                    color = colorResource(R.color.orange),
                    modifier = Modifier.weight(0.4f),
                    textAlign = TextAlign.End
                )
            }
            Spacer(
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth()
            )
            ButtonGradient("Log In"){
                onLoginClick(email, password)
            }

            Text(
                "Or log in  with:",
                color = Color.DarkGray,
                modifier = Modifier
                    .padding(top = 50.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Box(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(
                        RoundedCornerShape(4.dp)
                    )
                    .background(Color.White),
                contentAlignment = Alignment.Center

            ) {
                Image(
                    painter = painterResource(R.drawable.google_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .size(20.dp)
                )
            }

        }
}

@Composable
fun ForgotPassword(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.silver))
            .padding(40.dp)
            .padding(top=150.dp)
    ) {
        CustomText(
            "Enter your email below, we will send instruction\n" +
                    "to reset your password"
        )
        Box(modifier = Modifier.padding(top =30.dp)) {
            MyTextField()
        }
        Box(modifier = Modifier.padding(top =30.dp)) {
           ButtonGradient("Submit")
        }

    }
}
