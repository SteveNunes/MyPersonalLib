package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public abstract class DurationTimerFX {

	private static final Map<String, Timeline> timers = new HashMap<>();
	private static boolean fxApplicationIsClosed = false;

	public static void createTimer(String timerName, Duration startingDelay, Runnable runnable) {
		createTimer(timerName, startingDelay, null, 1, runnable); // -1 for no repeating delay, 1 execution
	}

	public static void createTimer(String timerName, Duration repeatingDelay, int repeatingTimes, Runnable runnable) {
		createTimer(timerName, null, repeatingDelay, repeatingTimes, runnable); // 0 for immediate start
	}

	public static void createTimer(String timerName, Duration startingDelay, Duration repeatingDelay, int repeatingTimes, Runnable runnable) {
		if (timers.containsKey(timerName))
			resetTimer(timerName, startingDelay, repeatingDelay, repeatingTimes, runnable);
		else
			startNewTimer(timerName, startingDelay, repeatingDelay, repeatingTimes, runnable);
	}

	private static void startNewTimer(String timerName, Duration startingDelay, Duration repeatingDelay, int repeatingTimes, Runnable runnable) {
		if (fxApplicationIsClosed)
			return;

		Timeline timeline = new Timeline();
		timeline.setCycleCount(repeatingTimes > 0 ? repeatingTimes : Timeline.INDEFINITE);

		if (startingDelay != null)
			timeline.getKeyFrames().add(new KeyFrame(startingDelay, event -> runnable.run()));

		if (repeatingDelay != null)
			timeline.getKeyFrames().add(new KeyFrame(repeatingDelay, event -> runnable.run()));

		timers.put(timerName, timeline);
		timeline.setOnFinished(event -> {
			if (repeatingTimes > 0)
				stopTimer(timerName);
		});

		timeline.play();
	}

	public static void resetTimer(String timerName, Duration startingDelay, Duration repeatingDelay, int repeatingTimes, Runnable runnable) {
		stopTimer(timerName);
		startNewTimer(timerName, startingDelay, repeatingDelay, repeatingTimes, runnable);
	}

	public static void stopTimer(String timerName) {
		Timeline timeline = timers.get(timerName);
		if (timeline != null) {
			timeline.stop();
			timers.remove(timerName);
		}
	}

	public static void stopAllTimers() {
		for (String timerName : new ArrayList<>(timers.keySet()))
			stopTimer(timerName);
	}

	public static Set<String> getAllTimersNames() {
		return timers.keySet();
	}

	public static void close() {
		fxApplicationIsClosed = true;
		stopAllTimers();
	}
}
