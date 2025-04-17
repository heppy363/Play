package com.matteorossi.play.interfaceData;

public interface DataReceiver<T> {

    /**
     * Metodo per ricevere un dato di tipo generico T.
     * @param data il dato passato alla nuova finestra.
     */

    void receiveData(T data);
}
