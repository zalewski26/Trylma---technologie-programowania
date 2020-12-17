package TestSocket;

import java.net.ServerSocket;
import java.util.concurrent.Executors;

public class TrylmaServer {

    public static void main(String[] args) throws Exception {
        try (var listener = new ServerSocket(59090)) {
            System.out.println("My server is running...");
            var pool = Executors.newFixedThreadPool(6);
            while (true) {
                myGame game = new myGame();
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
