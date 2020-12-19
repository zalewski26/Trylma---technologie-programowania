package com.zalewskiwojtczak;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import static java.lang.Math.abs;

public class TrylmaGame extends GameBase {

    class playerHandler implements Runnable {
        protected final int[] prev = new int[2];
        protected final int id;
        protected playerHandler opponent;
        private final Socket socket;
        private Scanner input;
        private PrintWriter output;
        protected boolean marked = false;

        public playerHandler(Socket socket, int id){
            this.socket = socket;
            this.id = id;
        }

        @Override
        public void run(){
            try{
                setup();
                processCommands();
            } catch (Exception ex){
                ex.printStackTrace();
            } finally {
                if (opponent != null && opponent.output != null) {
                    opponent.output.println("OTHER_PLAYER_LEFT");
                }
                try{
                    socket.close();
                } catch (IOException e) {}
            }
        }

        public void setup() throws IOException{
            input = new Scanner(socket.getInputStream());
            output = new PrintWriter(socket.getOutputStream(), true);
            output.println(id);

            if (id == 1){
                current = this;
                output.println("MESSAGE Waiting for opponent!");
            }
            else {
                opponent = current;
                opponent.opponent = this;
                opponent.output.println("MESSAGE Your move!");
            }
        }

        private void processCommands(){
            while (input.hasNextLine()){
                var command = input.nextLine();
                if (command.startsWith("QUIT")){
                    return;
                }
                else if (command.startsWith("CLICK")){
                    String str = command.substring(6);
                    String[] arr = str.split("\\s+");
                    int row = Integer.parseInt(arr[0]);
                    int column = Integer.parseInt(arr[1]);
                    try{
                        action(row, column, this);
                        if (!marked){
                            output.println("VALID_MARK");
                            for (int[] a: available){
                                if (a[0] != 0 && a[1] != 0)
                                    output.println("POSSIBILITIES " + a[0] + " " + a[1]);
                            }
                            output.println("REPAINT");
                            opponent.output.println("OTHER_MARK " + row + " " + column);
                            opponent.output.println("REPAINT");
                        }
                        else{
                            output.println("VALID_MOVE");
                            output.println("REPAINT");
                            opponent.output.println("OTHER_MOVE " + row + " " + column);
                            opponent.output.println("REPAINT");
                            while (current.equals(this)){
                                int[][] temp = getAvailable(prev[0], prev[1]);
                                int tempCounter = 0;
                                for (int[] t: temp){
                                    if (abs(t[0] - prev[0]) != 2 && (abs(t[1] - prev[1]) != 2)){
                                        t[0] = 0;
                                        t[1] = 0;
                                    }
                                    if (t[0] != 0 && t[1] != 0)
                                        tempCounter++;
                                }
                                if (tempCounter == 0){
                                    current = current.opponent;
                                }
                                else{
                                    output.println("VALID_MARK");
                                    for (int[] t: temp){
                                        if (t[0] != 0 && t[1] != 0)
                                            output.println("POSSIBILITIES " + t[0] + " " + t[1]);
                                    }
                                    output.println("REPAINT");
                                    opponent.output.println("OTHER_MARK " + prev[0] + " " + prev[1]);
                                    opponent.output.println("REPAINT");
                                    var cmd = input.nextLine();
                                    String str2 = cmd.substring(6);
                                    String[] arr2 = str2.split("\\s+");
                                    int row2 = Integer.parseInt(arr2[0]);
                                    int column2 = Integer.parseInt(arr2[1]);
                                    available = temp;
                                    try{
                                        action(row2, column2, this);
                                    }
                                    catch (IllegalStateException ex) {
                                        output.println("UNMARK " + prev[0] + " " + prev[1]);
                                        output.println("REPAINT");
                                        current = current.opponent;
                                        break;
                                    }
                                    output.println("VALID_MOVE");
                                    output.println("REPAINT");
                                    opponent.output.println("OTHER_MOVE " + row2 + " " + column2);
                                    opponent.output.println("REPAINT");
                                }
                            }
                        }
                        marked = !marked;
                    } catch (IllegalStateException ex){
                        if (marked){
                            output.println("UNMARK " + prev[0] + " " + prev[1]);
                            output.println("REPAINT");
                            marked = false;
                        }
                        output.println("MESSAGE " + ex.getMessage());
                    }
                }
            }
        }
    }
}