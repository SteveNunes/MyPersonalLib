package util;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class Timer {

	private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private static final Map<String, ScheduledFuture<?>> timers = new ConcurrentHashMap<>();

	private static void ensureSchedulerActive() {
		if (scheduler.isShutdown() || scheduler.isTerminated())
			scheduler = Executors.newScheduledThreadPool(1);
	}

	public static void createTimer(String timerName, long startingDelayInMs, Runnable runnable)
		{ createTimer(timerName, startingDelayInMs, 1, 1, runnable); }

	public static void createTimer(String timerName, long repeatingDelayInMs, int repeatingTimes, Runnable runnable)
		{ createTimer(timerName, 0, repeatingDelayInMs, repeatingTimes, runnable); }

	public static void createTimer(String timerName, long startingDelayInMs, long repeatingDelayInMs, int repeatingTimes, Runnable runnable) {
		ensureSchedulerActive();
		if (timers.containsKey(timerName))
			resetTimer(timerName, startingDelayInMs, repeatingDelayInMs, repeatingTimes, runnable);
		else
			startNewTimer(timerName, startingDelayInMs, repeatingDelayInMs, repeatingTimes, runnable);
	}

	private static void startNewTimer(String timerName, long startingDelayInMs, long repeatingDelayInMs, int repeatingTimes, Runnable runnable) {
		final int[] counter = { 0 };
		ScheduledFuture<?> future;

		if (repeatingTimes == 1)
			future = scheduler.schedule(() -> runnable.run(), startingDelayInMs, TimeUnit.MILLISECONDS);
		else {
			future = scheduler.scheduleAtFixedRate(() -> {
				runnable.run();
				if (repeatingTimes > 0 && ++counter[0] >= repeatingTimes) {
					stopTimer(timerName);
				}
			}, startingDelayInMs, repeatingDelayInMs, TimeUnit.MILLISECONDS);
		}

		timers.put(timerName, future);
	}

	public static void resetTimer(String timerName, long startingDelayInMs, long repeatingDelayInMs, int repeatingTimes, Runnable runnable) {
		stopTimer(timerName);
		startNewTimer(timerName, startingDelayInMs, repeatingDelayInMs, repeatingTimes, runnable);
	}

	public static void stopTimer(String timerName) {
		ScheduledFuture<?> future = timers.get(timerName);
		if (future != null) {
			future.cancel(false);
			timers.remove(timerName);
		}
	}

	public static void stopAllTimers() {
		for (String timerName : new ArrayList<>(timers.keySet()))
			stopTimer(timerName);
	}

	public static void stopScheduler()
		{ scheduler.shutdown(); }

	public static Set<String> getAllTimersNames()
		{ return timers.keySet(); }

	public static void close() {
		stopAllTimers();
		stopScheduler();
	}

}
