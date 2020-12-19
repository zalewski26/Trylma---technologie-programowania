package com.zalewskiwojtczak;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Executors;
/** Klasa serwera aplikacji Trylma
 * 
 */
public class TrylmaServer {
	/** Funkcja main klasy TrylmaServer
	 * @param args
	 */
    public static void main(String[] args) throws Exception{
    	//proba utworzenia gniazda na podanym porcie
        try (var listener = new ServerSocket(59090)) {
        	
            System.out.println("Trylma server is running...");
            /**utworzenie osobnych wątków dla każdego gracza */
            var pool = Executors.newFixedThreadPool(2);
            while (true) {
            	//utworzenie gry przez serwer...
                TrylmaGame game = new TrylmaGame();
                //...oraz dołączenie wątków graczy
                pool.execute(game.new playerHandler(listener.accept(), 1));
                pool.execute(game.new playerHandler(listener.accept(), 6));
            }
        }
    }
}
