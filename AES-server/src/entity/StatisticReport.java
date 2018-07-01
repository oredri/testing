package entity;

public class StatisticReport {

	private float median;
	private float average;
	private int[] histogram;

	public StatisticReport(float median, float average, int[] histogram) {
		this.median = median;
		this.average = average;
		this.histogram = histogram;
	}

	public float getMedian() {
		return median;
	}

	public void setMedian(float median) {
		this.median = median;
	}

	public float getAverage() {
		return average;
	}

	public void setAverage(float average) {
		this.average = average;
	}

	public int[] getHistogram() {
		return histogram;
	}

	public void setHistogram(int[] histogram) {
		this.histogram = histogram;
	}
}
