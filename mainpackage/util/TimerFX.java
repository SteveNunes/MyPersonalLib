package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javafx.animation.AnimationTimer;

public abstract class TimerFX {

	private static final Map<String, AnimationTimer> timers = new HashMap<>();

	public static void createTimer(String timerName, long startingDelayInMs, Runnable runnable) {
		createTimer(timerName, startingDelayInMs, -1, 1, runnable); // -1 for no repeating delay, 1 execution
	}

	public static void createTimer(String timerName, long repeatingDelayInMs, int repeatingTimes, Runnable runnable) {
		createTimer(timerName, 0, repeatingDelayInMs, repeatingTimes, runnable); // 0 for immediate start
	}

	public static void createTimer(String timerName, long startingDelayInMs, long repeatingDelayInMs, int repeatingTimes, Runnable runnable) {
		if (timers.containsKey(timerName))
			resetTimer(timerName, startingDelayInMs, repeatingDelayInMs, repeatingTimes, runnable);
		else
			startNewTimer(timerName, startingDelayInMs, repeatingDelayInMs, repeatingTimes, runnable);
	}

	private static void startNewTimer(String timerName, long startingDelayInMs, long repeatingDelayInMs, int repeatingTimes, Runnable runnable) {
		final long[] lastTime = {0};
		final long[] startTime = {0};
		final int[] executionCount = {0};
		final boolean[] started = {false};

		AnimationTimer timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				if (!started[0]) {
					startTime[0] = now;
					lastTime[0] = now;
					started[0] = true;
				}

				long elapsedSinceStart = now - startTime[0];
				long elapsedSinceLastRun = now - lastTime[0];

				if (executionCount[0] == 0 && elapsedSinceStart >= startingDelayInMs * 1_000_000L) {
					runnable.run();
					executionCount[0]++;
					lastTime[0] = now;
					if (repeatingTimes == 1)
						stopTimer(timerName);
				}
				else if (executionCount[0] > 0 && repeatingDelayInMs > 0 && elapsedSinceLastRun >= repeatingDelayInMs * 1_000_000L) {
					runnable.run();
					executionCount[0]++;
					lastTime[0] = now;
					if (repeatingTimes > 0 && executionCount[0] >= repeatingTimes) {
						stopTimer(timerName);
					}
				}
			}
		};

		timers.put(timerName, timer);
		timer.start();
	}

	public static void resetTimer(String timerName, long startingDelayInMs, long repeatingDelayInMs, int repeatingTimes, Runnable runnable) {
		stopTimer(timerName);
		startNewTimer(timerName, startingDelayInMs, repeatingDelayInMs, repeatingTimes, runnable);
	}

	public static void stopTimer(String timerName) {
		AnimationTimer timer = timers.get(timerName);
		if (timer != null) {
			timer.stop();
			timers.remove(timerName);
		}
	}

	public static void stopAllTimers() {
		for (String timerName : new ArrayList<>(timers.keySet()))
			stopTimer(timerName);
	}

	public static Set<String> getAllTimersNames()
		{ return timers.keySet(); }
	
}
