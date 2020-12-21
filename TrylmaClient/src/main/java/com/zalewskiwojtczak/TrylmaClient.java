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
/** Klasa klienta aplikacji Trylma
 * 
 */
public class TrylmaClient {
	/** Obiekt klasy JFrame odpowiedzialny za utworzenie ramki aplikacji okinkowej */
    private final JFrame frame = new JFrame("Trylma");
    /** Komponent odpowiedzialny za ustawianie tekstu */
    private final JLabel label = new JLabel("...");
    /** Przycisk odpowiedzialny za pomijanie ruchu */
    private final JButton button = new JButton("skip/finish move");
    /** Zmienna typu TrylmaPanel odpowiedzialna za przedstawianie planszy w trakcie rozgrywki */
    private final TrylmaPanel panel;
    /** Zmienna przechowująca współrzędnie pionka, na którym w danym momencie są wykonywane operacje */
    private final int[] currentCircle = new int[2];
    /**Zmienna typu Socket tworząca gniazdo */
    private final Socket socket;
    /** Zmienna typu Scanner zajmująca się wczytywanie informacji od użytkownika */
    private final Scanner input;
    /**  Zmienna typu PrintWriter zajmująca się wyświetlaniem informacji */
    private final PrintWriter output;
    /** Konstruktor klasy TrylmaClient
     * @param Socket socket gniazdo, do którego podłącza się klient w celu rozegrania rozgrywki
     */
    public TrylmaClient(Socket socket) throws Exception {
    	//inicjalizacja zmiennych
        this.socket = socket;
        input = new Scanner(socket.getInputStream());
        output = new PrintWriter(socket.getOutputStream(), true);

        panel = new TrylmaPanel();
        //MouseListener, którego zadaniem jest nasłuchiwanie, czy któryś z pionków został kliknięty.
        //Jeśli taka operacja zaszła, informuje o tym serwer
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
      //Osobny Listener nasłuchujący, czy użytkownik chce pominąc turę
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                output.println("SKIP");
            }
        });
      //dodanie komponentów do obiektu typu JFrame
        frame.getContentPane().add(label, BorderLayout.NORTH);
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.getContentPane().add(button, BorderLayout.SOUTH);
    }
    /** Funkcja zwracająca panel planszę na której odbywa się rozgrywka */
    public TrylmaPanel getPanel(){
        return panel;
    }
    /** Funkcja odpowiedzialna za rozgrywkę użytkownika */
    public void play() throws Exception {
        try{
        	//Cała filozofia rozgrywki polega na analizowaniu informacji, które
        	//gracz otrzymuje od serwera w formie napisu (zapisywane są do zmiennej response).
        	// W zależności od odpowiedzi serwera uruchamiane są różne funkcje z klasy TrylmaBoard,
        	// która zarządza planszą
            var response = input.nextLine();
            var id = Integer.parseInt(response);
            //utożsamienie planszy z graczem- każdy gracz musi mieć swoją planszę na której może 
            //wykonywac operacje
            panel.setId(id);
            response = input.nextLine();
            var number = Integer.parseInt(response) + 1;
            response = input.nextLine();
            frame.setTitle("Game: Player " + number);
            var numOfPlayers = Integer.parseInt(response);
            //ustawienie wszystkich graczy na pozycjach
            panel.setPlayers(numOfPlayers);
            panel.repaint();
            //Główna pętla realizująca rozgrywkę
            while (input.hasNextLine()){
                response = input.nextLine();
                //Jeśli odpowiedź serwera rozpoczyna się od komunikatu VALID_MARK
                // to można oznaczyć dane pole jako możliwe do wykonania ruchu
                if (response.startsWith("VALID_MARK")){
                    panel.mark(currentCircle[0], currentCircle[1], true);
                }
                else if (response.startsWith("OTHER_MARK")){
                    String str = response.substring(11);
                    String[] arr = str.split("\\s+");
                    panel.mark(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), false);
                }
                //Odznaczenie piona
                else if (response.startsWith("UNMARK")){
                    panel.unmark();
                }
                //Wykonanie ruchu
                else if (response.startsWith("VALID_MOVE")) {
                    panel.makeMove(currentCircle[0], currentCircle[1]);
                    panel.unmark();
                    label.setText("Good move, now wait!");
                }
                //Gdy ruch wykona przeciwnik, aktualizacja planszy przekazanie tury
                else if (response.startsWith("OTHER_MOVE")){
                    String str = response.substring(11);
                    String[] arr = str.split("\\s+");
                    panel.makeMove(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
                }
                //Zaznaczenie możliwych do wykonania ruchów
                else if (response.startsWith("POSSIBILITIES")){
                    String str = response.substring(14);
                    String[] arr = str.split("\\s+");
                    int temp1 = Integer.parseInt(arr[0]);
                    int temp2 = Integer.parseInt(arr[1]);
                    panel.mark(temp1, temp2);
                }
                // zaktualizowanie planszy o informacje otrzymane od serwera
                else if (response.startsWith("REPAINT")){
                    panel.repaint();
                }
                //wysyłanie wiadomości
                else if (response.startsWith("MESSAGE")) {
                    label.setText(response.substring(8));
                }
                //informacja o zwycięstwie
                else if (response.startsWith("WIN")){
                    JOptionPane.showMessageDialog(frame, "You won!");
                }
                //pozostali otrzymują informacje o możliwości kontynuowania gry mimo porażki
                else if (response.startsWith("DEFEAT")){
                    int dialogButton = JOptionPane.YES_NO_OPTION;
                    int dialogResult = JOptionPane.showConfirmDialog(frame, "Do you want to continue the game?", "You lost!", dialogButton);
                    if(dialogResult != 0)
                        break;
                }
                //Przeciwnik opuścił planszę
                else if (response.startsWith("OTHER_PLAYER_LEFT")) {
                    JOptionPane.showMessageDialog(frame, "Other player left");
                    break;
                }
                // do testów
                else if (response.startsWith("SIM_CLICK")){ 
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
    /** Funkcja main klasy TrylmaClient
     * @param args 
     */
    public static void main(String[] args){
        try {
        	//inicjalizacja gniazda dla portu 59090
            Socket socket = new Socket("localhost", 59090);
            //utworzenie nowego klienta
            TrylmaClient client = new TrylmaClient(socket);
            //dodanie odpowiednich atrybutów dla okienka klienta
            client.frame.setSize(700,950);
            client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            client.frame.setVisible(true);
            //funkcja dodająca użytkownica gry oraz używana do porozumiewania się między graczami
            //w celu rozegrania partii
            client.play();

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
