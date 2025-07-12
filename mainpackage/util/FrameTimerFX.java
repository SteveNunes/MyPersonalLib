package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javafx.animation.AnimationTimer;
import javafx.util.Duration;

public abstract class FrameTimerFX {

	static {
		Misc.addShutdownEvent(FrameTimerFX::close);
	}

	private static final Map<String, AnimationTimer> timers = new ConcurrentHashMap<>();
	private static final Map<String, Long> timersCurrentLoopTime = new ConcurrentHashMap<>();
	private static final Map<String, Long> timersStartTime = new ConcurrentHashMap<>();
	private static final Map<String, Long> timersTotalCycles = new ConcurrentHashMap<>();
	private static boolean fxApplicationIsClosed = false;

	public static void createTimer(String timerName, long startingDelayInFrames, Runnable runnable) {
		createTimer(timerName, startingDelayInFrames, -1, 1, runnable); // -1 para sem repetição, 1 execução
	}

	public static void createTimer(String timerName, long repeatingDelayInFrames, int repeatingTimes, Runnable runnable) {
		createTimer(timerName, 0, repeatingDelayInFrames, repeatingTimes, runnable); // 0 para início imediato
	}

	public static void createTimer(String timerName, long startingDelayInFrames, long repeatingDelayInFrames, int repeatingTimes, Runnable runnable) {
		if (timerName == null || runnable == null) {
			System.out.println("Tentativa de criar um timer com nome ou ação nula.");
			return;
		}
		if (timers.containsKey(timerName))
			resetTimer(timerName, startingDelayInFrames, repeatingDelayInFrames, repeatingTimes, runnable);
		else
			startNewTimer(timerName, startingDelayInFrames, repeatingDelayInFrames, repeatingTimes, runnable);
	}

	public static boolean timerExists(String timerName) {
		return timers.containsKey(timerName);
	}

	private static void startNewTimer(String timerName, long startingDelayInFrames, long repeatingDelayInFrames, int repeatingTimes, Runnable runnable) {
		if (fxApplicationIsClosed)
			return;
		timersStartTime.put(timerName, System.currentTimeMillis());
		timersTotalCycles.put(timerName, 0L);
		final long[] frameCount = { 0 };
		final int[] executionCount = { 0 };
		final boolean[] isFirstExecution = { true };
		AnimationTimer timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				if (fxApplicationIsClosed || !timers.containsKey(timerName) || timers.get(timerName) == null) {
					timers.remove(timerName);
					stop();
					return;
				}
				frameCount[0]++;
				if (isFirstExecution[0]) {
					if (frameCount[0] >= startingDelayInFrames) {
						updateTimerCurrentLoopTime(timerName);
						runnable.run();
						executionCount[0]++;
						frameCount[0] = 0;
						isFirstExecution[0] = false;
						if (repeatingTimes == 1) {
							stopTimer(timerName);
							return;
						}
					}
				}
				else if (repeatingDelayInFrames > 0 && frameCount[0] >= repeatingDelayInFrames) {
					updateTimerCurrentLoopTime(timerName);
					runnable.run();
					executionCount[0]++;
					frameCount[0] = 0;
					if (repeatingTimes > 0 && executionCount[0] >= repeatingTimes)
						stopTimer(timerName);
				}
			}
		};
		timers.put(timerName, timer);
		timer.start();
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

	public static void resetTimer(String timerName, long startingDelayInFrames, long repeatingDelayInFrames, int repeatingTimes, Runnable runnable) {
		stopTimer(timerName);
		startNewTimer(timerName, startingDelayInFrames, repeatingDelayInFrames, repeatingTimes, runnable);
	}

	public static void stopTimer(String timerName) {
		if (timers.containsKey(timerName)) {
			AnimationTimer timer = timers.get(timerName);
			if (timer != null) {
				timer.stop();
				timers.remove(timerName);
				timersStartTime.remove(timerName);
				timersCurrentLoopTime.remove(timerName);
			}
		}
	}

	public static void stopAllTimers() {
		List<String> names = new ArrayList<>(timers.keySet());
		for (String timerName : names) {
			AnimationTimer timer = timers.get(timerName);
			if (timer != null)
				timer.stop();
			timers.remove(timerName);
		}
	}

	public static Set<String> getAllTimersNames() {
		return timers.keySet();
	}

	private static void close() {
		fxApplicationIsClosed = true;
		stopAllTimers();
	}
}
