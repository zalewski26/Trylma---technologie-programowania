package com.zalewskiwojtczak;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Executors;

public class TrylmaServer {

    public TrylmaServer(int decide) throws IOException {
        try (var listener = new ServerSocket(59090)) {
            System.out.println("Trylma server is running...");
            var pool = Executors.newFixedThreadPool(decide);
            while (true) {
                TrylmaGame game = new TrylmaGame(decide);
                switch (decide) {
                    case 2:
                        pool.execute(game.new playerHandler(listener.accept(), 1));
                        pool.execute(game.new playerHandler(listener.accept(), 6));
                        break;
                    case 3:
                        pool.execute(game.new playerHandler(listener.accept(), 2));
                        pool.execute(game.new playerHandler(listener.accept(), 3));
                        pool.execute(game.new playerHandler(listener.accept(), 6));
                        break;
                    case 4:
                        pool.execute(game.new playerHandler(listener.accept(), 2));
                        pool.execute(game.new playerHandler(listener.accept(), 3));
                        pool.execute(game.new playerHandler(listener.accept(), 5));
                        pool.execute(game.new playerHandler(listener.accept(), 4));
                        break;
                    case 6:
                        pool.execute(game.new playerHandler(listener.accept(), 1));
                        pool.execute(game.new playerHandler(listener.accept(), 3));
                        pool.execute(game.new playerHandler(listener.accept(), 5));
                        pool.execute(game.new playerHandler(listener.accept(), 6));
                        pool.execute(game.new playerHandler(listener.accept(), 4));
                        pool.execute(game.new playerHandler(listener.accept(), 2));
                        break;
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("Ilość graczy");
        frame.setSize(300, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout());
        frame.setVisible(true);

        int options[] = {2, 3, 4, 6};
        for (int i = 0; i < options.length; i++){
            final int j = i;
            JButton button = new JButton(String.valueOf(options[i]));
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    try {
                        frame.setVisible(false);
                        TrylmaServer server = new TrylmaServer(options[j]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            frame.getContentPane().add(button);
        }
    }
}
