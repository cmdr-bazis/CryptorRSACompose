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
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.baz1s.cryptorrsacompose.ui.theme.CryptorRSAComposeTheme
import org.w3c.dom.Text

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val paddingValue = 8.dp

        setContent {
            Column(modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Blue)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.Red)
                        .weight(1f),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        modifier = Modifier
                            .padding(paddingValue)
                            .background(color = Color.Blue)
                            .fillMaxSize()
                    ) {
                        TextField(value = "Write your message here", onValueChange = {})

                        Row(
                            modifier = Modifier
                                .padding(paddingValue)
                                .background(color = Color.Red),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            TextField(value = "Key", onValueChange = {})
                            Button(onClick = {}) {
                                Text(text = "Save")
                            }
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.Red)
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CryptorRSAComposeTheme {
        Greeting("Android")
    }
}