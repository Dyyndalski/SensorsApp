package com.example.sensorsapp


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlin.math.roundToLong


class MainActivity : AppCompatActivity(), SensorEventListener, LocationListener{
    private lateinit var mSensorManager : SensorManager
    private var mAccelerometer : Sensor ?= null
    private var mLight : Sensor ?= null
    private var mTemperature : Sensor ?= null
    private var mPressure : Sensor ?= null
    private var mHumidity : Sensor ?= null

    private lateinit var locationManager: LocationManager
    private lateinit var tvGpsLocation: TextView
    private val locationPermissionCode = 2

    private var thread = Thread(){
        while(true) {
            run {
                Thread.sleep(5000)
                //Thread.sleep(60000) // ===> co minute!
            }
            runOnUiThread() {
                Toast.makeText(this, "Temperatura: " + Math.round(getRecord()).toString() + "° Celcjusza", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null){
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                findViewById<TextView>(R.id.X).text ="X: " + event.values[0].toString() + "°"
                findViewById<TextView>(R.id.Y).text ="Y: " + event.values[1].toString() + "°"
                findViewById<TextView>(R.id.Z).text ="Z: " + event.values[2].toString() + "°"
            }
            if (event.sensor.type == Sensor.TYPE_LIGHT) {
                findViewById<TextView>(R.id.Jasnosc).text = event.values[0].toString() + " lux"
            }
            if (event.sensor.type == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                setRecord(event.values[0])
            }
            if (event.sensor.type == Sensor.TYPE_PRESSURE) {
                findViewById<TextView>(R.id.Cisnienie).text = event.values[0].toString() + " hPa"
            }
            if (event.sensor.type == Sensor.TYPE_RELATIVE_HUMIDITY) {
               findViewById<TextView>(R.id.Wilgotnosc).text = event.values[0].toString() + "%"
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        return
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        mTemperature = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)
        mHumidity = mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)

        getLocation()

        thread.start()
    }



    override fun onResume() {
        super.onResume()
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_FASTEST)
        mSensorManager.registerListener(this, mTemperature, SensorManager.SENSOR_DELAY_NORMAL)
        mSensorManager.registerListener(this, mPressure, SensorManager.SENSOR_DELAY_NORMAL)
        mSensorManager.registerListener(this, mHumidity, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun setRecord(temperature: Float){
        val sharedScore = this.getSharedPreferences("com.example.myapplication.shared",0)
        val edit = sharedScore.edit()
        edit.putFloat("temperature", temperature)
        edit.apply()
    }

    fun getRecord(): Float {
        val sharedScore = this.getSharedPreferences("com.example.myapplication.shared",0)
        return sharedScore.getFloat("temperature", 0f)
    }

    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
    }

    override fun onLocationChanged(location: Location) {
        findViewById<TextView?>(R.id.lettitude).text = "Szerokość: " + Math.round(location.latitude * 100.0) / 100.0+ "°"
        findViewById<TextView?>(R.id.longitude).text = "Długość: " + Math.round(location.longitude * 100.0) / 100.0 + "°"

    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}