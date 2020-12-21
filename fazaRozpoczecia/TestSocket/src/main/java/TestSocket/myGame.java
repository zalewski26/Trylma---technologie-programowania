package TestSocket;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class myGame{
    private int[][] board = {
            {8,8,8,8,8,8,6,8,8,8,8,8,8},
            {8,8,8,8,8,6,6,8,8,8,8,8,8},
            {8,8,8,8,8,6,6,6,8,8,8,8,8},
            {8,8,8,8,6,6,6,6,8,8,8,8,8},
            {5,5,5,5,0,0,0,0,0,4,4,4,4},
            {5,5,5,0,0,0,0,0,0,4,4,4,8},
            {8,5,5,0,0,0,0,0,0,0,4,4,8},
            {8,5,0,0,0,0,0,0,0,0,4,8,8},
            {8,8,0,0,0,0,0,0,0,0,0,8,8},
            {8,3,0,0,0,0,0,0,0,0,2,8,8},
            {8,3,3,0,0,0,0,0,0,0,2,2,8},
            {3,3,3,0,0,0,0,0,0,2,2,2,8},
            {3,3,3,3,0,0,0,0,0,2,2,2,2},
            {8,8,8,8,1,1,1,1,8,8,8,8,8},
            {8,8,8,8,8,1,1,1,8,8,8,8,8},
            {8,8,8,8,8,1,1,8,8,8,8,8,8},
            {8,8,8,8,8,8,1,8,8,8,8,8,8},
    };
    private playerHandler current;

    public synchronized void move(int i, int j, playerHandler player){
        if (player != current){
            throw new IllegalStateException("Not your turn!");
        }
        else if (player.opponent == null){
            throw new IllegalStateException("You have no opponent yet!");
        }
        else if (board[j][i] != 0){
            throw new IllegalStateException("Place is not available! " + j + i + board[j][i]);
        }
        board[current.prev[0]][current.prev[1]] = 0;
        board[j][i] = current.id;
        current = current.opponent;
    }

    public synchronized void mark(int i, int j, playerHandler player){
        if (player != current){
            throw new IllegalStateException("Not your turn!");
        }
        else if (player.opponent == null){
            throw new IllegalStateException("You have no opponent yet!");
        }
        else if (board[j][i] != current.id){
            throw new IllegalStateException("Place is not available! " + j + i + board[j][i]);
        }
        current.prev[0] = j;
        current.prev[1] = i;
    }

    class playerHandler implements Runnable{
        private int[] prev = new int[2];
        private int id;
        private playerHandler opponent;
        private Socket socket;
        private Scanner input;
        private PrintWriter output;

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

        private void setup() throws IOException{
            input = new Scanner(socket.getInputStream());
            output = new PrintWriter(socket.getOutputStream(), true);
            output.println("WELCOME " + id);

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
                else if (command.startsWith("MARK")){
                    String str = command.substring(5);
                    String[] arr = str.split("\\s+");
                    processMarkCommand(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
                }
                else if (command.startsWith("MOVE")){
                    String str = command.substring(5);
                    String[] arr = str.split("\\s+");
                    processMoveCommand(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
                }
            }
        }

        private void processMarkCommand(int i, int j){
            try{
                mark(i, j, this);
                output.println("VALID_MARK");
            } catch (IllegalStateException ex){
                output.println("MESSAGE " + ex.getMessage());
            }
        }

        private void processMoveCommand(int i, int j){
            try{
                move(i, j, this);
                output.println("VALID_MOVE");
                opponent.output.println("OPPONENT_MOVED " + prev[1] + " " + prev[0] + " " + i + " " + j);
            } catch (IllegalStateException ex){
                output.println("MESSAGE " + ex.getMessage());
            }
        }
    }
}
