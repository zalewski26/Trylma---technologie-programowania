import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class MyClient{
    private JFrame frame = new JFrame("Game");
    private JLabel label = new JLabel("...");
    private Place[] board = new Place[100];
    private Place currentPlace;
    private Socket socket;
    private Scanner input;
    private PrintWriter output;

    public MyClient() throws Exception {
        socket = new Socket("localhost", 59090);
        input = new Scanner(socket.getInputStream());
        output = new PrintWriter(socket.getOutputStream(), true);

        label.setBackground(Color.LIGHT_GRAY);
        frame.getContentPane().add(label, BorderLayout.SOUTH);
        var panel = new JPanel();
        panel.setBackground(Color.BLACK);
        panel.setLayout(new GridLayout(10, 10, 2, 2));

        for (var i = 0; i < board.length; i++){
            final int j = i;
            board[i] = new Place();
            board[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    currentPlace = board[j];
                    output.println("MOVE " + j);
                }
            });
            panel.add(board[i]);
        }
        frame.getContentPane().add(panel, BorderLayout.CENTER);
    }

    public void play() throws Exception{
        try{
            var response = input.nextLine();
            var id = Integer.parseInt(String.valueOf(response.charAt(8)));
            var opponentId = id == 1 ? 2 : 1;
            frame.setTitle("Game: Player " + id);
            while (input.hasNextLine()){
                response = input.nextLine();

                if (response.startsWith("VALID_MOVE")) {
                    label.setText("Valid move, please wait");
                    currentPlace.changeColor(id);
                    currentPlace.repaint();
                }
                else if (response.startsWith("OPPONENT_MOVED")) {
                    var opp = Integer.parseInt(response.substring(15));
                    board[opp].changeColor(opponentId);
                    board[opp].repaint();
                    label.setText("Opponent moved, your turn");
                }
                else if (response.startsWith("MESSAGE")) {
                    label.setText(response.substring(8));
                }
                else if (response.startsWith("OTHER_PLAYER_LEFT")) {
                    JOptionPane.showMessageDialog(frame, "Other player left");
                    break;
                }
            }
            output.println("QUIT");
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            socket.close();
            frame.dispose();
        }
    }

    static class Place extends JPanel{
        private Color currentColor;

         public Place(){
             currentColor = Color.WHITE;
             setBackground(currentColor);
             setLayout(new GridBagLayout());
         }

         public void changeColor(int id){
             if (currentColor == Color.WHITE){
                 switch (id){
                     case 1:
                         currentColor = Color.RED;
                         break;
                     case 2:
                         currentColor = Color.BLUE;
                         break;
                 }
             }
             else {
                 currentColor = Color.BLACK;
             }
             setBackground(currentColor);
         }
    }

    public static void main(String[] args) throws Exception {
        MyClient client = new MyClient();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setSize(800, 800);
        client.frame.setVisible(true);
        client.frame.setResizable(false);
        client.play();
    }
}