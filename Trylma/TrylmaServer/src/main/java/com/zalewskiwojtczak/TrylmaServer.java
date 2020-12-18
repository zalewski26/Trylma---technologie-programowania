package com.zalewskiwojtczak;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Executors;

public class TrylmaServer {
    public static void main(String[] args) throws Exception{
        try (var listener = new ServerSocket(59090)) {
            System.out.println("Trylma server is running...");
            var pool = Executors.newFixedThreadPool(2);
            while (true) {
                TrylmaGame game = new TrylmaGame();
                pool.execute(game.new playerHandler(listener.accept(), 1));
                pool.execute(game.new playerHandler(listener.accept(), 6));
            }
        }
    }
}
