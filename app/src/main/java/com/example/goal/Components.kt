package com.example.goal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun TextBox(title :String,text: String = "",onvalueChange: ((String) -> Unit)? = null ) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(color = colorResource(R.color.silver)),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        CustomText(title)
        MyTextField(text){
            if (onvalueChange != null) {
                onvalueChange(it)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTextField(text: String ="",onvalueChange: ((String) -> Unit)? =null){
    TextField(
        text,
        onValueChange = {
            if (onvalueChange != null) {
                onvalueChange(it)
            }
        },
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(4.dp))
            .background(color = Color.White)
            .border(width = 1.dp, shape = RoundedCornerShape(4.dp), color = Color.Gray)
    )
}

@Composable
fun ButtonGradient(text: String , onclick: (()->Unit)? =null){
    val gradient = Brush.horizontalGradient(
        listOf(
            Color(0xffFFA450),
            colorResource(R.color.orange)

        )
    )
    Box(
        modifier = Modifier.fillMaxWidth().wrapContentHeight().clip(
            RoundedCornerShape(4.dp)).background(gradient).clickable {
            if (onclick != null) {
                onclick()
            }
        },
        contentAlignment = Alignment.Center

    ) {
        Text(text, color = Color.White,
            modifier  = Modifier.padding(vertical = 16.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.W700
        )
    }
}
@Composable
fun TitleField(text :String){
    Text(
        text,

        fontSize = 44.sp,
        color = Color(0xFF2f2f2f),
        fontFamily = FontFamily(
            Font(R.font.nunito_bolod)
        ),
        fontWeight = FontWeight.ExtraBold,
    )
}
@Composable
fun CustomText(title: String){
    Text(
        title,
        fontSize = 14.sp,
        color = Color.DarkGray
    )
}

