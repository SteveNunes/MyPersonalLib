package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public abstract class DurationTimerFX {

	static {
		Misc.addShutdownEvent(DurationTimerFX::close);
	}

	private static final Map<String, Timeline> timers = new ConcurrentHashMap<>();
	private static final Map<String, Long> timersStartTime = new ConcurrentHashMap<>();
	private static final Map<String, Long> timersCurrentLoopTime = new ConcurrentHashMap<>();
	private static final Map<String, Long> timersTotalCycles = new ConcurrentHashMap<>();
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
		timersStartTime.put(timerName, System.currentTimeMillis());
		timersTotalCycles.put(timerName, 0L);
		final int repeatingTimes2 = repeatingTimes - 1;
		final Duration repeatingDelay2 = repeatingDelay;
		if (startingDelay != null) {
			if (startingDelay == Duration.ZERO)
				startingDelay = Duration.millis(1);
			Timeline initialTimeline = new Timeline();
			timers.put(timerName, initialTimeline);
			initialTimeline.getKeyFrames().add(new KeyFrame(startingDelay, event -> {
				updateTimerCurrentLoopTime(timerName);
				runnable.run();
				if (repeatingDelay2 != Duration.ZERO) {
					Timeline repeatTimeline = new Timeline();
					repeatTimeline.setOnFinished(e -> timers.remove(timerName));
					repeatTimeline.getKeyFrames().add(new KeyFrame(repeatingDelay2, e -> {
						updateTimerCurrentLoopTime(timerName);
						runnable.run();
					}));
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
			if (startingDelay == null || startingDelay == Duration.ZERO) {
				updateTimerCurrentLoopTime(timerName);
				runnable.run();
			}
			Timeline repeatTimeline = new Timeline();
			timers.put(timerName, repeatTimeline);
			repeatTimeline.setOnFinished(e -> timers.remove(timerName));
			repeatTimeline.getKeyFrames().add(new KeyFrame(repeatingDelay, e -> {
				updateTimerCurrentLoopTime(timerName);
				runnable.run();
			}));
			repeatTimeline.setCycleCount(repeatingTimes2 > 0 ? repeatingTimes2 : Timeline.INDEFINITE);
			repeatTimeline.play();
		}
		else
			throw new RuntimeException("Unable to start a timer without at least a startingDelay or a repeatingDelay");
	}
	
	private static void updateTimerCurrentLoopTime(String timerName) {
		timersCurrentLoopTime.put(timerName, System.currentTimeMillis());
		timersTotalCycles.put(timerName, timersTotalCycles.get(timerName) + 1);
	}

	public static long getTimerTotalCycles(String timerName) {
		return !timersTotalCycles.containsKey(timerName) ? -1 : timersTotalCycles.get(timerName);
	}

	public static Duration getTimerTotalDuration(String timerName) {
		int v = !timersStartTime.containsKey(timerName) ? -1 : (int)(System.currentTimeMillis() - timersStartTime.get(timerName));
		return Duration.millis(v);
	}

	public static Duration getTimerCurrentLoopDuration(String timerName) {
		int v = !timersCurrentLoopTime.containsKey(timerName) ? -1 : (int)(System.currentTimeMillis() - timersCurrentLoopTime.get(timerName));
		return Duration.millis(v);
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
				timersStartTime.remove(timerName);
				timersCurrentLoopTime.remove(timerName);
			}
		}
	}

	public static void stopAllTimers() {
		List<String> list = new ArrayList<>(timers.keySet());
		for (String timerName : list)
			stopTimer(timerName);
	}

	public static Set<String> getAllTimersNames() {
		return timers.keySet();
	}

	private static void close() {
		fxApplicationIsClosed = true;
		stopAllTimers();
	}
}
