package com.karloresetar.bluetoothlightcontrol

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat

@Suppress("DEPRECATION")
@RequiresApi(Build.VERSION_CODES.M)
class SelectDeviceActivity : AppCompatActivity() {


    private lateinit var bluetoothManager: BluetoothManager
    private var bluetoothAdapter: BluetoothAdapter? = null
    private lateinit var m_pairedDevices: Set<BluetoothDevice>
    private val REQUEST_ENABLE_BLUETOOTH = 1

    companion object {
        val EXTRA_ADDRESS: String = "Device_address"
        private const val PERMISSION_REQUEST_CODE = 2
    }


    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_device_layout)

        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter


        if (bluetoothAdapter == null) {
            showToast(this,"Device does not support Bluetooth")
            return
        }


        if (!bluetoothAdapter!!.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH)
        } else {
            checkBluetoothPermission()
        }


        val refreshButton: Button = findViewById(R.id.select_device_refresh)
        refreshButton.setOnClickListener { pairedDeviceList() }



        Log.d("SelectDeviceActivity", "Inside onCreate()")
    }

    @SuppressLint("MissingPermission")
    private fun pairedDeviceList() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request the missing permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                PERMISSION_REQUEST_CODE
            )
            return
        }

        // Permission is already granted, continue with the logic
        Log.d("SelectDeviceActivity", "Bluetooth permission granted")

        m_pairedDevices = bluetoothAdapter!!.bondedDevices
        val list: ArrayList<BluetoothDevice> = ArrayList()

        if (!m_pairedDevices.isEmpty()) {
            for (device: BluetoothDevice in m_pairedDevices) {
                list.add(device)
                Log.i("device", "" + device)
            }
        } else {
            showToast(this, "No paired Bluetooth devices found")
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        val deviceList: ListView = findViewById(R.id.select_device_list)
        deviceList.adapter = adapter
        deviceList.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val device: BluetoothDevice = list[position]
            val address: String = device.address

            val intent = Intent(this, ControlActivity::class.java)
            intent.putExtra(EXTRA_ADDRESS, address)
            startActivity(intent)
        }
    }


    private fun checkBluetoothPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request the missing permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH),
                PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission is already granted, continue with the logic
            pairedDeviceList()
        }
    }




    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showToast(this, "Bluetooth permission granted")
                // Permission granted, continue with the logic
                pairedDeviceList()
            } else {
                showToast(
                    this,
                    "Bluetooth permission denied. This app requires Bluetooth permission to function. Please grant the permission in the app settings."
                )
            }
        }
    }





    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BLUETOOTH){
            if (resultCode == Activity.RESULT_OK){
                if(bluetoothAdapter!!.isEnabled){
                    showToast(this,"Bluetooth has been enabled")
                } else {
                    showToast(this,"Bluetooth has been disabled")
                }
            } else if(resultCode == Activity.RESULT_CANCELED) {
                showToast(this,"Bluetooth has been canceled")
            }
        }
    }
}