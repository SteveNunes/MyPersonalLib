package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public abstract class DurationTimerFX {

	static {
		Misc.addShutdownEvent(() -> close());
	}

	private static final Map<String, Timeline> timers = new HashMap<>();
	private static boolean fxApplicationIsClosed = false;

	public static void createTimer(String timerName, Duration startingDelay, Runnable runnable) {
		createTimer(timerName, startingDelay, Duration.ZERO, 1, runnable); // -1 for no repeating delay, 1 execution
	}

	public static void createTimer(String timerName, Duration repeatingDelay, int repeatingTimes, Runnable runnable) {
		createTimer(timerName, Duration.ZERO, repeatingDelay, repeatingTimes, runnable); // 0 for immediate start
	}

	public static void createTimer(String timerName, Duration startingDelay, Duration repeatingDelay, int repeatingTimes, Runnable runnable) {
		if (timers.containsKey(timerName))
			resetTimer(timerName, startingDelay, repeatingDelay, repeatingTimes, runnable);
		else
			startNewTimer(timerName, startingDelay, repeatingDelay, repeatingTimes, runnable);
	}
	
	public static boolean timerExists(String timerName) {
		return timers.containsKey(timerName);
	}

	private static void startNewTimer(String timerName, Duration startingDelay, Duration repeatingDelay, int repeatingTimes, Runnable runnable) {
    if (fxApplicationIsClosed)
			return;
		final int repeatingTimes2 = repeatingTimes - 1;
		final Duration repeatingDelay2 = repeatingDelay;
		if (startingDelay != null) {
			if (startingDelay == Duration.ZERO)
				startingDelay = Duration.millis(1);
			Timeline initialTimeline = new Timeline();
			timers.put(timerName, initialTimeline);
			initialTimeline.getKeyFrames().add(new KeyFrame(startingDelay, event -> {
				runnable.run();
				if (repeatingDelay2 != Duration.ZERO) {
					Timeline repeatTimeline = new Timeline();
					repeatTimeline.setOnFinished(e -> timers.remove(timerName));
					repeatTimeline.getKeyFrames().add(new KeyFrame(repeatingDelay2, e -> runnable.run()));
					repeatTimeline.setCycleCount(repeatingTimes2 > 0 ? repeatingTimes2 : Timeline.INDEFINITE);
					repeatTimeline.play();
					timers.put(timerName, repeatTimeline);
				}
				else
					timers.remove(timerName);
			}));
			initialTimeline.setCycleCount(1);
			initialTimeline.play();
		}
		else if (repeatingDelay != null) {
			if (repeatingDelay == Duration.ZERO)
				repeatingDelay = Duration.millis(1);
			if (startingDelay == null || startingDelay == Duration.ZERO)
				runnable.run();
			Timeline repeatTimeline = new Timeline();
			timers.put(timerName, repeatTimeline);
			repeatTimeline.setOnFinished(e -> timers.remove(timerName));
			repeatTimeline.getKeyFrames().add(new KeyFrame(repeatingDelay, e -> runnable.run()));
			repeatTimeline.setCycleCount(repeatingTimes2 > 0 ? repeatingTimes2 : Timeline.INDEFINITE);
			repeatTimeline.play();
		}
		else
			throw new RuntimeException("Unable to start a timer without at least a startingDelay or a repeatingDelay");
	}

	public static void resetTimer(String timerName, Duration startingDelay, Duration repeatingDelay, int repeatingTimes, Runnable runnable) {
		stopTimer(timerName);
		startNewTimer(timerName, startingDelay, repeatingDelay, repeatingTimes, runnable);
	}

	public static void stopTimer(String timerName) {
		if (timers.containsKey(timerName)) {
			Timeline timeline = timers.get(timerName);
			if (timeline != null) {
				timeline.stop();
				timers.remove(timerName);
			}
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
