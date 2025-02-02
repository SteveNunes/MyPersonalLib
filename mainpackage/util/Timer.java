package util;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class Timer {

	static {
		Misc.addShutdownEvent(() -> close());
	}

	private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private static final Map<String, ScheduledFuture<?>> timers = new ConcurrentHashMap<>();

	private static void ensureSchedulerActive() {
		if (scheduler.isShutdown() || scheduler.isTerminated())
			scheduler = Executors.newScheduledThreadPool(1);
	}

	public static void createTimer(String timerName, Duration startingDelay, Runnable runnable) {
		createTimer(timerName, startingDelay, null, 1, runnable);
	}

	public static void createTimer(String timerName, Duration repeatingDelay, int repeatingTimes, Runnable runnable) {
		createTimer(timerName, null, repeatingDelay, repeatingTimes, runnable);
	}

	public static void createTimer(String timerName, Duration startingDelay, Duration repeatingDelay, int repeatingTimes, Runnable runnable) {
		ensureSchedulerActive();
		if (timers.containsKey(timerName))
			resetTimer(timerName, startingDelay, repeatingDelay, repeatingTimes, runnable);
		else
			startNewTimer(timerName, startingDelay, repeatingDelay, repeatingTimes, runnable);
	}

	public static boolean timerExists(String timerName) {
		return timers.containsKey(timerName);
	}

	private static void startNewTimer(String timerName, Duration startingDelay, Duration repeatingDelay, int repeatingTimes, Runnable runnable) {
		ensureSchedulerActive();
		final int[] counter = { 0 };
		ScheduledFuture<?> initialFuture = scheduler.schedule(() -> {
			runnable.run();
			counter[0]++;
			if (repeatingDelay != null && repeatingDelay != Duration.ZERO) {
				ScheduledFuture<?> repeatingFuture = scheduler.scheduleAtFixedRate(() -> {
					runnable.run();
					if (repeatingTimes > 0 && ++counter[0] >= repeatingTimes)
						stopTimer(timerName);
				}, (long) repeatingDelay.toMillis(), (long) repeatingDelay.toMillis(), TimeUnit.MILLISECONDS);
				timers.put(timerName, repeatingFuture);
			}
			else
				stopTimer(timerName);
		}, (startingDelay != null ? (long) startingDelay.toMillis() : 0), TimeUnit.MILLISECONDS);
		timers.put(timerName, initialFuture);
	}

	public static void resetTimer(String timerName, Duration startingDelay, Duration repeatingDelay, int repeatingTimes, Runnable runnable) {
		stopTimer(timerName);
		startNewTimer(timerName, startingDelay, repeatingDelay, repeatingTimes, runnable);
	}

	public static void stopTimer(String timerName) {
		if (timers.containsKey(timerName)) {
			ScheduledFuture<?> future = timers.get(timerName);
			if (future != null) {
				future.cancel(false);
				timers.remove(timerName);
			}
		}
	}

	public static void stopAllTimers() {
		for (String timerName : new ArrayList<>(timers.keySet()))
			stopTimer(timerName);
	}

	public static void stopScheduler() {
		scheduler.shutdown();
	}

	public static Set<String> getAllTimersNames() {
		return timers.keySet();
	}

	public static void close() {
		stopAllTimers();
		stopScheduler();
	}
}
