package com.zalewskiwojtczak;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Executors;

public class TrylmaServer {
    private final static int VAL = 6;
    public static void main(String[] args) throws Exception{
        try (var listener = new ServerSocket(59090)) {
            System.out.println("Trylma server is running...");
            var pool = Executors.newFixedThreadPool(VAL);
            while (true) {
                TrylmaGame game = new TrylmaGame();
                pool.execute(game.new playerHandler(listener.accept(), 1));
                pool.execute(game.new playerHandler(listener.accept(), 2));
                pool.execute(game.new playerHandler(listener.accept(), 3));
                pool.execute(game.new playerHandler(listener.accept(), 4));
                pool.execute(game.new playerHandler(listener.accept(), 5));
                pool.execute(game.new playerHandler(listener.accept(), 6));
            }
        }
    }
}
