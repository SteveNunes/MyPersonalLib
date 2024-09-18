package util;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class Timer {

	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private static final Map<String, ScheduledFuture<?>> timers = new ConcurrentHashMap<>();

	public static void createTimer(String timerName, long startingDelayInMs, Runnable runnable)
		{ createTimer(timerName, startingDelayInMs, 1, 1, runnable); }
	
	public static void createTimer(String timerName, long repeatingDelayInMs, int repeatingTimes, Runnable runnable)
		{ createTimer(timerName, 0, repeatingDelayInMs, repeatingTimes, runnable); }

	public static void createTimer(String timerName, long startingDelayInMs, long repeatingDelayInMs, int repeatingTimes, Runnable runnable) {
		final int[] counter = {0};
		ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(() -> {
			runnable.run();
			if (repeatingTimes > 0 && ++counter[0] >= repeatingTimes)
				stopTimer(timerName);
		}, startingDelayInMs, repeatingDelayInMs, TimeUnit.MILLISECONDS);
		timers.put(timerName, future);
	}

	public static void stopTimer(String timerName) {
		ScheduledFuture<?> future = timers.get(timerName);
		if (future != null) {
			future.cancel(false);
			timers.remove(timerName);
			checkAndShutdownScheduler();
		}
	}

	public static void stopAllTimers() {
		for (String timerName : timers.keySet())
			stopTimer(timerName);
		scheduler.shutdown();
	}

	public static Set<String> getAllTimersNames()
		{ return timers.keySet(); }

	private static void checkAndShutdownScheduler() {
		if (timers.isEmpty())
			scheduler.shutdown();
	}
	
}
