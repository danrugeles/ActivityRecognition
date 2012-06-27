package com.i2r.dm.samz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class TestActivity extends Activity implements SensorEventListener {

	public static final String DEBUG_TAG = "Accelerometer Log";
	private boolean mInitialized;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private boolean sensing = false;
	PowerManager pm;
	PowerManager.WakeLock wl;
	private int rate = 3;
	AccelerometerDataSource accDataSource;
	List<AccData> dataList = new ArrayList<AccData>();

	final int THETA_1_ROWS = 15;
	final int THETA_1_COLS = 39;
	final int THETA_2_ROWS = 2;
	final int THETA_2_COLS = 16;
	float[][] theta1, theta2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.test);

		mInitialized = false;
		try {
			theta1 = readTheta1();
			theta2 = readTheta2();
		} catch (IOException e) {
			Log.e(DEBUG_TAG, "IO Exceprion in reading file");
		}
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
		initLayout();
	}

	private void initLayout() {
		Button sensorBtn = (Button) findViewById(R.id.sensorBtn);

		sensorBtn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (sensing) {
					sensing = false;
					unregisterListener();

				} else {
					sensing = true;
					TextView fallStatus = (TextView) findViewById(R.id.textView_fallStatus);
					TextView fallprob = (TextView) findViewById(R.id.textView_probability);
					fallprob.setText("");
					fallStatus.setText("");
					registerListener();
				}
			}
		});

	}

	private void unregisterListener() {
		wl.release();
		final Button sensorBtn = (Button) findViewById(R.id.sensorBtn);
		final TextView senseStatus = (TextView) findViewById(R.id.textView_senseStatus);
		mSensorManager.unregisterListener(TestActivity.this);
		// mSensorManager.disconnectSimulator();
		sensorBtn.setText(getString(R.string.start));
		senseStatus.setText(getResources().getString(R.string.not_sensing));
		senseStatus.setTextColor(getResources().getColor(R.color.red));
	}

	private void registerListener() {

		wl.acquire();
		final Button sensorBtn = (Button) findViewById(R.id.sensorBtn);
		final TextView senseStatus = (TextView) findViewById(R.id.textView_senseStatus);
		if (mSensorManager.registerListener(TestActivity.this, mAccelerometer,
				SensorManager.SENSOR_DELAY_UI)) {
			sensorBtn.setText(getString(R.string.stop));
			senseStatus.setText(getResources().getString(R.string.sensing));
			senseStatus.setTextColor(getResources().getColor(R.color.green));
		}
	}

	protected void onResume() {
		super.onResume();
		Log.i(DEBUG_TAG, "Resume");
		if (sensing)
			mSensorManager.registerListener(this, mAccelerometer, rate);
	}

	protected void onPause() {
		super.onPause();
		Log.i(DEBUG_TAG, "Pause");
		if (sensing)
			mSensorManager.unregisterListener(this);
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	public void onSensorChanged(SensorEvent event) {
		TextView tvX = (TextView) findViewById(R.id.x_axis);
		TextView tvY = (TextView) findViewById(R.id.y_axis);
		TextView tvZ = (TextView) findViewById(R.id.z_axis);
		TextView counter = (TextView) findViewById(R.id.textView_counter);
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];
		if (!mInitialized) {
			tvX.setText("0.0");
			tvY.setText("0.0");
			tvZ.setText("0.0");
			mInitialized = true;
		} else {
			tvX.setText(Float.toString(x));
			tvY.setText(Float.toString(y));
			tvZ.setText(Float.toString(z));
			AccData data = new AccData(-1, System.currentTimeMillis(), x, y, z);
			dataList.add(data);
			counter.setText(Integer.toString(dataList.size()));
			if (dataList.size() == 38) {
				unregisterListener();
				sensing = false;
				boolean isFall = analyzeTestSet(dataList);
				updateScreen(isFall);
				dataList.clear();
			}

		}

	}

	private void updateScreen(boolean isFall) {
		TextView fallStatus = (TextView) findViewById(R.id.textView_fallStatus);
		if(isFall){
			fallStatus.setText(getResources().getString(R.string.fall));
			fallStatus.setTextColor(getResources().getColor(R.color.red));
		}
		else{
			fallStatus.setText(getResources().getString(R.string.no_fall));
			fallStatus.setTextColor(getResources().getColor(R.color.green));
		}
	}

	private boolean analyzeTestSet(List<AccData> X) {
		AccData bias = new AccData(1, 0, 0);
		X.add(0, bias);
		float[] X1 = new float[THETA_1_ROWS + 1];
		X1[0] = 1;
		float[] X2 = new float[THETA_2_ROWS];
		for (int i = 0; i < THETA_1_ROWS; i++) {
			float sum = 0;
			for (int j = 0; j < THETA_1_COLS; j++) {
				sum += X.get(j).getRSS() * theta1[j][i];
			}
			X1[i + 1] = sigmoid(sum);
		}

		for (int i = 0; i < THETA_2_ROWS; i++) {
			float sum = 0;
			for (int j = 0; j < THETA_2_COLS; j++) {
				sum += X1[j] * theta2[j][i];
			}
			X2[i] = sigmoid(sum);
		}
		
		TextView probText = (TextView) findViewById(R.id.textView_probability);
		probText.setText("fall probability: " + Float.toString(X2[1]));

		float fallProb = X2[1];
		if(fallProb> 0.5)
			return true;
		else 
			return false;
		

	}

	private float[][] readTheta1() throws IOException {
		String str = "";
		float[][] theta1 = new float[THETA_1_COLS][THETA_1_ROWS];

		int row = 0;
		InputStream is = getResources().getAssets().open("Theta1.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		if (is != null) {
			while ((str = reader.readLine()) != null) {
				String[] rowValues = str.split(" ");
				for (int i = 0; i < rowValues.length; i++) {
					String s = rowValues[i];
					float x = Float.parseFloat(s);
					theta1[i][row] = x;
				}
				row++;

			}
		}
		is.close();

		return theta1;

	}

	private float[][] readTheta2() throws IOException {
		String str = "";
		float[][] theta2 = new float[THETA_2_COLS][THETA_2_ROWS];
		int row = 0;
		InputStream is = getResources().getAssets().open("Theta2.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		if (is != null) {
			while ((str = reader.readLine()) != null) {
				String[] rowValues = str.split(" ");
				for (int i = 0; i < rowValues.length; i++) {
					String s = rowValues[i];
					float x = Float.parseFloat(s);
					theta2[i][row] = x;
				}
				row++;

			}
		}
		is.close();
		return theta2;
	}
	
	public float sigmoid(float in){
		float result = (float) (1 / (1 + Math.exp(-1* in)));
		return result;
	}

}
