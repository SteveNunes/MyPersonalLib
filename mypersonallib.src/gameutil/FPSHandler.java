package gameutil;

import util.Misc;

public class FPSHandler {

	private int gameCyclesPerSecond;
	private long nextCicleAt;
	private int gameFrameSkip;
	private int frameSkip;
	private long elapsedFrames;
	private int fps;
	private int cps;
	private long fpsTimer;
	private int currentFPS;
	private int currentCPS;
	
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
	}

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
			Misc.sleep(nextCicleAt - System.currentTimeMillis());
			nextCicleAt += 1000 / gameCyclesPerSecond;
		}
		frameSkip++;
		cps++;
		if (gameFrameSkip == 0 || ++frameSkip > gameFrameSkip) {
			frameSkip = 0;
			fps++;
			elapsedFrames++;
		}
		if (System.currentTimeMillis() >= fpsTimer) {
			fpsTimer = System.currentTimeMillis() + 1000;
			currentFPS = fps;
			currentCPS = cps;
			fps = 0;
			cps = 0;
		}
	}
	
	/*
	 * Only update your screen when this method returns {@code true},
	 * otherwise, just update game stuffs without any drawning. 
	 */
	public Boolean ableToDraw()
		{ return gameFrameSkip == 0 || frameSkip >= gameFrameSkip; }

}
