package com.baz1s.cryptorrsacompose

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import kotlinx.coroutines.launch
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class CryptorActivity : ComponentActivity() {

    private val mainPaddingValue = 15.dp
    private val elementPaddingValue = 2.dp
    private val fontSize = 5.em
    private val textFieldHeight = 150.dp

    private val isDarkTheme = false
    private val insetPadding = 20.dp

    private var numE = ""
    private var numD = ""
    private var numN = ""

    private var isKeyGenUsed = false
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent{
            MainLayOut()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview(
        uiMode = Configuration.UI_MODE_NIGHT_YES,
        name = "CryptorPreviewLight"
    )
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun MainLayOut(){
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        val navController = rememberNavController()

        val items = listOf(
            NavDrawerItem.Cryptor,
            NavDrawerItem.Keygen,
            NavDrawerItem.Signature
        )

        var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

        ModalNavigationDrawer(
            drawerContent = {
                ModalDrawerSheet{
                    Spacer(modifier = Modifier.height(16.dp))
                    items.forEachIndexed { index, item ->
                        NavigationDrawerItem(
                            label = { Text(text = item.title, fontSize = fontSize)},
                            selected = index == selectedItemIndex,
                            onClick = {
                                selectedItemIndex = index
                                navController.navigate(item.route) {
                                    navController.graph.startDestinationRoute?.let { route ->
                                        popUpTo(route) {
                                            saveState = true
                                        }
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                                scope.launch {
                                    drawerState.close()
                                }
                            },
                            icon = { Icon(imageVector = item.icon, contentDescription = item.title)},
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }
            },
            drawerState = drawerState
        ){
            ComposeNavigation(navController = navController)
        }
    }

    @Composable
    fun ComposeNavigation(navController: NavHostController){
        NavHost(navController = navController, startDestination = NavDrawerItem.Cryptor.route) {
            composable(NavDrawerItem.Cryptor.route){
                CryptorScreen()
            }
            composable(NavDrawerItem.Keygen.route){
                KeyGenScreen()
            }
            composable(NavDrawerItem.Signature.route){
                SignatureScreen()
            }
        }
    }


    @Composable
    fun LoadingView(mutableState: MutableState<Boolean>) {
        if (mutableState.value) {
            Column(modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray.copy(alpha = 0.5f))
            ){
                Spacer(modifier = Modifier.weight(1f))
                Row(modifier = Modifier
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Preview
    @Composable
    fun CryptorScreen() {
        val messageConverted = remember { mutableStateOf("") }

        val keyTextField = remember { mutableStateOf("") }
        val messageToCryptTextField = remember { mutableStateOf("") }
        val messageCryptedTextField = remember { mutableStateOf("") }
        val isEncoderSwitch = remember { mutableStateOf(true) }
        val switchText = remember { mutableStateOf("Encoder") }
        val messageCryptedConverted = remember { mutableStateOf("") }
        val checkBoxState = remember { mutableStateOf(false) }

        val snackbarCoroutineScope = rememberCoroutineScope()
        val snackbarHostState = remember { mutableStateOf(SnackbarHostState()) }

        val loadingViewIsShown = remember { mutableStateOf(false) }

        var messageToCryptSave = ""
        var keySave = ""
        var isParametersFilled = false

        val decoder = Decoder()
        val encoder = Encoder()

        val encoderThread = Thread {
            loadingViewIsShown.value = true

            Log.d("EncoderThread", "Started")

            if (isKeyGenUsed and checkBoxState.value) encoder.setMessage(messageToCryptTextField.value, this.numN + " " + this.numE)
            else encoder.setMessage(messageToCryptSave, keySave)

            messageCryptedTextField.value = encoder.getFinalMessage()
            messageConverted.value = encoder.getConvertedMessage()

            Log.d("EncoderCrypted", encoder.getCryptedMessage())

            loadingViewIsShown.value = false
        }

        val decoderThread = Thread {
            loadingViewIsShown.value = true

            Log.d("DecoderThread", "Started")

            if (isKeyGenUsed and checkBoxState.value) decoder.setMessage(messageToCryptTextField.value, this.numN + " " + this.numD)
            else decoder.setMessage(messageToCryptSave, keySave)

            messageCryptedTextField.value = decoder.getFinalMessage()
            messageConverted.value = decoder.getConvertedMessage()

            Log.d("DecoderCrypted", decoder.getCryptedMessage())

            loadingViewIsShown.value = false
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDarkTheme) Color.DarkGray else Color.White)
        )
        {
            Spacer(modifier = Modifier.height(insetPadding))
            Column(
                modifier = Modifier
                    .padding(mainPaddingValue)
                    .fillMaxSize()
                    .weight(1f),
                horizontalAlignment = Alignment.End
            )
            {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp), horizontalArrangement = Arrangement.End){
                    Row(verticalAlignment = Alignment.CenterVertically){
                        Box(modifier = Modifier.width(90.dp)) {
                            Text(text = switchText.value, fontSize = fontSize, color = if (isDarkTheme) Color.LightGray else Color.Black)
                        }
                        Switch(checked = isEncoderSwitch.value, onCheckedChange = {
                            isEncoderSwitch.value = it
                            if (isEncoderSwitch.value) switchText.value = "Encoder"
                            else switchText.value  = "Decoder"
                        })
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(elementPaddingValue)
                            .height(150.dp),
                        value = messageToCryptTextField.value,
                        onValueChange = { newText -> messageToCryptTextField.value = newText },
                        placeholder = { Text(text = "Write your message here") }
                    )
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
                    Box(modifier = Modifier
                        .width(150.dp)
                        .padding(elementPaddingValue)) {
                        OutlinedTextField(
                            value = keyTextField.value,
                            onValueChange = {newText -> keyTextField.value = newText},
                            placeholder = { Text(text = "Key")},
                        )
                    }
                    Column(horizontalAlignment = Alignment.Start) {
                        Checkbox(
                            checked = checkBoxState.value,
                            onCheckedChange = {checkBoxState.value = it}
                        )
                        Text(text = if (checkBoxState.value) "Generated keys" else "Use keys here")
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
                        },
                        enabled = !checkBoxState.value,
                        colors = if (checkBoxState.value) ButtonDefaults.buttonColors(containerColor = Color.DarkGray) else ButtonDefaults.buttonColors())
                    {
                        Text(text = "Save", fontSize = fontSize)
                    }
                }
            }
            Column(
                modifier = Modifier
                    .padding(mainPaddingValue)
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(textFieldHeight)
                        .padding(elementPaddingValue),
                    value = messageCryptedTextField.value,
                    onValueChange = {newText -> messageCryptedTextField.value = newText},
                    placeholder = { Text(text = "Crypted message here") }
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(textFieldHeight)
                        .padding(elementPaddingValue),
                    value = messageConverted.value,
                    onValueChange = {newText -> messageConverted.value = newText},
                    placeholder = { Text(text = "Converted message here") }
                )
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End) {
                    Button(
                        modifier = Modifier.height(50.dp),
                        onClick = {
                            snackbarCoroutineScope.launch {
                                if (isParametersFilled or isKeyGenUsed){
                                    if (isEncoderSwitch.value) { encoderThread.start() }
                                    else { decoderThread.start() }
                                }
                                else{ snackbarHostState.value.showSnackbar("Cryption failed") }
                            }
                        }) {
                        Text(text = if (isEncoderSwitch.value) "Encode" else "Decode", fontSize = fontSize)
                    }
                }
                Spacer(modifier = Modifier.height(insetPadding))
            }
        }
        LoadingView(mutableState = loadingViewIsShown)
    }

    @Composable
    @Preview
    fun KeyGenScreen(){
        val keySizeTextField = remember { mutableStateOf("") }
        val numNTextField = remember { mutableStateOf("") }
        val numDTextField = remember { mutableStateOf("") }
        val numETextField = remember { mutableStateOf("") }

        val loadingViewIsShown = remember { mutableStateOf(false) }
        val snackbarCoroutineScope = rememberCoroutineScope()
        val snackbarHostState = remember { mutableStateOf(SnackbarHostState()) }

        var exponentSave = ""
        var keySizeSave = ""
        var isParametersFilled = false


        val keyGen = RSAkeygen()

        val keyGenThread = Thread {
            loadingViewIsShown.value = true

            keyGen.setParameters(keySizeSave, exponentSave)
            keyGen.createKeys()

            numNTextField.value = keyGen.getNumN()
            numDTextField.value = keyGen.getNumD()

            this.numE = exponentSave
            this.numN = keyGen.getNumN()
            this.numD = keyGen.getNumD()

            this.isKeyGenUsed = true

            loadingViewIsShown.value = false
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDarkTheme) Color.DarkGray else Color.White)
        ) {
            Spacer(modifier = Modifier.height(insetPadding))
            Column(
                modifier = Modifier
                    .padding(mainPaddingValue)
            ) {
                Row(modifier = Modifier.height(45.dp)) {
                    Text(
                        text = "KeyGen",
                        fontSize = fontSize,
                        color = if (isDarkTheme) Color.LightGray else Color.Black
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .width(125.dp)
                            .padding(elementPaddingValue)
                    ) {
                        OutlinedTextField(
                            value = numETextField.value,
                            onValueChange = { newText -> numETextField.value = newText },
                            placeholder = { Text(text = "Exponent") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .width(125.dp)
                            .padding(elementPaddingValue)
                    ) {
                        OutlinedTextField(
                            value = keySizeTextField.value,
                            onValueChange = { newText -> keySizeTextField.value = newText },
                            placeholder = { Text(text = "Size") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                    }
                    Button(modifier = Modifier.height(50.dp), onClick = {
                        snackbarCoroutineScope.launch {
                            if (keyGen.parametersCheck(
                                    keySizeTextField.value,
                                    numETextField.value
                                )
                            ) {
                                exponentSave = numETextField.value
                                keySizeSave = keySizeTextField.value
                                isParametersFilled = true
                                snackbarHostState.value.showSnackbar("Saved")
                            } else {
                                isParametersFilled = false
                                snackbarHostState.value.showSnackbar("Wrong parameters, try again")
                            }
                        }
                    }
                    ) {
                        Text(text = "Save", fontSize = fontSize)
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                SnackbarHost(hostState = snackbarHostState.value)
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(elementPaddingValue)
                        .height(200.dp),
                    value = numNTextField.value,
                    onValueChange = { newText -> numNTextField.value = newText },
                    placeholder = { Text(text = "Number N") }
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(elementPaddingValue)
                        .height(200.dp),
                    value = numDTextField.value,
                    onValueChange = { newText -> numDTextField.value = newText },
                    placeholder = { Text(text = "Number D") }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(modifier = Modifier
                        .height(50.dp)
                        .padding(elementPaddingValue),
                        onClick = {
                            snackbarCoroutineScope.launch {
                                if (isParametersFilled) {
                                    keyGenThread.start()
                                } else {
                                    snackbarHostState.value.showSnackbar("Generation failed")
                                }
                            }
                        }) {
                        Text(text = "Generate", fontSize = fontSize)
                    }
                }
                Spacer(modifier = Modifier.height(insetPadding))
            }
        }
        LoadingView(mutableState = loadingViewIsShown)
    }

    @Preview
    @Composable
    fun SignatureScreen(){
        val messageToSignTextField = remember { mutableStateOf("") }
        val keyTextField = remember { mutableStateOf("") }
        val messageSignedTextField = remember { mutableStateOf("") }
        val isToSignSwitch = remember { mutableStateOf(true) }
        val switchText = remember { mutableStateOf("Sign") }
        val checkBoxState = remember { mutableStateOf(false) }
        val loadingViewIsShown = remember { mutableStateOf(false) }

        val snackbarCoroutineScope = rememberCoroutineScope()
        val snackbarHostState = remember { mutableStateOf(SnackbarHostState()) }

        var isParametersFilled = false
        var keySave = ""
        var messageToSignSave = ""

        var encoder = Encoder()
        var decoder = Decoder()


        val signThread = Thread {
            loadingViewIsShown.value = true

            if (isKeyGenUsed and checkBoxState.value) encoder.setMessage(messageToSignTextField.value, this.numN + " " + this.numD)
            else encoder.setMessage(messageToSignSave, keySave)

            messageSignedTextField.value = encoder.getFinalMessage()

            loadingViewIsShown.value = false
        }

        val checkSignThread = Thread {
            loadingViewIsShown.value = true

            if (isKeyGenUsed and checkBoxState.value) decoder.setMessage(messageToSignTextField.value, this.numN + " " + this.numE)
            else encoder.setMessage(messageToSignSave, keySave)

            messageSignedTextField.value = decoder.getFinalMessage()

            loadingViewIsShown.value = false
        }



        Column(modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
        ) {
            Column(modifier = Modifier.padding(mainPaddingValue)) {
                Spacer(modifier = Modifier.height(insetPadding))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically){
                    Box(modifier = Modifier.padding(elementPaddingValue)) {
                        Text(text = switchText.value, fontSize = fontSize)
                    }
                    Switch(checked = isToSignSwitch.value, onCheckedChange = {
                        isToSignSwitch.value = it
                        if (isToSignSwitch.value) switchText.value = "Sign"
                        else switchText.value  = "Check Sign"
                    })
                }
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(elementPaddingValue)
                        .height(150.dp),
                    value = messageToSignTextField.value,
                    onValueChange = { newText -> messageToSignTextField.value = newText },
                    placeholder = { Text(text = "Write your message here") }
                )
                Spacer(modifier = Modifier.weight(1f))
                SnackbarHost(hostState = snackbarHostState.value)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(elementPaddingValue),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                )
                {
                    Box(
                        modifier = Modifier
                            .width(150.dp)
                            .padding(elementPaddingValue)
                    ) {
                        OutlinedTextField(
                            value = keyTextField.value,
                            onValueChange = { newText -> keyTextField.value = newText },
                            placeholder = { Text(text = "Key") },
                        )
                    }
                    Column(horizontalAlignment = Alignment.Start) {
                        Checkbox(
                            checked = checkBoxState.value,
                            onCheckedChange = { checkBoxState.value = it }
                        )
                        Text(text = if (checkBoxState.value) "Generated keys" else "Use keys here")
                    }
                    Button(
                        modifier = Modifier.height(50.dp),
                        onClick = {
                            snackbarCoroutineScope.launch {
                                if (!encoder.keyCheck(keyTextField.value)) {
                                    snackbarHostState.value.showSnackbar("Wrong key type, try again")
                                    isParametersFilled = false
                                } else {
                                    snackbarHostState.value.showSnackbar("Saved")
                                    keySave = keyTextField.value
                                    messageToSignSave = messageToSignTextField.value
                                    isParametersFilled = true
                                }
                            }
                        },
                        enabled = !checkBoxState.value,
                        colors = if (checkBoxState.value) ButtonDefaults.buttonColors(containerColor = Color.DarkGray) else ButtonDefaults.buttonColors()
                    )
                    {
                        Text(text = "Save", fontSize = fontSize)
                    }
                }
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(elementPaddingValue)
                        .height(200.dp),
                    value = messageSignedTextField.value,
                    onValueChange = { newText -> messageSignedTextField.value = newText },
                    placeholder = { Text(text = "Signed message here") }
                )
                Row(modifier = Modifier
                    .padding(elementPaddingValue)
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = {
                        snackbarCoroutineScope.launch {
                            if (isParametersFilled or isKeyGenUsed){
                                if (isToSignSwitch.value) { signThread.start() }
                                else { checkSignThread.start() }
                            }
                            else{ snackbarHostState.value.showSnackbar("Sign failed") }
                        }
                    }) {
                        Text(text = if (isToSignSwitch.value) "Sign" else "Check sign", fontSize = fontSize)
                    }
                }
            }
        }
    }
}