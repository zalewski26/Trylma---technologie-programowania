package com.zalewskiwojtczak;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;
import static java.lang.Math.abs;

public class TrylmaGame extends GameBase {

    public TrylmaGame(int NUMOF){
        this.NUMOF = NUMOF;
        players = new TrylmaGame.playerHandler[NUMOF];
        Random rand = new Random();
        first = rand.nextInt(NUMOF);

        for (int row = 0; row < board.length; row++) {
            for (int column = 0; column < board[row].length; column++) {
                switch (NUMOF){
                    case 2:
                        if (board[row][column] == 2 || board[row][column] == 3 || board[row][column] == 4
                                || board[row][column] == 5)
                            board[row][column] = 0;
                        break;
                    case 3:
                        if (board[row][column] == 1 || board[row][column] == 4 || board[row][column] == 5)
                            board[row][column] = 0;
                        break;
                    case 4:
                        if (board[row][column] == 1 || board[row][column] == 6)
                            board[row][column] = 0;
                        break;
                }
            }
        }
    }

    class playerHandler implements Runnable {
        protected final int[] prev = new int[2];
        private final int[] prev_marked = new int[2];
        protected final int id;
        protected final int number;
        private final Socket socket;
        private Scanner input;
        private PrintWriter output;
        protected boolean marked = false;
        protected boolean jump = false;

        public playerHandler(Socket socket, int id){
            this.socket = socket;
            this.id = id;
            number = playerCounter;
            playerCounter++;
            players[number] = this;
        }

        @Override
        public void run(){
            try{
                setup();
                processCommands();
            } catch (Exception ex){
                ex.printStackTrace();
            } finally {
                for (playerHandler opponent: players) {
                    if (opponent != null && opponent.output != null)
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
            output.println(number);
            output.println(NUMOF);

            if (number == first){
                current = this;
                output.println("MESSAGE Waiting for opponent!");
            }
            else if (number == NUMOF - 1) {
                players[first].output.println("MESSAGE Your move!");
            }
        }

        private void processCommands(){
            while (input.hasNextLine()){
                var command = input.nextLine();
                if (command.startsWith("QUIT")){
                    return;
                }
                else if (command.startsWith("SKIP")){
                    if (marked) {
                        output.println("UNMARK");
                        output.println("REPAINT");
                    }
                    current = players[(number + 1) % NUMOF];
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
                            for (playerHandler opponent: players) {
                                if (opponent.equals(this))
                                    continue;
                                opponent.output.println("OTHER_MARK " + row + " " + column);
                                opponent.output.println("REPAINT");
                            }
                            prev_marked[0] = row;
                            prev_marked[1] = column;
                        }
                        else{
                            output.println("VALID_MOVE");
                            output.println("REPAINT");
                            for (playerHandler opponent: players) {
                                if (opponent.equals(this))
                                    continue;
                                opponent.output.println("OTHER_MOVE " + row + " " + column);
                                opponent.output.println("REPAINT");
                            }
                            while (jump){
                                int[][] temp = getAvailable(prev[0], prev[1]);
                                int tempCounter = 0;
                                for (int[] t: temp){
                                    if (abs(t[0] - prev[0]) != 2 && (abs(t[1] - prev[1]) != 2)){
                                        t[0] = 0;
                                        t[1] = 0;
                                    }
                                    else if (t[0] == prev_marked[0] && t[1] == prev_marked[1]){
                                        t[0] = 0;
                                        t[1] = 0;
                                    }
                                    if (t[0] != 0 && t[1] != 0)
                                        tempCounter++;
                                }
                                prev_marked[0] = prev[0];
                                prev_marked[1] = prev[1];
                                if (tempCounter == 0){
                                    current = players[(number + 1) % NUMOF];
                                    jump = false;
                                }
                                else{
                                    output.println("VALID_MARK");
                                    for (int[] t: temp){
                                        if (t[0] != 0 && t[1] != 0)
                                            output.println("POSSIBILITIES " + t[0] + " " + t[1]);
                                    }
                                    output.println("REPAINT");
                                    for (playerHandler opponent: players) {
                                        if (opponent.equals(this))
                                            continue;
                                        opponent.output.println("OTHER_MARK " + prev[0] + " " + prev[1]);
                                        opponent.output.println("REPAINT");
                                    }
                                    var cmd = input.nextLine();
                                    if (cmd.startsWith("SKIP")){
                                        output.println("UNMARK");
                                        output.println("REPAINT");
                                        current = players[(number + 1) % NUMOF];
                                        jump = false;
                                        break;
                                    }
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
                                        current = players[(number + 1) % NUMOF];
                                        jump= false;
                                        break;
                                    }
                                    output.println("VALID_MOVE");
                                    output.println("REPAINT");
                                    for (playerHandler opponent: players) {
                                        if (opponent.equals(this))
                                            continue;
                                        opponent.output.println("OTHER_MOVE " + row2 + " " + column2);
                                        opponent.output.println("REPAINT");
                                    }
                                }
                            }
                        }
                        if (checkWin(id)){
                            output.println("WIN");
                            for (playerHandler opponent: players) {
                                if (opponent.equals(this))
                                    continue;
                                opponent.output.println("DEFEAT");
                            }
                            return;
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