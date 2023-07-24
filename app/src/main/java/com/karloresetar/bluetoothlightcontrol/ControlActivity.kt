@file:Suppress("DEPRECATION")

package com.karloresetar.bluetoothlightcontrol

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import androidx.appcompat.widget.SwitchCompat
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.util.UUID

class ControlActivity : AppCompatActivity() {

    companion object {
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_progress: ProgressDialog
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String
    }

    lateinit var brightnessInput: EditText
    lateinit var submitButton: Button
    lateinit var brightnessSlider: SeekBar
    lateinit var brightnessValueText: TextView
    lateinit var controlLedSwitch: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.control_layout)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            m_address = intent.getStringExtra(SelectDeviceActivity.EXTRA_ADDRESS).toString()
            ConnectToDevice(this).execute()

            controlLedSwitch = findViewById(R.id.control_led_switch)
            controlLedSwitch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // Turn on the LED (Send the "x" command to the Bluetooth device)
                    sendCommand("x")
                } else {
                    // Turn off the LED (Send the "y" command to the Bluetooth device)
                    sendCommand("y")
                }
            }

            val controlLedBlinkButton: Button = findViewById(R.id.control_led_blink)
            controlLedBlinkButton.setOnClickListener { sendCommand("z") }

            val controlLedDisconnectButton: Button = findViewById(R.id.control_led_disconnect)
            controlLedDisconnectButton.setOnClickListener { disconnect() }

            brightnessInput = findViewById(R.id.brightness_input)
            submitButton = findViewById(R.id.submit_button)
            submitButton.setOnClickListener { sendBrightnessCommand() }

            brightnessSlider = findViewById(R.id.brightness_slider)
            brightnessValueText = findViewById(R.id.brightness_value_text)
            brightnessSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    val brightnessText = getString(R.string.brightnessProgress) + " $progress"
                    brightnessValueText.text = brightnessText
                    sendIntCommand(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
    }

    private fun sendCommand(input: String) {
        if (m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun sendIntCommand(input: Int) {
        if (m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.outputStream.write(input)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun disconnect() {
        if (m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnected = false
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        finish()
    }

    private fun sendBrightnessCommand() {
        val brightnessText = brightnessInput.text.toString()
        if (brightnessText.isNotEmpty()) {
            val brightnessValue = brightnessText.toIntOrNull()
            if (brightnessValue != null && brightnessValue >= 0 && brightnessValue <= 100) {
                sendIntCommand(brightnessValue)
            } else {
                showToast("Invalid input; must be between 0-100!")
            }
        } else {
            showToast("Please enter a brightness value")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private class ConnectToDevice(private val activity: ControlActivity) :
        AsyncTask<Void, Void, BluetoothDevice?>() {

        private var connectSuccess: Boolean = true

        override fun onPreExecute() {
            super.onPreExecute()
            m_progress = ProgressDialog.show(activity, "Connecting...", "please wait")
        }

        @SuppressLint("MissingPermission")
        override fun doInBackground(vararg params: Void?): BluetoothDevice? {
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
            return if (connectSuccess) m_bluetoothSocket!!.remoteDevice else null
        }

        @SuppressLint("MissingPermission")
        override fun onPostExecute(result: BluetoothDevice?) {
            super.onPostExecute(result)
            if (result != null) {
                val deviceName = result.name
                activity.showToast("Connected to device: $deviceName")
            } else {
                activity.showToast("Could not connect to device")
                activity.finish()
            }
            m_isConnected = result != null
            m_progress.dismiss()
        }
    }


}
