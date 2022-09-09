package gameutil;

import java.util.function.Consumer;

import javafx.application.Platform;

public class GameTools {

	/**
	 * Chame esse método no final do seu main loop, passando no consumer a chamada do mesmo método
	 * para um efetivo loop infinito sem dar freezing
	 */
	public static void callMethodAgain(Consumer<?> consumer)
		{ Platform.runLater(new Runnable() { public void run() { consumer.accept(null); } }); }

	
	
}
