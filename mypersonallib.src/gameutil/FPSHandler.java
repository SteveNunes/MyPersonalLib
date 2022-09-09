package gameutil;

import java.util.function.Consumer;

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

	public void setFPS(int fps)
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
	public void fpsCounter(Consumer<?> consumerWhileWaitingForFPS) {
		if (gameCyclesPerSecond > 0) {
			while (System.currentTimeMillis() < nextCicleAt)
				consumerWhileWaitingForFPS.accept(null);
			nextCicleAt += 1000 / gameCyclesPerSecond;
		}
		frameSkip++;
		elapsedFrames++;
		cps++;
		if (ableToDraw()) {
			frameSkip = 0;
			fps++;
		}
		if (System.currentTimeMillis() >= fpsTimer) {
			fpsTimer += 1000;
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
