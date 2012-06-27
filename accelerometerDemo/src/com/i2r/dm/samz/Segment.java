package com.i2r.dm.samz;

import java.util.Iterator;
import java.util.Vector;

public class Segment {
	private Vector<AccData> data;
	
	public Segment(int size){
		data = new Vector<AccData>();
	}
	public Segment(){
		data = new Vector<AccData>();
	}
	
	public void add(AccData item){
		data.add(item);
	}
	
	public Vector<AccData> getData() {
		return this.data;
	}
	
	
	public int count(){
		return data.size();
	}
	public Iterator<AccData> listIterator() {
		return data.listIterator();
	}
}
