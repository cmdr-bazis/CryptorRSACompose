package com.baz1s.cryptorrsacompose

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import kotlinx.coroutines.launch
import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent{
            CryptorPreview()
        }
    }

//    @Preview
//    @OptIn(ExperimentalPermissionsApi::class)
//    @Composable
//    private fun RequestPermission(){
//        val readPermissionState = rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)
//        val writePermissionState = rememberPermissionState(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE)
//
//
//        if (readPermissionState.status.isGranted && writePermissionState.status.isGranted){
//            Text(text = "Permissions granted")
//        }
//        else{
//            val textToShow = if (readPermissionState.status.shouldShowRationale && writePermissionState.status.shouldShowRationale){
//                "Read and write permission are used for read files to encode/decode and write crypted files"
//            } else {
//                "Permissions denied"
//            }
//
//            Text(text = textToShow)
//            Spacer(modifier = Modifier.height(8.dp))
//            Button(onClick = {
////                readPermissionState.launchPermissionRequest()
////                writePermissionState.launchPermissionRequest()
//            }){
//                Text(text = "Grant permission")
//            }
//
//        }
//    }


    private fun convertToPath(uriPath: String): String {
        val storageTemplate = "/storage/emulated/0/"
        val path = storageTemplate + uriPath.substringAfter(':')
        return path
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Preview
    @Composable
    fun CryptorPreview() {
        val paddingValue = 20.dp
        val elementPaddingValue = 2.dp
        val fontSize = 5.em
        val textFieldHeight = 70.dp

        val messageConverted = remember { mutableStateOf("") }
        val PRS = remember { mutableStateOf("") }
        val pathToMessage = remember { mutableStateOf("") }

        val keyTextField = remember { mutableStateOf("") }
        val messageToCryptTextField = remember { mutableStateOf("") }
        val messageCryptedTextField = remember { mutableStateOf("") }
        val isEncoderSwitch = remember { mutableStateOf(true) }
        val switchText = remember { mutableStateOf("Encoder") }

        val snackbarCoroutineScope = rememberCoroutineScope()
        val snackbarHostState = remember { mutableStateOf(SnackbarHostState()) }

        var messageToCryptSave = ""
        var keySave = ""
        var isParametersFilled = false

        val decoder = Decoder()
        val encoder = Encoder()

//        val readPermissionState = rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)
//        val writePermissionState = rememberPermissionState(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE)

        var showFilePicker by remember { mutableStateOf(false) } // for FilePicker MPFile Library

        val permissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {isGranted: Boolean ->
            if (isGranted){
                Log.d("Permission", "Granted")
            }
            else{
                Log.d("Permission", "Denied")
            }
        }

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        val result = remember { mutableStateOf("") }
        val fileChooserLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { uri ->
            if (uri != null) {
//                result.value = uri.path?.toUri().toString()
//                pathToMessage.value = convertToPath(result.value)
//
//                val inputStream: InputStream = File(convertToPath(result.value)).inputStream()
//                val lineList = ArrayList<String>()
//
//                inputStream.bufferedReader().forEachLine { lineList.add(it) }
//                val finalMessageToCrypt = lineList.toString()
//
//                messageToCryptTextField.value = finalMessageToCrypt
                pathToMessage.value = uri.data.toString()
            }
        }

        val encoderThread = Thread {
            encoder.setMessage(messageToCryptSave, keySave)
            messageCryptedTextField.value = encoder.getFinalMessage()
        }

        val decoderThread = Thread {
            decoder.setMessage(messageToCryptSave, keySave)
            messageCryptedTextField.value = decoder.getFinalMessage()
        }

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
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(elementPaddingValue),
                        value = messageToCryptTextField.value,
                        onValueChange = { newText -> messageToCryptTextField.value = newText},
                        placeholder = { Text(text = "Write your message here")}
                    )
//                    Button(onClick = {
//                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
////                        readPermissionState.launchPermissionRequest()
//                    }) {
//                        Text("R")
//                    }
//                    FloatingActionButton(
//                        onClick = {
////                            readPermissionState.launchPermissionRequest()
////                            fileChooserLauncher.launch(arrayOf("pdf", "txt", "text", "text/plain"))
//                            fileChooserLauncher.launch(intent)
//                        }) {
//                        Icon(Icons.Rounded.List, "")
//                    }
                }

//                Row(){
//                    OutlinedTextField(
//                        modifier = Modifier
//                            .fillMaxWidth(),
//                        value = pathToMessage.value,
//                        onValueChange = { newText -> pathToMessage.value = newText},
//                        placeholder = { Text(text = "path")}
//                    )
//                }

                Spacer(modifier = Modifier.weight(1f))
                SnackbarHost(hostState = snackbarHostState.value)
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                )
                {
                    Box(modifier = Modifier
                        .width(150.dp)
                        .padding(elementPaddingValue)) {
                        OutlinedTextField(
                            value = keyTextField.value,
                            onValueChange = {newText -> keyTextField.value = newText},
                            placeholder = { Text(text = "Key")},
                        )
                    }
                    Button(
                        modifier = Modifier.height(50.dp),
                        onClick = {
                            snackbarCoroutineScope.launch {
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
                        .height(150.dp)
                        .padding(elementPaddingValue),
                    value = messageCryptedTextField.value,
                    onValueChange = {newText -> messageCryptedTextField.value = newText},
                    placeholder = { Text(text = "Crypted message here") }
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(elementPaddingValue),
                    value = PRS.value,
                    onValueChange = {newText -> PRS.value = newText},
                    placeholder = { Text(text = "PRS") }
                )
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(
                        modifier = Modifier.height(50.dp),
                        onClick = {

                        }) {
                        Text(text = "Auto Generate")
                    }
                    Button(
                        modifier = Modifier.height(50.dp),
                        onClick = {
                            snackbarCoroutineScope.launch {
                                if (isParametersFilled){
                                    if (isEncoderSwitch.value) {
                                        encoderThread.start()
                                    }
                                    else{
                                        decoderThread.start()
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