package com.i2r.dm.samz;

import java.util.Calendar;

public class AccData {
	private int _id;
	private int _session;
	private long _timestamp;
	private float _x;
	private float _y;
	private float _z;
	private int _rate;
	private float _rss;
	
	public AccData()
	{
		
	}

	public AccData(int session,long timestamp, float x, float y, float z) {
		_session = session;
		_timestamp = timestamp;
		_x = x;
		_y = y;
		_z = z;
	}

	public AccData(float x, float y, float z) {
		Calendar c = Calendar.getInstance();
		_timestamp = c.getTimeInMillis();
		_x = x;
		_y = y;
		_z = z;
	}
	
	public int getId(){
		return _id;
	}
	
	public int getSession(){
		return _session;
	}

	public long getTime() {
		return _timestamp;
	}

	public float getX() {
		return _x;
	}

	public float getY() {
		return _y;
	}

	public float getZ() {
		return _z;
	}
	
	public int getRate(){
		return _rate;
	}
	
	public void setId(int id){
		_id = id;
	}
	
	public void setSession(int session){
		_session = session;
	}
	
	public void setTime(long time) {
		_timestamp = time;
	}

	public void setX(float x) {
		this._x = x;
	}

	public void setY(float y) {
		this._y = y;
	}

	public void setZ(float z) {
		this._z = z;
	}
	public void setRate(int rate){
		_rate = rate;
	}
	
	public double getRSS(){
		return Math.sqrt(_x*_x + _y*_y +_z *_z);
	}

}
