package com.example.farmer

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.farmer.screens.CropViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text
import java.io.IOException
import java.util.UUID


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var bluetoothSocket: BluetoothSocket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
                  MyApp()
        }
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            // Bluetooth is not supported or not enabled
            // Handle this case
        }

        // Request runtime permissions if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }
    @Composable
    fun MyApp(viewModel: CropViewModel =  hiltViewModel()) {

        var receivedData by remember { mutableStateOf("") }
        val recieved_ = remember { mutableStateOf(1) }
        if (receivedData.isNotEmpty() && receivedData.length == 7 ) {
            recieved_.value = receivedData.toInt()
        }
        val context = LocalContext.current

        val soilnitrogen = recieved_.value % 100
        val soilphosphorus = (recieved_.value / 100) % 100
        val soilpotassium = (recieved_.value / 10000)
        val cropdata = viewModel.data.value.data?.toMutableList()
        val crop_nitrogen_index = remember { mutableStateOf(0) }
        val crop_phosphorus_index = remember { mutableStateOf(1) }
        val crop_potassium_index = remember { mutableStateOf(2) }
        val crop_nitrogen = cropdata?.elementAt(crop_nitrogen_index.value)
        val crop_phosphorus = cropdata?.elementAt(crop_phosphorus_index.value)
        val crop_potassium = cropdata?.elementAt(crop_potassium_index.value)
        val crop_nitrogen_average = ((crop_nitrogen?.maximum)?.plus((crop_nitrogen.minimum)))?.div(2)
        val crop_phosphorus_average = ((crop_phosphorus?.maximum)?.plus((crop_phosphorus.minimum)))?.div(2)
        val crop_potassium_average = ((crop_potassium?.maximum)?.plus((crop_potassium.minimum)))?.div(2)
        var UREArequired : Int? = (crop_nitrogen_average?.minus(soilnitrogen))
        var SSPrequired :Int? = (crop_phosphorus_average?.minus(soilphosphorus))
        var MOPrequired :Int? = (crop_potassium_average?.minus(soilpotassium))
        var UREA_required: Int? = UREArequired?.times(225 / 46)
        var SSP_required: Int? = SSPrequired?.times(225 / 18)
        var MOP_required: Int? = MOPrequired?.times(225 / 61)
        if (UREA_required != null) {
            if (UREA_required < 0){
                UREA_required = 0
            }
        }
        if (SSP_required != null) {
            if (SSP_required < 0){
                SSP_required = 0
            }
        }
        if (MOP_required != null) {

            if (MOP_required < 0){
                MOP_required = 0
            }
        }



        val (selectedOption, setSelectedOption) = remember { mutableStateOf(0) }


            if (viewModel.data.value.loading == true && recieved_.value == 1){
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator(  )
                }
            }
            else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                        Button(onClick = {
                            connectToDevice("00:18:E4:40:00:06")
                            // Replace with your HC-05 MAC address
                            receivedData = "Connecting..."
                        }) {
                            Text(text = "Connect to HC-05")
                        }
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // First option
                            RadioButton(
                                selected = selectedOption == 0,
                                onClick = {
                                    setSelectedOption(0)
                                    crop_nitrogen_index.value = 0
                                    crop_phosphorus_index.value = 1
                                    crop_potassium_index.value = 2

                                },
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text("RICE")

                            // Second option
                            RadioButton(
                                selected = selectedOption == 1,
                                onClick = {
                                    setSelectedOption(1)
                                    crop_nitrogen_index.value = 3
                                    crop_phosphorus_index.value = 4
                                    crop_potassium_index.value = 5
                                },
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text("WHEAT")

                            // Third option
                            RadioButton(
                                selected = selectedOption == 2,
                                onClick = {
                                    setSelectedOption(2)
                                    crop_nitrogen_index.value = 6
                                    crop_phosphorus_index.value = 7
                                    crop_potassium_index.value = 8
                                },
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text("SUGARCANE")
                        }
                        Text(
                            text = "${ crop_nitrogen?.Crop }",
                            fontWeight = FontWeight.Bold,
                        )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ){
                        Text(text = "UREA",
                            fontWeight = FontWeight.Bold,)
                       OutlinedTextField(
                           value =  UREA_required.toString(),
                           onValueChange = {},
                           modifier = Modifier.fillMaxWidth(0.5f)
                       )
                        Text(text = "KG / HECTARE")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                        ){
                        Text(
                            text = "SSP",
                            fontWeight = FontWeight.Bold,
                        )
                        OutlinedTextField(
                            value =  SSP_required.toString(),
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth(0.5f)
                        )
                        Text(text = "KG / HECTARE")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ){
                        Text(text = "MOP",
                            fontWeight = FontWeight.Bold,)
                        OutlinedTextField(
                            value =  MOP_required.toString()
                            , onValueChange = {},
                            modifier = Modifier.fillMaxWidth(0.5f)
                        )
                        Text(text = "KG / HECTARE")
                    }
            }

        }

        // Launch a coroutine to listen for changes in received data
        LaunchedEffect(Unit) {
            while (true) {
                receivedData = listenForData()
            }
        }
    }

    private fun connectToDevice(macAddress: String) {
        val device: BluetoothDevice? = bluetoothAdapter.getRemoteDevice(macAddress)
        val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // Standard SerialPortService ID

        try {
            bluetoothSocket = device?.createRfcommSocketToServiceRecord(uuid)
            bluetoothSocket?.connect()

            // Display a toast message when connected successfully
            val context = applicationContext
            Toast.makeText(context, "Bluetooth Connected", Toast.LENGTH_SHORT).show()

            // Start a coroutine to listen for incoming data
            CoroutineScope(Dispatchers.IO).launch {
                listenForData()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            // Handle connection error
        } catch (e: SecurityException) {
            e.printStackTrace()
            // Handle security exception (permission denied)
            // Show a toast message or request permission again
        }
    }

    private suspend fun listenForData(): String {
        val buffer = ByteArray(1024)
        val inputStream = bluetoothSocket?.inputStream

        return withContext(Dispatchers.IO) {
            inputStream?.let { input ->
                try {
                    val bytes = input.read(buffer)
                    String(buffer, 0, bytes)
                } catch (e: IOException) {
                    e.printStackTrace()
                    "Error reading data"
                }
            } ?: ""
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            bluetoothSocket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
            // Handle socket closing error
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
    }
}








