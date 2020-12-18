package com.zalewskiwojtczak;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TrylmaGame {
    public final int[][] board = { {8,8,8,8,8,8,6,8,8,8,8,8,8}, {8,8,8,8,8,6,6,8,8,8,8,8,8}, {8,8,8,8,8,6,6,6,8,8,8,8,8},
            {8,8,8,8,6,6,6,6,8,8,8,8,8}, {5,5,5,5,0,0,0,0,0,4,4,4,4}, {5,5,5,0,0,0,0,0,0,4,4,4,8}, {8,5,5,0,0,0,0,0,0,0,4,4,8},
            {8,5,0,0,0,0,0,0,0,0,4,8,8}, {8,8,0,0,0,0,0,0,0,0,0,8,8}, {8,3,0,0,0,0,0,0,0,0,2,8,8}, {8,3,3,0,0,0,0,0,0,0,2,2,8},
            {3,3,3,0,0,0,0,0,0,2,2,2,8}, {3,3,3,3,0,0,0,0,0,2,2,2,2}, {8,8,8,8,1,1,1,1,8,8,8,8,8}, {8,8,8,8,8,1,1,1,8,8,8,8,8},
            {8,8,8,8,8,1,1,8,8,8,8,8,8}, {8,8,8,8,8,8,1,8,8,8,8,8,8},
    };
    private playerHandler current;
    private int[][] available;

    public synchronized void action(int row, int column, playerHandler player){
        if (player != current){
            throw new IllegalStateException("Not your turn!");
        }
        else if (player.opponent == null){
            throw new IllegalStateException("You have no opponent yet!");
        }

        if (!current.marked){
            if (board[row][column] != current.id)
                throw new IllegalStateException("This is not your pawn!");
            current.prev[0] = row;
            current.prev[1] = column;
            available = getAvailable(current.prev[0], current.prev[1]);
        }
        else{
            if (board[row][column] != 0) {
                throw new IllegalStateException("Place is already occupied!");
            }

            for (int[] av: available){
                if (av[0] == row && av[1] == column) {
                    board[current.prev[0]][current.prev[1]] = 0;
                    board[row][column] = current.id;
                    current = current.opponent;
                    return;
                }
            }
            throw new IllegalStateException("Place is not available for move!");
        }
    }

    public int[][] getAvailable(int row, int column){
        int[][] result = { {row - 1, column}, {row - 1, column + 1}, {row, column - 1}, {row, column + 1},
                {row + 1, column}, {row + 1, column + 1}};
        if (row % 2 == 0) {
            result[0][1] = column - 1;
            result[1][1] = column;
            result[4][1] = column - 1;
            result[5][1] = column;
        }
        for (int i = 0; i < result.length; i++){
            if (result[i][0] < 0 || result[i][0] > 16 || result[i][1] < 0 || result[i][1] > 12)
                continue;
            if (board[result[i][0]][result[i][1]] != 0){
                int diffrow = result[i][0] - row;
                int diffcol = result[i][1] - column;
                if (diffrow == 0 && diffcol == -1)
                    result[i][1] -= 1;
                else if (diffrow == 0 && diffcol == 1)
                    result[i][1] += 1;
                else if (diffrow == -1 && diffcol == -1)
                    result[i][0] -= 1;
                else if (diffrow == 1 && diffcol == -1)
                    result[i][0] += 1;
                else if (diffrow == -1 && diffcol == 1)
                    result[i][0] -= 1;
                else if (diffrow == 1 && diffcol == 1)
                    result[i][0] += 1;
                else if (diffrow == -1 && diffcol == 0){
                    if (row % 2 == 1){
                        result[i][0] -= 1;
                        result[i][1] -= 1;
                    }
                    else{
                        result[i][0] -= 1;
                        result[i][1] += 1;
                    }
                }
                else if (diffrow == 1 && diffcol == 0){
                    if (row % 2 == 1){
                        result[i][0] += 1;
                        result[i][1] -= 1;
                    }
                    else{
                        result[i][0] += 1;
                        result[i][1] += 1;
                    }
                }
            }
        }
        for (int[] a: result){
            if (a[0] < 0 || a[0] > 16 || a[1] < 0 || a[1] > 13){
                a[0] = 0;
                a[1] = 0;
            }
            else if (board[a[0]][a[1]] != 0){
                a[0] = 0;
                a[1] = 0;
            }
        }
        return result;
    }

    class playerHandler implements Runnable {
        private final int[] prev = new int[2];
        private final int id;
        private playerHandler opponent;
        private final Socket socket;
        private Scanner input;
        private PrintWriter output;
        private boolean marked = false;

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