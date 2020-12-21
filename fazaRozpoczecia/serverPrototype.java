import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class MyServer {

    public static void main(String[] args) throws Exception {
        try (var listener = new ServerSocket(59090)) {
            System.out.println("My server is running...");
            var pool = Executors.newFixedThreadPool(2);
            while (true) {
                myGame game = new myGame();
                pool.execute(game.new myPlayer(listener.accept(), 1));
                pool.execute(game.new myPlayer(listener.accept(), 2));
            }
        }
    }
}

class myGame{
    private myPlayer[] board = new myPlayer[221];
    private myPlayer current;

    public synchronized void move(int location, myPlayer player){
        if (player != current){
            throw new IllegalStateException("Not your turn!");
        }
        else if (player.opponent == null){
            throw new IllegalStateException("You have no opponent yet!");
        }
        else if (board[location] != null){
            throw new IllegalStateException("Place is already occupied!");
        }
        board[location] = current;
        current = current.opponent;
    }

    class myPlayer implements Runnable{
        private int id;
        private myPlayer opponent;
        private Socket socket;
        private Scanner input;
        private PrintWriter output;

        public myPlayer(Socket socket, int id){
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
                else if (command.startsWith("MOVE")){
                    processMoveCommand(Integer.parseInt(command.substring(5)));
                }
            }
        }

        private void processMoveCommand(int location){
            try{
                move(location, this);
                output.println("VALID_MOVE");
                opponent.output.println("OPPONENT_MOVED " + location);
            } catch (IllegalStateException ex){
                output.println("MESSAGE " + ex.getMessage());
            }
        }
    }
}
