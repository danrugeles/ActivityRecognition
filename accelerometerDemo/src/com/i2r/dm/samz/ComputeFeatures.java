package com.i2r.dm.samz;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

public class ComputeFeatures {
	final static int g = 10;
	final static float MaxTh = (float) (1.75 * g);
	final static float MinTh = (float) (0.75 * g);

	public static final String DEBUG_TAG = "Accelerometer Log";

	public static AccData computeMean(List<AccData> list) {
		float sumX = 0, sumY = 0, sumZ = 0;
		int size = list.size();
		ListIterator<AccData> iterator = list.listIterator();
		while (iterator.hasNext()) {
			AccData item = iterator.next();
			sumX += item.getX();
			sumY += item.getY();
			sumZ += item.getZ();
		}
		return new AccData(sumX / size, sumY / size, sumZ / size);
	}

	public static AccData computeMeanOfSegment(Segment window) {
		float sumX = 0, sumY = 0, sumZ = 0;
		int size = window.count();
		Iterator<AccData> iterator = window.listIterator();
		while (iterator.hasNext()) {
			AccData item = iterator.next();
			sumX += item.getX();
			sumY += item.getY();
			sumZ += item.getZ();
		}
		return new AccData(sumX / size, sumY / size, sumZ / size);
	}

	public static AccData computeStandardDeviation(List<AccData> list) {

		float tempX = 0, tempY = 0, tempZ = 0;
		AccData mean = computeMean(list);
		float meanX = mean.getX();
		float meanY = mean.getY();
		float meanZ = mean.getZ();

		int size = list.size();
		ListIterator<AccData> iterator = list.listIterator();
		while (iterator.hasNext()) {
			AccData item = iterator.next();
			tempX += Math.pow(item.getX() - meanX, 2);
			tempY += Math.pow(item.getY() - meanY, 2);
			tempZ += Math.pow(item.getZ() - meanZ, 2);
		}

		return new AccData((float) Math.sqrt(tempX / (size - 1)),
				(float) Math.sqrt(tempY / (size - 1)), (float) Math.sqrt(tempZ
						/ (size - 1)));
	}

	public static AccData computeAverageAbsoluteDifference(List<AccData> list) {

		float tempX = 0, tempY = 0, tempZ = 0;
		AccData mean = computeMean(list);
		float meanX = mean.getX();
		float meanY = mean.getY();
		float meanZ = mean.getZ();

		int size = list.size();
		ListIterator<AccData> iterator = list.listIterator();
		while (iterator.hasNext()) {
			AccData item = iterator.next();
			tempX += Math.abs(item.getX() - meanX);
			tempY += Math.abs(item.getY() - meanY);
			tempZ += Math.abs(item.getZ() - meanZ);
		}

		return new AccData((float) tempX / size, (float) tempY / size,
				(float) tempZ / size);
	}

	public static float computeAverageResultantAcceleration(List<AccData> list) {
		int size = list.size();
		ListIterator<AccData> iterator = list.listIterator();
		float temp = 0;
		while (iterator.hasNext()) {
			AccData item = iterator.next();
			temp += item.getRSS();
		}
		return temp / size;
	}

//	public static double computeResultantAcceleration(float x, float y, float z) {
//		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
//	}

	public static float[][] computeBinnedDistribution(List<AccData> list) {
		ListIterator<AccData> iterator = list.listIterator();
		float minX = list.get(0).getX(), maxX = list.get(0).getX();
		float minY = list.get(0).getY(), maxY = list.get(0).getY();
		float minZ = list.get(0).getZ(), maxZ = list.get(0).getZ();
		while (iterator.hasNext()) {
			AccData item = iterator.next();
			if (item.getX() > maxX)
				maxX = item.getX();
			if (item.getX() < minX)
				minX = item.getX();
			if (item.getY() > maxY)
				maxY = item.getY();
			if (item.getY() < minY)
				minY = item.getY();
			if (item.getZ() > maxZ)
				maxZ = item.getZ();
			if (item.getZ() < minZ)
				minZ = item.getZ();
		}

		float rangeX = maxX - minX;
		float binSizeX = rangeX / 10;
		float rangeY = maxY - minY;
		float binSizeY = rangeY / 10;
		float rangeZ = maxZ - minZ;
		float binSizeZ = rangeZ / 10;
		float[][] bin = new float[3][10];
		iterator = list.listIterator();
		while (iterator.hasNext()) {
			AccData item = iterator.next();
			try {
				bin[0][(int) Math.floor((item.getX() - minX) / binSizeX)]++;
			} catch (ArrayIndexOutOfBoundsException e) {
				bin[0][9]++;
			}
			try {
				bin[1][(int) Math.floor((item.getY() - minY) / binSizeY)]++;
			} catch (ArrayIndexOutOfBoundsException e) {
				bin[1][9]++;
			}
			try {
				bin[2][(int) Math.floor((item.getZ() - minZ) / binSizeZ)]++;
			} catch (ArrayIndexOutOfBoundsException e) {
				bin[2][9]++;
			}

		}
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 10; j++)
				bin[i][j] = bin[i][j] / list.size();
		return bin;

	}

	public void computeTimeBetweenPeaks(List<AccData> list) {

	}

	public static void splitWindows(List<AccData> list, int windowSize) {
		List<Vector<AccData>> windows = new ArrayList<Vector<AccData>>();
		AccData item = null;
		Iterator<AccData> listIterator = list.listIterator();
		if (listIterator.hasNext())
			item = listIterator.next();
		while (listIterator.hasNext()) {
			Vector<AccData> newWindow = new Vector<AccData>();
			long itemTimeStamp = item.getTime();
			while (item.getTime() < itemTimeStamp + windowSize) {
				newWindow.add(item);
				if (listIterator.hasNext())
					item = listIterator.next();
				else
					break;
			}
			windows.add(newWindow);
		}

	}

	public static List<Segment> splitWindowsWithOverlaps(List<AccData> list,
			int windowSize) {
		List<Segment> windows = new ArrayList<Segment>();
		int halfwindowSize = windowSize / 2;
		AccData item = null;
		Iterator<AccData> listIterator = list.listIterator();
		if (listIterator.hasNext())
			item = listIterator.next();
		Segment window = new Segment();
		Segment newWindow = new Segment();
		while (listIterator.hasNext()) {
			long itemTimeStamp = item.getTime();
			while (item.getTime() < itemTimeStamp + halfwindowSize) {
				window.add(item);
				newWindow.add(item);
				if (listIterator.hasNext())
					item = listIterator.next();
				else
					break;
			}
			windows.add(window);
			window = newWindow;
			newWindow = new Segment();
		}
		windows.add(window);
		windows.remove(0);
		return windows;
	}

	public static AccData[][] computePeaks(List<Segment> windows) {
		Iterator<Segment> listIteretor = windows.listIterator();
		AccData[][] minMax = new AccData[windows.size()][2];
		int windowCounter = 0;
		while (listIteretor.hasNext()) {
			Segment window = listIteretor.next();
			Iterator<AccData> windowIterator = window.listIterator();
			AccData maxItem = null, minItem = null;
			if (windowIterator.hasNext()) {
				maxItem = windowIterator.next();
				minItem = maxItem;
			}
			while (windowIterator.hasNext() && (maxItem != null)
					&& (minItem != null)) {
				AccData item = windowIterator.next();
				double resultanAcceleration = item.getRSS();
				if (resultanAcceleration > maxItem.getRSS())
					maxItem = item;
				if (resultanAcceleration < minItem.getRSS())
					minItem = item;
			}
			minMax[windowCounter][0] = maxItem;
			minMax[windowCounter][1] = minItem;
			windowCounter++;
		}
		return minMax;
	
	}



}
