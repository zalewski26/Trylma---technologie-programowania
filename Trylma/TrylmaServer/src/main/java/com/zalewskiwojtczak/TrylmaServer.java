package com.zalewskiwojtczak;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Executors;

public class TrylmaServer {

    private TrylmaServer(int decide) throws IOException {
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

    public static void main(String[] args) throws Exception {
        JFrame frame=new JFrame("Opcje serwera");
        frame.setSize(300,300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        JPanel panel=new JPanel();
        JButton two=new JButton("Dwóch graczy");
        JButton three=new JButton("Trzech graczy");
        JButton four=new JButton("Czterech graczy");
        JButton six=new JButton("Sześciu graczy");
        two.setPreferredSize(new Dimension(100, 100));
        panel.setLayout(new GridLayout(4,1));
        frame.add(panel);
        panel.add(two);
        panel.add(three);
        panel.add(four);
        panel.add(six);
        frame.pack();
        two.setActionCommand("2");
        three.setActionCommand("3");
        four.setActionCommand("4");
        six.setActionCommand("6");
        two.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                try {
                    frame.setVisible(false);
                    TrylmaServer server = new TrylmaServer(2);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        three.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                try {
                    frame.setVisible(false);
                    TrylmaServer server = new TrylmaServer(3);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        four.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                try {
                    frame.setVisible(false);
                    TrylmaServer server = new TrylmaServer(4);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        six.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                try {
                    frame.setVisible(false);
                    TrylmaServer server = new TrylmaServer(6);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }
}
