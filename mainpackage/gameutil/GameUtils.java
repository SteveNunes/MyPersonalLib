package gameutil;

import java.util.function.Predicate;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public abstract class GameUtils {

	public static Timeline createTimeLine(int fps, Predicate<Object> predicateForStop, Runnable mainLoop) {
		Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(1000 / fps), e -> {
			mainLoop.run();
			if (predicateForStop.test(null))
				timeline.stop();
		}));
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();
		return timeline;
	}
	
}
