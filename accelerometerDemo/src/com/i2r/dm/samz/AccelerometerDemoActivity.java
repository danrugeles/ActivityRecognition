package com.i2r.dm.samz;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AccelerometerDemoActivity extends Activity implements
		SensorEventListener {

	public static final String DEBUG_TAG = "Accelerometer Log";
	private static final int NORMAL_DELAY = 3;
	private static final int UI_DELAY = 2;
	private static final int GAME_DELAY = 1;
	private static final int FASTEST_DELAY = 0;
	private boolean mInitialized;
	private int sessionNum = 0;
	private int rate = 3;
	private long startSenseTime;
	private int senseDuration;
	AccelerometerDataSource accDataSource;

	List<AccData> dataList = new ArrayList<AccData>();
	List<Integer> sessionList = new ArrayList<Integer>();

	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private boolean sensing = false;
	PowerManager pm;
	PowerManager.WakeLock wl;

	// private final float NOISE = (float) 2.0;

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.main);

		mInitialized = false;

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		// mSensorManager.registerListener(AccelerometerDemoActivity.this,
		// mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		// mSensorManager.connectSimulator();

		initLayout();
		initSession();
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");

	}

	private void initSession() {
		AccelerometerDataSource ds = new AccelerometerDataSource(this);
		try {
			ds.open();
			sessionList = ds.getAllSessions();
			ds.close();
		} catch (SQLException e) {
			Log.e(DEBUG_TAG, "SQL Exception in session retrieval", e);
		}
		try {
			sessionNum = sessionList.get(sessionList.size() - 1);
		} catch (Exception e) {
			sessionNum = 0;
			Log.e(DEBUG_TAG, "there were no previous session num");
		}
	}

	private void initLayout() {
		// clearDatabase();

		final EditText windowSizeEdit = (EditText) findViewById(R.id.editText_windowSize);
		windowSizeEdit.setText("1500");

		final EditText duration = (EditText) findViewById(R.id.editText_senseDuration);
		duration.setText("5");

		final TextView senseStatus = (TextView) findViewById(R.id.textView_senseStatus);
		senseStatus.setText(getResources().getString(R.string.not_sensing));
		senseStatus.setTextColor(getResources().getColor(R.color.red));

		/* Sensor Button */
		final Button sensorBtn = (Button) findViewById(R.id.sensorBtn);
		sensorBtn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (sensing) {
					sensing = false;
					unregisterListener();

				} else {
					sensing = true;
					EditText duration = (EditText) findViewById(R.id.editText_senseDuration);
					senseDuration = Integer.parseInt(duration.getText()
							.toString());
					dataList.clear();
					startSenseTime = System.currentTimeMillis();
					registerListener();
				}
			}

		});

		/* Spinner */
		Spinner rateSpinner = (Spinner) findViewById(R.id.spinner_rate);
		String[] rates = new String[] { "Normal: 180", "UI: 60", "Game: 20",
				"Fastest: 10" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, rates);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		rateSpinner.setAdapter(adapter);
		rateSpinner.setSelection(1);
		rateSpinner
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> adapterView,
							View view, int position, long id) {
						switch (position) {
						case 0:
							rate = NORMAL_DELAY;
							break;
						case 1:
							rate = UI_DELAY;
							break;
						case 2:
							rate = GAME_DELAY;
							break;
						case 3:
							rate = FASTEST_DELAY;
							break;
						}

					}

					public void onNothingSelected(AdapterView<?> adapterView) {
						return;
					}
				});

		/* Clear Database Button */
		Button clearDatabase = (Button) findViewById(R.id.clearDbBtn);
		clearDatabase.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				new AlertDialog.Builder(AccelerometerDemoActivity.this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Are you sure?")
				.setMessage("All Data in Database Will be Lost")
				.setPositiveButton(android.R.string.ok,
						new AlertDialog.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								clearDatabase();
							}
						})
				.setNegativeButton(android.R.string.cancel,
						new AlertDialog.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {

							}
						}).show();
			}
		});

		/* Extract Features Button */
		Button extract = (Button) findViewById(R.id.extractFeaturesBtn);
		extract.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				EditText windowSizeEdit = (EditText) findViewById(R.id.editText_windowSize);
				int windowSize = Integer.parseInt(windowSizeEdit.getText()
						.toString());
				Intent sessionView = new Intent(AccelerometerDemoActivity.this,
						SessionActivity.class);
				sessionView.putExtra("windowSize", windowSize);
				startActivity(sessionView);

			}
		});
		
		/* Test Button */
		Button testBtn = (Button) findViewById(R.id.TestBtn);
		testBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				startActivity(new Intent(AccelerometerDemoActivity.this,TestActivity.class));
			}
		});
		
	}

	private void clearDatabase() {
		AccelerometerDataSource ds = new AccelerometerDataSource(
				AccelerometerDemoActivity.this);
		try {
			ds.open();
			int rowDeleted = ds.deleteAllData();
			Toast.makeText(AccelerometerDemoActivity.this,
					rowDeleted + " rows deleted", Toast.LENGTH_SHORT).show();
			ds.close();
		} catch (SQLException e) {
			Log.e(DEBUG_TAG, "SQL Exception in clear database", e);
			Toast.makeText(AccelerometerDemoActivity.this,
					"could not delete rows", Toast.LENGTH_SHORT).show();
		}
		sessionNum = 0;
	}

	private void unregisterListener() {
		wl.release();
		final Button sensorBtn = (Button) findViewById(R.id.sensorBtn);
		final TextView senseStatus = (TextView) findViewById(R.id.textView_senseStatus);
		mSensorManager.unregisterListener(AccelerometerDemoActivity.this);
		// mSensorManager.disconnectSimulator();
		sensorBtn.setText(getString(R.string.start));
		senseStatus.setText(getResources().getString(R.string.not_sensing));
		senseStatus.setTextColor(getResources().getColor(R.color.red));
	}

	private void registerListener() {

		createNewSession();
		wl.acquire();
		final Button sensorBtn = (Button) findViewById(R.id.sensorBtn);
		final TextView senseStatus = (TextView) findViewById(R.id.textView_senseStatus);
		if (mSensorManager.registerListener(AccelerometerDemoActivity.this,
				mAccelerometer, rate)) {
			sensorBtn.setText(getString(R.string.stop));
			senseStatus.setText(getResources().getString(R.string.sensing));
			senseStatus.setTextColor(getResources().getColor(R.color.green));
		}

	}

	private void createNewSession() {
		sessionNum++;

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
		// Log.i(DEBUG_TAG, "Sensing...");

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
			AccData data = new AccData(sessionNum, System.currentTimeMillis(),
					x, y, z);
			data.setRate(rate);
			dataList.add(data);
			counter.setText(Integer.toString(dataList.size()));
			if (System.currentTimeMillis() > startSenseTime + senseDuration
					* 1000) {
				unregisterListener();
				sensing = false;
				insertToDatabase(dataList);
				dataList.clear();

			}

		}
	}

	private void insertToDatabase(final List<AccData> dataList) {

		accDataSource = new AccelerometerDataSource(
				AccelerometerDemoActivity.this);
		Log.i(DEBUG_TAG, "inserting to database");
		try {
			accDataSource.open();
			ListIterator<AccData> iterator = dataList.listIterator();
			while (iterator.hasNext()) {
				AccData data = iterator.next();
				accDataSource.insertData(data);
			}
			accDataSource.close();

		} catch (Exception e) {
			Log.e(DEBUG_TAG, "SQL Exception in inserting to database", e);
		}
		Log.i(DEBUG_TAG, "data list inserted to database");

	}

}