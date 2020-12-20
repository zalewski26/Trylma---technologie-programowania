package com.zalewskiwojtczak;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TrylmaClient {
    private final JFrame frame = new JFrame("Trylma");
    private final JLabel label = new JLabel("...");
    private final JButton button = new JButton("skip/finish move");
    private final TrylmaPanel panel;
    private final int[] currentCircle = new int[2];
    private final Socket socket;
    private final Scanner input;
    private final PrintWriter output;

    public TrylmaClient(Socket socket) throws Exception {
        this.socket = socket;
        input = new Scanner(socket.getInputStream());
        output = new PrintWriter(socket.getOutputStream(), true);

        panel = new TrylmaPanel();
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (int row = 0; row < panel.getCircles().length; row++){
                    for (int column = 0; column < panel.getCircles()[row].length; column++){
                        if (panel.getCircles()[row][column].contains(e.getX(), e.getY())){
                            currentCircle[0] = row;
                            currentCircle[1] = column;
                            output.println("CLICK " + row + " " + column);
                        }
                    }
                }
            }
        });
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                output.println("SKIP");
            }
        });
        frame.getContentPane().add(label, BorderLayout.NORTH);
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.getContentPane().add(button, BorderLayout.SOUTH);
    }

    public TrylmaPanel getPanel(){
        return panel;
    }

    public void play() throws Exception {
        try{
            var response = input.nextLine();
            var id = Integer.parseInt(response);
            panel.setId(id);
            response = input.nextLine();
            var number = Integer.parseInt(response) + 1;
            response = input.nextLine();
            frame.setTitle("Game: Player " + number);
            var numOfPlayers = Integer.parseInt(response);
            panel.setPlayers(numOfPlayers);
            panel.repaint();

            while (input.hasNextLine()){
                response = input.nextLine();

                if (response.startsWith("VALID_MARK")){
                    panel.mark(currentCircle[0], currentCircle[1], true);
                }
                else if (response.startsWith("OTHER_MARK")){
                    String str = response.substring(11);
                    String[] arr = str.split("\\s+");
                    panel.mark(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), false);
                }
                else if (response.startsWith("UNMARK")){
                    panel.unmark();
                }
                else if (response.startsWith("VALID_MOVE")) {
                    panel.makeMove(currentCircle[0], currentCircle[1]);
                    panel.unmark();
                    label.setText("Good move, now wait!");
                }
                else if (response.startsWith("OTHER_MOVE")){
                    String str = response.substring(11);
                    String[] arr = str.split("\\s+");
                    panel.makeMove(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
                    label.setText("Opponent moved, your turn!");
                }
                else if (response.startsWith("POSSIBILITIES")){
                    String str = response.substring(14);
                    String[] arr = str.split("\\s+");
                    int temp1 = Integer.parseInt(arr[0]);
                    int temp2 = Integer.parseInt(arr[1]);
                    panel.mark(temp1, temp2);
                }
                else if (response.startsWith("REPAINT")){
                    panel.repaint();
                }
                else if (response.startsWith("MESSAGE")) {
                    label.setText(response.substring(8));
                }
                else if (response.startsWith("WIN")){
                    JOptionPane.showMessageDialog(frame, "You won!");
                    break;
                }
                else if (response.startsWith("DEFEAT")){
                    JOptionPane.showMessageDialog(frame, "You lost!");
                    break;
                }
                else if (response.startsWith("OTHER_PLAYER_LEFT")) {
                    JOptionPane.showMessageDialog(frame, "Other player left");
                    break;
                }
                else if (response.startsWith("SIM_CLICK")){     // do testów
                    String str = response.substring(10);
                    String[] arr = str.split("\\s+");
                    currentCircle[0] = Integer.parseInt(arr[0]);
                    currentCircle[1] = Integer.parseInt(arr[1]);
                }
            }
            output.println("QUIT");
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            socket.close();
            frame.dispose();
        }
    }

    public static void main(String[] args){
        try {
            Socket socket = new Socket("localhost", 59090);
            TrylmaClient client = new TrylmaClient(socket);
            client.frame.setSize(700,950);
            client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            client.frame.setVisible(true);
            client.play();

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
