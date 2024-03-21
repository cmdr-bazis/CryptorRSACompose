package com.baz1s.cryptorrsacompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            CryptorPreview()
        }
}
@Preview
@Composable
fun CryptorPreview() {
    val paddingValue = 20.dp
    val keyTextField = remember { mutableStateOf("") }
    val messageToCryptTextField = remember { mutableStateOf("") }
    val messageCryptedTextField = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
    )
    {
        Column(
            modifier = Modifier
                .padding(paddingValue)
                .background(color = Color.White)
                .fillMaxSize()
                .weight(1f),
            verticalArrangement = Arrangement.SpaceBetween
        )
        {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = messageToCryptTextField.value,
                onValueChange = { newText -> messageToCryptTextField.value = newText},
                placeholder = { Text(text = "Write your message here")},
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                Box(modifier = Modifier.width(150.dp)) {
                    TextField(
                        value = keyTextField.value,
                        onValueChange = {newText -> keyTextField.value = newText},
                        placeholder = { Text(text = "Key")})
                }
                Box(){
                    Button(
                        modifier = Modifier.height(50.dp),
                        onClick = {}) {
                        Text(text = "Save", fontSize = 5.em)
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .padding(paddingValue)
                .fillMaxSize()
                .weight(1f),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = messageCryptedTextField.value,
                onValueChange = {newText -> messageCryptedTextField.value = newText},
                placeholder = {Text(text = "Crypted message here")}
            )
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End) {
                Box(){
                    Button(
                        modifier = Modifier.height(50.dp),
                        onClick = {}) {
                        Text(text = "Crypt", fontSize = 5.em)
                    }
                }
            }
        }
    }
}
}