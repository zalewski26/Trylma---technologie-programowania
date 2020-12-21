package com.zalewskiwojtczak;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Executors;
/** Klasa serwera aplikacji Trylma
 *
 */
public class TrylmaServer {
    /** Konstrukto klasy Trylmaserver
     * @param decide- liczba graczy biorącuch udział w rozgrywce
     * @throws IOException
     */
    public TrylmaServer(int decide) throws IOException {
        //proba utworzenia gniazda na podanym porcie
        try (var listener = new ServerSocket(59090)) {
            System.out.println("Trylma server is running...");
            /**utworzenie osobnych wątków dla każdego gracza */
            var pool = Executors.newFixedThreadPool(decide);
            while (true) {
                //utworzenie gry przez serwer...
                TrylmaGame game = new TrylmaGame(decide);
                //...oraz dołączenie wątków graczy
                switch (decide) {
                    //w zależności od liczby klasy uruchamami odpowiednią liczbę threadów
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
    /** Funkcja main klasy TrylmaServer tworząca menu wyboru liczby graczy
     * @param args
     */
    public static void main(String[] args) throws IOException
    {
        //utworzenie okna
        JFrame frame=new JFrame("Ilość graczy");
        frame.setSize(300,300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(4,1));
        frame.setVisible(true);
        int options[] ={2,3,4,6};
        //dodanie przyciskow
        for(int i=0;i<options.length;i++)
        {
            final int j=i;
            JButton button=new JButton(String.valueOf(options[i]));
            button.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent actionEvent)
                {
                    try
                    {
                        frame.setVisible(false);
                        //utworzenie serwera dla odpowiedniej liczby graczy
                        TrylmaServer server=new TrylmaServer(options[j]);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            frame.getContentPane().add(button);
        }
    }
}