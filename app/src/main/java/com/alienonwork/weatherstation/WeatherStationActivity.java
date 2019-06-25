package com.alienonwork.weatherstation;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class WeatherStationActivity extends AppCompatActivity {
    private SensorManager mSensorManager;

    private TextView mTemperatureTextView;
    private TextView mPressureTextView;
    private TextView mHumidityTextView;
    private TextView mLightTextView;

    private float mLastTemperature = Float.NaN;
    private float mLastPressure = Float.NaN;
    private float mLastHumidity = Float.NaN;
    private float mLastLight = Float.NaN;

    private final SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case (Sensor.TYPE_AMBIENT_TEMPERATURE):
                    mLastTemperature = event.values[0];
                    break;
                case (Sensor.TYPE_RELATIVE_HUMIDITY):
                    mLastHumidity = event.values[0];
                    break;
                case (Sensor.TYPE_PRESSURE):
                    mLastPressure = event.values[0];
                    break;
                case (Sensor.TYPE_LIGHT):
                    mLastLight = event.values[0];
                    break;
                default: break;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) { }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_station);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mTemperatureTextView = findViewById(R.id.temperature);
        mPressureTextView = findViewById(R.id.pressure);
        mHumidityTextView = findViewById(R.id.humidity);

        Timer updateTimer = new Timer("weatherUpdate");
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateGUI();
            }
        }, 0, 1000);
    }

    @Override
    protected  void onResume() {
        super.onResume();

        Sensor lightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (lightSensor != null)
            mSensorManager.registerListener(mSensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        else
            mLightTextView.setText("Light Sensor Unavailable");

        Sensor pressureSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        if (pressureSensor != null)
            mSensorManager.registerListener(mSensorEventListener, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        else
            mPressureTextView.setText("Barometer Unavailable");

        Sensor temperatureSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        if (temperatureSensor != null)
            mSensorManager.registerListener(mSensorEventListener, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        else
            mTemperatureTextView.setText("Thermometer Unavailable");

        Sensor humiditySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        if (humiditySensor != null)
            mSensorManager.registerListener(mSensorEventListener, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL);
        else
            mHumidityTextView.setText("Humidity Sensor Unavailable");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mSensorEventListener);
    }

    private void updateGUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!Float.isNaN(mLastPressure)) {
                    mPressureTextView.setText(mLastPressure + "hPa");
                    mPressureTextView.invalidate();
                }
                if (!Float.isNaN(mLastLight)) {
                    String lightStr = "Sunny";
                    if (mLastLight <= SensorManager.LIGHT_CLOUDY)
                        lightStr = "Night";
                    if (mLastLight <= SensorManager.LIGHT_OVERCAST)
                        lightStr = "Cloudy";
                    else if (mLastLight <= SensorManager.LIGHT_SUNLIGHT)
                        lightStr = "Overcast";
                    mLightTextView.setText(lightStr);
                    mLightTextView.invalidate();
                }
                if (!Float.isNaN(mLastTemperature)) {
                    mTemperatureTextView.setText(mLastTemperature + "C");
                    mTemperatureTextView.invalidate();
                }
                if (!Float.isNaN(mLastHumidity)) {
                    mHumidityTextView.setText(mLastHumidity + "% Rel. Humidity");
                    mHumidityTextView.invalidate();
                }
            }
        });
    }
}
