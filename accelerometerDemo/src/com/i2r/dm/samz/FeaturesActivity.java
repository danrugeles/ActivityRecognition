package com.i2r.dm.samz;

import java.util.List;

import android.app.Activity;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class FeaturesActivity extends Activity {

	private int windowSize = 1500;
	private int sessionID;
	AccelerometerDataSource accDataSource;
	List<AccData> workingList;
	public static final String DEBUG_TAG = "Accelerometer Log";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.features);

		Bundle extras = getIntent().getExtras();
		sessionID = extras.getInt("session id");
		windowSize = extras.getInt("windowSize");
		accDataSource = new AccelerometerDataSource(this);
		try {
			accDataSource.open();
			workingList = accDataSource.getAllDataBySessionId(sessionID);
			accDataSource.close();
		} catch (SQLException e) {
			Log.e(DEBUG_TAG, "SQL Exception in Features Activity", e);
		}

		computeMean();
		computeStandardDeviation();
		computeAbsoluteDifference();
		computeAverageResultantAcceleration();
		computeBinnedDistribution();
		boolean isFall = findFall();

		TextView fallStatus = (TextView) findViewById(R.id.textView_fallStatus);
		if (isFall) {
			fallStatus.setText(getResources().getString(R.string.fall));
			fallStatus.setTextColor(getResources().getColor(R.color.red));
		} else {
			fallStatus.setText(getResources().getString(R.string.no_fall));
			fallStatus.setTextColor(getResources().getColor(R.color.green));
		}

	}

	private void computeMean() {
		AccData meanData = null;
		if (!workingList.isEmpty()) {
			meanData = ComputeFeatures.computeMean(workingList);
		}

		TextView meanX = (TextView) findViewById(R.id.textView_meanX);
		TextView meanY = (TextView) findViewById(R.id.textView_meanY);
		TextView meanZ = (TextView) findViewById(R.id.textView_meanZ);

		meanX.setText(Float.toString(meanData.getX()));
		meanY.setText(Float.toString(meanData.getY()));
		meanZ.setText(Float.toString(meanData.getZ()));
	}

	private void computeStandardDeviation() {
		AccData devData = null;
		if (!workingList.isEmpty()) {
			devData = ComputeFeatures.computeStandardDeviation(workingList);
		}

		TextView devX = (TextView) findViewById(R.id.textView_SDevX);
		TextView devY = (TextView) findViewById(R.id.textView_SDevY);
		TextView devZ = (TextView) findViewById(R.id.textView_SDevZ);

		devX.setText(Float.toString(devData.getX()));
		devY.setText(Float.toString(devData.getY()));
		devZ.setText(Float.toString(devData.getZ()));
	}

	private void computeAbsoluteDifference() {
		AccData absDifData = null;
		if (!workingList.isEmpty()) {
			absDifData = ComputeFeatures
					.computeAverageAbsoluteDifference(workingList);
		}

		TextView absDifX = (TextView) findViewById(R.id.TextView_absDifX);
		TextView absDifY = (TextView) findViewById(R.id.TextView_absDifY);
		TextView absDifZ = (TextView) findViewById(R.id.TextView_absDifZ);

		absDifX.setText(Float.toString(absDifData.getX()));
		absDifY.setText(Float.toString(absDifData.getY()));
		absDifZ.setText(Float.toString(absDifData.getZ()));
	}

	private void computeAverageResultantAcceleration() {
		float avgResultantAcc = 0;
		if (!workingList.isEmpty()) {
			avgResultantAcc = ComputeFeatures
					.computeAverageResultantAcceleration(workingList);
		}

		TextView avgResAcc = (TextView) findViewById(R.id.textView_avgResultantAcc);
		avgResAcc.setText(Float.toString(avgResultantAcc));

	}

	private void computeBinnedDistribution() {
		TextView x1 = (TextView) findViewById(R.id.textViewBin1X);
		TextView y1 = (TextView) findViewById(R.id.textViewBin1Y);
		TextView z1 = (TextView) findViewById(R.id.textViewBin1Z);
		TextView x2 = (TextView) findViewById(R.id.textViewBin2X);
		TextView y2 = (TextView) findViewById(R.id.textViewBin2Y);
		TextView z2 = (TextView) findViewById(R.id.textViewBin2Z);
		TextView x3 = (TextView) findViewById(R.id.textViewBin3X);
		TextView y3 = (TextView) findViewById(R.id.textViewBin3Y);
		TextView z3 = (TextView) findViewById(R.id.textViewBin3Z);
		TextView x4 = (TextView) findViewById(R.id.textViewBin4X);
		TextView y4 = (TextView) findViewById(R.id.textViewBin4Y);
		TextView z4 = (TextView) findViewById(R.id.textViewBin4Z);
		TextView x5 = (TextView) findViewById(R.id.textViewBin5X);
		TextView y5 = (TextView) findViewById(R.id.textViewBin5Y);
		TextView z5 = (TextView) findViewById(R.id.textViewBin5Z);
		TextView x6 = (TextView) findViewById(R.id.textViewBin6X);
		TextView y6 = (TextView) findViewById(R.id.textViewBin6Y);
		TextView z6 = (TextView) findViewById(R.id.textViewBin6Z);
		TextView x7 = (TextView) findViewById(R.id.textViewBin7X);
		TextView y7 = (TextView) findViewById(R.id.textViewBin7Y);
		TextView z7 = (TextView) findViewById(R.id.textViewBin7Z);
		TextView x8 = (TextView) findViewById(R.id.textViewBin8X);
		TextView y8 = (TextView) findViewById(R.id.textViewBin8Y);
		TextView z8 = (TextView) findViewById(R.id.textViewBin8Z);
		TextView x9 = (TextView) findViewById(R.id.textViewBin9X);
		TextView y9 = (TextView) findViewById(R.id.textViewBin9Y);
		TextView z9 = (TextView) findViewById(R.id.textViewBin9Z);
		TextView x10 = (TextView) findViewById(R.id.textViewBin10X);
		TextView y10 = (TextView) findViewById(R.id.textViewBin10Y);
		TextView z10 = (TextView) findViewById(R.id.textViewBin10Z);

		float[][] results = ComputeFeatures
				.computeBinnedDistribution(workingList);

		x1.setText(Float.toString(results[0][0] * 100) + "%");
		x2.setText(Float.toString(results[0][1] * 100) + "%");
		x3.setText(Float.toString(results[0][2] * 100) + "%");
		x4.setText(Float.toString(results[0][3] * 100) + "%");
		x5.setText(Float.toString(results[0][4] * 100) + "%");
		x6.setText(Float.toString(results[0][5] * 100) + "%");
		x7.setText(Float.toString(results[0][6] * 100) + "%");
		x8.setText(Float.toString(results[0][7] * 100) + "%");
		x9.setText(Float.toString(results[0][8] * 100) + "%");
		x10.setText(Float.toString(results[0][9] * 100) + "%");

		y1.setText(Float.toString(results[1][0] * 100) + "%");
		y2.setText(Float.toString(results[1][1] * 100) + "%");
		y3.setText(Float.toString(results[1][2] * 100) + "%");
		y4.setText(Float.toString(results[1][3] * 100) + "%");
		y5.setText(Float.toString(results[1][4] * 100) + "%");
		y6.setText(Float.toString(results[1][5] * 100) + "%");
		y7.setText(Float.toString(results[1][6] * 100) + "%");
		y8.setText(Float.toString(results[1][7] * 100) + "%");
		y9.setText(Float.toString(results[1][8] * 100) + "%");
		y10.setText(Float.toString(results[1][9] * 100) + "%");

		z1.setText(Float.toString(results[2][0] * 100) + "%");
		z2.setText(Float.toString(results[2][1] * 100) + "%");
		z3.setText(Float.toString(results[2][2] * 100) + "%");
		z4.setText(Float.toString(results[2][3] * 100) + "%");
		z5.setText(Float.toString(results[2][4] * 100) + "%");
		z6.setText(Float.toString(results[2][5] * 100) + "%");
		z7.setText(Float.toString(results[2][6] * 100) + "%");
		z8.setText(Float.toString(results[2][7] * 100) + "%");
		z9.setText(Float.toString(results[2][8] * 100) + "%");
		z10.setText(Float.toString(results[2][9] * 100) + "%");
	}
	

	private boolean inactivityAfterFall(AccData[][] minMax, int lastSpikeWindow) {
		
		return true;
	}


	private boolean findFall() {
		int counter = 0;
		int lastSpikeWindow = -1;
		List<Segment> windows = ComputeFeatures.splitWindowsWithOverlaps(
				workingList, windowSize);
		AccData[][] minMax = ComputeFeatures.computePeaks(windows);

		boolean[] fallSegments = analyzeSegmentPeaks(minMax);

		for (int i = 0; i < fallSegments.length; i++)
			if (fallSegments[i] == true){
				counter++;
				lastSpikeWindow = i;
			}
		if ((counter>0) &&(counter <= 2))
			return inactivityAfterFall(minMax,lastSpikeWindow);
		return false;
	}

	private boolean[] analyzeSegmentPeaks(AccData[][] minMax) {

		boolean[] isFall = new boolean[minMax.length];
		for (int i = 0; i < minMax.length; i++) {
			if (((minMax[i][0].getRSS()) > ComputeFeatures.MaxTh)
					&& ((minMax[i][1].getRSS()) < ComputeFeatures.MinTh)
					&& (minMax[i][1].getTime() < minMax[i][0].getTime())) {
				isFall[i] = true;
				showPeakData(minMax[i], i, true);
			} else {
				isFall[i] = false;
				showPeakData(minMax[i], i, false);
			}

		}
		return isFall;
	}

	private void showPeakData(AccData[] oneMinMax, int window, boolean fall) {
		TableLayout layout = (TableLayout) findViewById(R.id.tableLayout_fallPeaks);
		TableRow labelRow = new TableRow(FeaturesActivity.this);
		TextView windowNumber = new TextView(FeaturesActivity.this);
		windowNumber.setPadding(5, 5, 5, 5);
		windowNumber.setText("Segment Number: " + Integer.toString(window));
		labelRow.addView(windowNumber);
		layout.addView(labelRow);

		TableRow min = new TableRow(FeaturesActivity.this);
		TextView minText = new TextView(FeaturesActivity.this);
		minText.setPadding(20, 5, 5, 5);
		minText.setText("Min: " + oneMinMax[1].getRSS());
		minText.setTextColor(fall ? getResources().getColor(R.color.red)
				: getResources().getColor(R.color.green));
		min.addView(minText);
		layout.addView(min);

		TableRow max = new TableRow(FeaturesActivity.this);
		TextView maxText = new TextView(FeaturesActivity.this);
		maxText.setPadding(20, 5, 5, 5);
		maxText.setText("Max: " + oneMinMax[0].getRSS());
		maxText.setTextColor(fall ? getResources().getColor(R.color.red)
				: getResources().getColor(R.color.green));
		max.addView(maxText);
		layout.addView(max);

		TableRow time = new TableRow(FeaturesActivity.this);
		TextView timeDifference = new TextView(FeaturesActivity.this);
		timeDifference.setPadding(20, 5, 5, 5);
		timeDifference
				.setText("Time Difference: "
						+ Long.toString(oneMinMax[0].getTime()
								- oneMinMax[1].getTime()) + " milliseconds");
		time.addView(timeDifference);
		layout.addView(time);

	}

}
