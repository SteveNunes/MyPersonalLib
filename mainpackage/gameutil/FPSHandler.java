package gameutil;

import util.Misc;

public class FPSHandler {

	private int gameCyclesPerSecond;
	private double nextCicleAt;
	private int gameFrameSkip;
	private int frameSkip;
	private long elapsedFrames;
	private int fps;
	private int cps;
	private long fpsTimer;
	private int currentFPS;
	private int currentCPS;
	private Runnable callsWhileWaitingForFPS;
	
	public FPSHandler(int cyclesPerSecond, int frameSkip) {
		gameCyclesPerSecond = cyclesPerSecond;
		gameFrameSkip = frameSkip;
		this.frameSkip = frameSkip;
		elapsedFrames = 0;
		fps = 0;
		cps = 0;
		nextCicleAt = System.currentTimeMillis();
		fpsTimer = System.currentTimeMillis();
		currentFPS = 0;
		currentCPS = 0;
		callsWhileWaitingForFPS = null;
	}
	
	public FPSHandler(int cyclesPerSecond)
		{ this(cyclesPerSecond, 0); }
	
	public void setEventToRunWhileWaitingForFPS(Runnable runnable)
		{ runnable = callsWhileWaitingForFPS; }

	public void setCPS(int fps)
		{ gameCyclesPerSecond = fps; }

	public void setFrameSkip(int frameSkip)
		{ this.gameFrameSkip = frameSkip; }
	
	public int getFPS()
		{ return currentFPS; }

	public int getCPS()
		{ return currentCPS; }

	public long getElapsedFrames()
		{ return elapsedFrames; }

	/**
	 * Call this method on your main loop every frame.
	 */
	public void fpsCounter() {
		if (gameCyclesPerSecond > 0) {
			while (System.currentTimeMillis() < nextCicleAt) {
				if (callsWhileWaitingForFPS != null)
					callsWhileWaitingForFPS.run();
				if (System.currentTimeMillis() < nextCicleAt)
					Misc.sleep(1);
			}
			if (System.currentTimeMillis() >= fpsTimer) {
				fpsTimer += 1000;
				currentFPS = fps;
				currentCPS = cps;
				fps = 0;
				cps = 0;
			}
			nextCicleAt += 1000d / gameCyclesPerSecond;
		}
		frameSkip++;
		cps++;
		if (gameFrameSkip == 0 || frameSkip > gameFrameSkip) {
			frameSkip = 0;
			fps++;
			elapsedFrames++;
		}
	}
	
	/*
	 * Only update your screen when this method returns {@code true}.
	 * It will guarante that your draw will run propperly at the
	 * desired frameskip. While it returns {@code false} just update
	 * game stuffs without any drawning. 
	 */
	public Boolean ableToDraw()
		{ return gameFrameSkip == 0 || frameSkip >= gameFrameSkip; }

}
