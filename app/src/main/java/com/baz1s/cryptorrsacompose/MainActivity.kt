package com.baz1s.cryptorrsacompose

import android.annotation.SuppressLint
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.List
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            CryptorPreview()
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Preview
    @Composable
    fun CryptorPreview() {
        val paddingValue = 20.dp
        val fontSize = 5.em
        val textFieldHeight = 70.dp

        val messageConverted = remember { mutableStateOf("") }
        val PRS = remember { mutableStateOf("") }

        val keyTextField = remember { mutableStateOf("") }
        val messageToCryptTextField = remember { mutableStateOf("") }
        val messageCryptedTextField = remember { mutableStateOf("") }
        val isEncoderSwitch = remember { mutableStateOf(true) }
        val switchText = remember { mutableStateOf("Encoder") }

        val coroutineScope = rememberCoroutineScope()
        val snackbarHostState = remember { mutableStateOf(SnackbarHostState()) }

        var messageToCryptSave = ""
        var keySave = ""
        var isParametersFilled = false

        val decoder = Decoder()
        val encoder = Encoder()

        var showFilePicker by remember { mutableStateOf(false) }

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
                horizontalAlignment = Alignment.End
            )
            {
                Row(horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.width(90.dp)) {
                        Text(text = switchText.value, fontSize = fontSize)
                    }
                    Switch(checked = isEncoderSwitch.value, onCheckedChange = {
                        isEncoderSwitch.value = it
                        if (isEncoderSwitch.value) switchText.value = "Encoder"
                        else switchText.value  = "Decoder"
                    })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween){
                    Box(
                        modifier = Modifier.width(260.dp),
                    ){
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                ,
                            value = messageToCryptTextField.value,
                            onValueChange = { newText -> messageToCryptTextField.value = newText},
                            placeholder = { Text(text = "Write your message here")}
                        )
                    }
                    FloatingActionButton(onClick = {showFilePicker = true}) {
                        Icon(Icons.Rounded.List, "")
                    }
                }
                FilePicker(show = showFilePicker, fileExtensions = listOf("txt")) { file ->
                    showFilePicker = false
                }
                Spacer(modifier = Modifier.weight(1f))
                SnackbarHost(hostState = snackbarHostState.value)
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                )
                {
                    Box(modifier = Modifier.width(150.dp)) {
                        OutlinedTextField(
                            value = keyTextField.value,
                            onValueChange = {newText -> keyTextField.value = newText},
                            placeholder = { Text(text = "Key")},
                        )
                    }
                    Button(
                        modifier = Modifier.height(50.dp),
                        onClick = {
                            coroutineScope.launch {
                                if (!encoder.keyCheck(keyTextField.value)){
                                    snackbarHostState.value.showSnackbar("Wrong key type, try again")
                                    isParametersFilled = false
                                }
                                else{
                                    snackbarHostState.value.showSnackbar("Saved")
                                    keySave = keyTextField.value
                                    messageToCryptSave = messageToCryptTextField.value
                                    isParametersFilled = true
                                }
                            }
                        })
                    {
                        Text(text = "Save", fontSize = fontSize)
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
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(textFieldHeight),
                    value = messageCryptedTextField.value,
                    onValueChange = {newText -> messageCryptedTextField.value = newText},
                    placeholder = { Text(text = "Crypted message here") }
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(textFieldHeight),
                    value = messageConverted.value,
                    onValueChange = {newText -> messageConverted.value = newText},
                    placeholder = { Text(text = "Converted message") }
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(textFieldHeight),
                    value = PRS.value,
                    onValueChange = {newText -> PRS.value = newText},
                    placeholder = { Text(text = "PRS") }
                )
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End) {
                    Box(){
                        Button(
                            modifier = Modifier.height(50.dp),
                            onClick = {
                                coroutineScope.launch {
                                if (isParametersFilled){
                                    if (isEncoderSwitch.value) {
                                        try { encoder.setMessage(messageToCryptSave, keySave) }
                                        catch (e: IndexOutOfBoundsException){ snackbarHostState.value.showSnackbar("Cryption failed") }
                                        messageCryptedTextField.value = encoder.getFinalMessage()

                                        messageConverted.value = encoder.getConvertedMessage()
                                        PRS.value = encoder.getGamma()
                                    }
                                    else{
                                        try { decoder.setMessage(messageToCryptSave, keySave) }
                                        catch (e: IndexOutOfBoundsException){ snackbarHostState.value.showSnackbar("Cryption failed") }
                                        messageCryptedTextField.value = decoder.getFinalMessage()

                                        messageConverted.value = decoder.getConvertedMessage()
                                        PRS.value = decoder.getGamma()
                                    }
                                }
                                else{ snackbarHostState.value.showSnackbar("Cryption failed") }
                                }
                            }) {
                            Text(text = "Crypt", fontSize = fontSize)
                        }
                    }
                }
            }
        }
    }
}