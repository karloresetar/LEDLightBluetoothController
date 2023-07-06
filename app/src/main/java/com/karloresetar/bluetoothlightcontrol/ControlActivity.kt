@file:Suppress("DEPRECATION")

package com.karloresetar.bluetoothlightcontrol

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.util.UUID

class ControlActivity: AppCompatActivity(){

    //koriste se za koristenje u drugim klasama
    companion object{
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_progress: ProgressDialog
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String
    }


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.control_layout)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            m_address = intent.getStringExtra(SelectDeviceActivity.EXTRA_ADDRESS).toString()
            ConnectToDevice(this).execute()

            val controlLedOnButton: Button = findViewById(R.id.control_led_on)
            controlLedOnButton.setOnClickListener { sendCommand("A") }

            val controlLedOffButton: Button = findViewById(R.id.control_led_off)
            controlLedOffButton.setOnClickListener { sendCommand("B") }

            val controlLedBlinkButton: Button = findViewById(R.id.control_led_blink)
            controlLedBlinkButton.setOnClickListener { sendCommand("C") }

            val controlLedDisconnectButton: Button = findViewById(R.id.control_led_disconnect)
            controlLedDisconnectButton.setOnClickListener { disconnect() }

        }
    }

    private fun sendCommand(input: String){
        if(m_bluetoothSocket != null){
            try{
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
            } catch(e: IOException){
                e.printStackTrace()
            }
        }

    }

    private fun disconnect(){
        if(m_bluetoothSocket != null){
            try{
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnected = false
            } catch (e: IOException){
                e.printStackTrace()
            }
        }
        finish()

    }

    private class ConnectToDevice(c: Context) : AsyncTask<Void, Void, String>(){

        private var connectSuccess: Boolean = true
        private val context: Context

        init{
            this.context = c
        }

        override fun onPreExecute() {
            super.onPreExecute()
            m_progress = ProgressDialog.show(context,"Connecting...","pleaase wait")
        }

        @SuppressLint("MissingPermission")
        override fun doInBackground(vararg params: Void?): String? {
            try {
                if (m_bluetoothSocket == null || !m_isConnected) {
                    m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    m_bluetoothSocket!!.connect()
                }
            } catch (e: IOException) {
                connectSuccess = false
                e.printStackTrace()
            }
            return null
        }


        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!connectSuccess) {
                Log.i("data", "coudlnt connect")
            }else {
                m_isConnected = true
            }
            m_progress.dismiss()
        }
    }


}