package gui.util;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * 
 * @author stackoverflow (Original source)
 * @ahthor Steve (Code enhance)
 * 
 * Para ativar/desativar listeners com facilidade. Para isso,
 * basta adicionar o listener dessa forma:
 * 
 * ListenerHandle<Boolean> nomeDoHandler = new ListenerHandle<>(checkBox.selectedProperty(), listener);
 * 
 * E então usar:
 * 
 * handler.attach(); para ativar
 * handler.detach(); para desativar
 * 
 */

public class ListenerHandle<T> {

	private final ObservableValue<T> observable;
	private final ChangeListener<? super T> changeListener;
	private final InvalidationListener invalidationListener;

	private boolean attached;

	public ListenerHandle(ObservableValue<T> observable, ChangeListener<? super T> changeListener) {
		this.observable = observable;
		this.changeListener = changeListener;
		this.invalidationListener = null;
	}

	public ListenerHandle(ObservableValue<T> observable, InvalidationListener invalidationListener) {
		this.observable = observable;
		this.invalidationListener = invalidationListener;
		this.changeListener = null;
	}

	public void attach() {
		if (!attached) {
			if (changeListener != null)
				observable.addListener(changeListener);
			else
				observable.addListener(invalidationListener);
			attached = true;
		}
	}

	public void detach() {
		if (attached) {
			if (changeListener != null)
				observable.removeListener(changeListener);
			else
				observable.removeListener(invalidationListener);
			attached = false;
		}
	}

}