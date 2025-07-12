package util;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class Timer {

	static {
		Misc.addShutdownEvent(Timer::close);
	}

	private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private static final Map<String, Long> timersCurrentLoopTime = new ConcurrentHashMap<>();
	private static final Map<String, Long> timersStartTime = new ConcurrentHashMap<>();
	private static final Map<String, Long> timersTotalCycles = new ConcurrentHashMap<>();
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
		timersStartTime.put(timerName, System.currentTimeMillis());
		timersTotalCycles.put(timerName, 0L);
		final int[] counter = { 0 };
		ScheduledFuture<?> initialFuture = scheduler.schedule(() -> {
			updateTimerCurrentLoopTime(timerName);
			runnable.run();
			counter[0]++;
			if (repeatingDelay != null && repeatingDelay != Duration.ZERO) {
				ScheduledFuture<?> repeatingFuture = scheduler.scheduleAtFixedRate(() -> {
					updateTimerCurrentLoopTime(timerName);
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

	private static void updateTimerCurrentLoopTime(String timerName) {
		timersCurrentLoopTime.put(timerName, System.currentTimeMillis());
		timersTotalCycles.put(timerName, timersTotalCycles.get(timerName) + 1);
	}

	public static long getTimerTotalCycles(String timerName) {
		return !timersTotalCycles.containsKey(timerName) ? -1 : timersTotalCycles.get(timerName);
	}

	public static Duration getTimerTotalDuration(String timerName) {
		int v = !timersStartTime.containsKey(timerName) ? -1 : (int)(System.currentTimeMillis() - timersStartTime.get(timerName));
		return Duration.ofMillis(v);
	}

	public static Duration getTimerCurrentLoopDuration(String timerName) {
		int v = !timersCurrentLoopTime.containsKey(timerName) ? -1 : (int)(System.currentTimeMillis() - timersCurrentLoopTime.get(timerName));
		return Duration.ofMillis(v);
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
				timersStartTime.remove(timerName);
				timersCurrentLoopTime.remove(timerName);
			}
		}
	}

	public static void stopAllTimers() {
		List<String> names = new ArrayList<>(timers.keySet());
		for (String timerName : names)
			stopTimer(timerName);
	}

	public static void stopScheduler() {
		scheduler.shutdown();
	}

	public static Set<String> getAllTimersNames() {
		return timers.keySet();
	}

	private static void close() {
		stopAllTimers();
		stopScheduler();
	}
}
