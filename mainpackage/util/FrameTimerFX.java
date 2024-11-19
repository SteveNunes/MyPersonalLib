package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javafx.animation.AnimationTimer;

public abstract class FrameTimerFX {

	private static final Map<String, AnimationTimer> timers = new HashMap<>();
	private static boolean fxApplicationIsClosed = false;

	public static void createTimer(String timerName, long startingDelayInFrames, Runnable runnable) {
		createTimer(timerName, startingDelayInFrames, -1, 1, runnable); // -1 para sem repetição, 1 execução
	}

	public static void createTimer(String timerName, long repeatingDelayInFrames, int repeatingTimes, Runnable runnable) {
		createTimer(timerName, 0, repeatingDelayInFrames, repeatingTimes, runnable); // 0 para início imediato
	}

	public static void createTimer(String timerName, long startingDelayInFrames, long repeatingDelayInFrames, int repeatingTimes, Runnable runnable) {
		if (timers.containsKey(timerName))
			resetTimer(timerName, startingDelayInFrames, repeatingDelayInFrames, repeatingTimes, runnable);
		else
			startNewTimer(timerName, startingDelayInFrames, repeatingDelayInFrames, repeatingTimes, runnable);
	}

	private static void startNewTimer(String timerName, long startingDelayInFrames, long repeatingDelayInFrames, int repeatingTimes, Runnable runnable) {
		if (fxApplicationIsClosed)
			return;
		final long[] frameCount = { 0 };
		final int[] executionCount = { 0 };
		final boolean[] started = { false };
		AnimationTimer timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				if (fxApplicationIsClosed) {
					timers.remove(timerName);
					stop();
					return;
				}
				if (!started[0]) {
					frameCount[0] = 0;
					started[0] = true;
				}
				frameCount[0]++;
				if (executionCount[0] == 0 && frameCount[0] >= startingDelayInFrames) {
					runnable.run();
					executionCount[0]++;
					frameCount[0] = 0;
					if (repeatingTimes == 1) {
						stopTimer(timerName);
						return;
					}
				}
				else if (executionCount[0] > 0 && repeatingDelayInFrames > 0 && frameCount[0] >= repeatingDelayInFrames) {
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

	public static void resetTimer(String timerName, long startingDelayInFrames, long repeatingDelayInFrames, int repeatingTimes, Runnable runnable) {
		stopTimer(timerName);
		startNewTimer(timerName, startingDelayInFrames, repeatingDelayInFrames, repeatingTimes, runnable);
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

	public static Set<String> getAllTimersNames() {
		return timers.keySet();
	}

	public static void close() {
		fxApplicationIsClosed = true;
		stopAllTimers();
	}
}
