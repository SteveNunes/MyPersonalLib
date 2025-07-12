package gameutil;

import java.util.function.BiPredicate;

import javafx.animation.AnimationTimer;

public abstract class GameUtils {

	/**
	 * 
	 * @param fps - Taxa fixa de quadros que a animação irá rodar
	 * @param conditionForStop - Condição para parar a animação (Retorna Integer (Millis somados de cada handle() antes de fechar o tempo para desenhar 1 frame (para calcular sobrecarga (quanto menor, menos sobrecarregado está))) Integer (Quadros processados no ultimo segundo)
	 * @param mainLoop - Runnable contendo o loop principal da sua aplicação
	 * @return
	 */
	public static AnimationTimer createAnimationTimer(int fps, BiPredicate<Integer, Integer> conditionForStop, Runnable mainLoop) {
		/* fpsCount[0] Tempo para atualizar o FPS
		 * fpsCount[1] Contador de frames do segundo atual 
		 * fpsCount[2] Contador de frames processados no ultimo segundo
		 */
		long[] fpsCount = { System.currentTimeMillis() + 1000, 0, 0 };
		/* framesCount[0] Ajuda para calcular se está no tempo de executar o mainLoop
		 * framesCount[1] Contador dos Ticks atuais antes da chamada de mainLoop
		 * framesCount[2] Contador final de Ticks processados antes da chamada do último mainLoop
		 */
		long[] framesCount = { 0, 0, 0 };
		final long NANOS_PER_FRAME = 1_000_000_000L / fps;
		return new AnimationTimer() {
			@Override
			public void handle(long now) {
				if (conditionForStop.test((int)framesCount[2], (int)fpsCount[2]))
					this.stop();
				else if (now - framesCount[0] >= NANOS_PER_FRAME) {
					long l = System.currentTimeMillis();
					mainLoop.run();
					framesCount[1] += (System.currentTimeMillis() - l);
					framesCount[0] = now - ((now - framesCount[0]) % NANOS_PER_FRAME);
					if (System.currentTimeMillis() >= fpsCount[0]) {
						fpsCount[2] = fpsCount[1];
						fpsCount[1] = 0;
						fpsCount[0] = System.currentTimeMillis() + 1000;
						framesCount[2] = framesCount[1];
						framesCount[1] = 0;
					}
					else
						fpsCount[1]++;
				}
			}
		};
	}
	
}
