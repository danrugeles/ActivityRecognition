package com.i2r.dm.samz;

import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class SessionActivity extends ListActivity {

	public static final String DEBUG_TAG = "Accelerometer Log";
	List<Integer> sessions;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		final int windowSize = extras.getInt("windowSize");
		AccelerometerDataSource ds = new AccelerometerDataSource(this);
		try {
			ds.open();
			sessions = ds.getAllSessions();
			ds.close();
		} catch (SQLException e) {
			Log.e(DEBUG_TAG, "SQL Exception in session retrieval", e);
		}
		// sessions = getIntent().getIntegerArrayListExtra("SessionList");

		setListAdapter(new ArrayAdapter<Integer>(this, R.layout.list_sessions,
				R.id.textView_session, sessions));

		ListView listView = getListView();
		listView.setTextFilterEnabled(true);

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView text = (TextView) view.findViewById(R.id.textView_session);
				Intent FeatureCompute = new Intent(SessionActivity.this, FeaturesActivity.class);
				FeatureCompute.putExtra("session id",Integer.parseInt(text.getText().toString()));
				FeatureCompute.putExtra("windowSize", windowSize);
				startActivity(FeatureCompute);
			}
		});
		
		

	}

}
