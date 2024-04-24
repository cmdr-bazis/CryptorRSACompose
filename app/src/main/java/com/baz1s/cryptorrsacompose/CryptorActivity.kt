package com.baz1s.cryptorrsacompose

import android.annotation.SuppressLint
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
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope

class CryptorActivity : ComponentActivity() {

    private val mainPaddingValue = 15.dp
    private val elementPaddingValue = 2.dp
    private val fontSize = 5.em
    private val textFieldHeight = 150.dp

    private var numE = ""
    private var numD = ""
    private var numN = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent{
            MainLayOut()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun MainLayOut(){
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        val navController = rememberNavController()

        val items = listOf(
            NavDrawerItem.Cryptor,
            NavDrawerItem.Keygen
        )

        var selectedItemIndex by rememberSaveable { mutableStateOf(0) }

        ModalNavigationDrawer(
            drawerContent = {
                ModalDrawerSheet{
                    Spacer(modifier = Modifier.height(16.dp))
                    items.forEachIndexed { index, item ->
                        NavigationDrawerItem(
                            label = { Text(text = item.title)},
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
        }
    }

    @Composable
    fun LoadingView(mutableState: MutableState<Boolean>) {
        if (mutableState.value) {
            Column(modifier = Modifier
                .fillMaxSize()
                .background(Color.DarkGray.copy(0.5f))
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
        val pathToMessage = remember { mutableStateOf("") }

        val keyTextField = remember { mutableStateOf("") }
        val messageToCryptTextField = remember { mutableStateOf("") }
        val messageCryptedTextField = remember { mutableStateOf("") }
        val isEncoderSwitch = remember { mutableStateOf(true) }
        val switchText = remember { mutableStateOf("Encoder") }

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

            encoder.setMessage(messageToCryptSave, this.numN + " " + this.numE)
            messageCryptedTextField.value = encoder.getFinalMessage()
            messageConverted.value = encoder.getConvertedMessage()

            loadingViewIsShown.value = false
        }

        val decoderThread = Thread {
            loadingViewIsShown.value = true

            Log.d("DecoderThread", "Started")

            decoder.setMessage(messageToCryptSave, this.numN + " " + this.numD)
            messageCryptedTextField.value = decoder.getFinalMessage()
            messageConverted.value = decoder.getConvertedMessage()

            loadingViewIsShown.value = false
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
        )
        {
            Column(
                modifier = Modifier
                    .padding(mainPaddingValue)
                    .background(color = Color.White)
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
                            Text(text = switchText.value, fontSize = fontSize)
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
                                if (isParametersFilled){
                                    if (isEncoderSwitch.value) { encoderThread.start() }
                                    else { decoderThread.start() }
                                }
                                else{ snackbarHostState.value.showSnackbar("Cryption failed") }
                            }
                        }) {
                        Text(text = "Crypt", fontSize = fontSize)
                    }
                }
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

            loadingViewIsShown.value = false
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
                .padding(mainPaddingValue)
        ) {
            Row(modifier = Modifier.height(45.dp)){
                Text(text = "KeyGen", fontSize = fontSize)
            }
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Box(modifier = Modifier
                    .width(125.dp)
                    .padding(elementPaddingValue)) {
                    OutlinedTextField(
                        value = numETextField.value,
                        onValueChange = {newText -> numETextField.value = newText},
                        placeholder = { Text(text = "Exponent")},
                    )
                }
                Box(modifier = Modifier
                    .width(125.dp)
                    .padding(elementPaddingValue)) {
                    OutlinedTextField(
                        value = keySizeTextField.value,
                        onValueChange = {newText -> keySizeTextField.value = newText},
                        placeholder = { Text(text = "Size")},
                    )
                }
                Button(modifier = Modifier.height(50.dp),onClick = {
                    snackbarCoroutineScope.launch {
                        if (keyGen.parametersCheck(keySizeTextField.value, numETextField.value)){
                            exponentSave = numETextField.value
                            keySizeSave = keySizeTextField.value
                            isParametersFilled = true
                            snackbarHostState.value.showSnackbar("Saved")
                        }
                        else {
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
                placeholder = { Text(text = "Number N")}
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(elementPaddingValue)
                    .height(200.dp),
                value = numDTextField.value,
                onValueChange = { newText -> numDTextField.value = newText },
                placeholder = { Text(text = "Number D")}
            )
            Row(modifier = Modifier
                .fillMaxWidth()
                .height(70.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ){
                Button(modifier = Modifier
                    .height(50.dp)
                    .padding(elementPaddingValue)
                    .height(50.dp),
                    onClick = {
                        snackbarCoroutineScope.launch {
                            if (isParametersFilled){
                                keyGenThread.start()
                            }
                            else {
                                snackbarHostState.value.showSnackbar("Generation failed")
                            }
                        }
                }){
                    Text(text = "Generate", fontSize = fontSize)
                }
            }
        }
        LoadingView(mutableState = loadingViewIsShown)
    }
}