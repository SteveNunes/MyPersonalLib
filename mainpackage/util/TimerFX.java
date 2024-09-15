package util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javafx.animation.AnimationTimer;

public class TimerFX {

	private final Map<String, AnimationTimer> timers = new HashMap<>();

	public void createTimer(String timerName, long startingDelayInMs, Runnable runnable)
		{ createTimer(timerName, startingDelayInMs, 1, 1, runnable); }
	
	public void createTimer(String timerName, long repeatingDelayInMs, int repeatingTimes, Runnable runnable)
		{ createTimer(timerName, 0, repeatingDelayInMs, repeatingTimes, runnable); }

	public void createTimer(String timerName, long startingDelayInMs, long repeatingDelayInMs, int repeatingTimes, Runnable runnable) {
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

				if (elapsedSinceStart >= startingDelayInMs * 1_000_000L) {
					if (executionCount[0] == 0 && elapsedSinceLastRun >= startingDelayInMs * 1_000_000L) {
						runnable.run();
						executionCount[0]++;
						lastTime[0] = now;
					}
					if (executionCount[0] > 0 && elapsedSinceLastRun >= repeatingDelayInMs * 1_000_000L) {
						runnable.run();
						executionCount[0]++;
						lastTime[0] = now;
					}
					if (repeatingTimes > 0 && executionCount[0] >= repeatingTimes) {
						stopTimer(timerName);
					}
				}
			}
		};

		timers.put(timerName, timer);

		timer.start();
	}

	public void stopTimer(String timerName) {
		AnimationTimer timer = timers.get(timerName);
		if (timer != null) {
			timer.stop();
			timers.remove(timerName);
			checkAndExit();
		}
	}

	public void stopAllTimers() {
		for (String timerName : timers.keySet())
			stopTimer(timerName);
	}

	public Set<String> getAllTimersNames()
		{ return timers.keySet(); }

	private void checkAndExit() {
		if (timers.isEmpty())
			System.exit(0);
	}

}
