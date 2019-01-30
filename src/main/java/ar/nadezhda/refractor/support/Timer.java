package ar.nadezhda.refractor.support;

public class Timer {

	protected final long start;

	public Timer() {
		this.start = System.nanoTime();
	}

	public static Timer start() {
		return new Timer();
	}

	public long getTime() {
		return System.nanoTime() - start;
	}

	public double getTimeInSeconds() {
		return 1.0E-9 * getTime();
	}
}
