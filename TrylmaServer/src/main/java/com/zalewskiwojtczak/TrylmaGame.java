package com.zalewskiwojtczak;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;
import static java.lang.Math.abs;
/** Klasa zarządzająca przebiegiem gry, kolejnością wykonywanych ruchów oraz
 * ich przekazywaniem między graczami
 */
public class TrylmaGame extends GameBase {
	/** Konstruktor klasy TrylmaGame
	 * @param NUMOF liczba graczy uczestniczaca w rozgrywce
	 */
    public TrylmaGame(int NUMOF){
        this.NUMOF = NUMOF;
        players = new TrylmaGame.playerHandler[NUMOF];
        Random rand = new Random();
        first = rand.nextInt(NUMOF);
        //ustawianie planszy dla graczy
        startingBoard = new int[board.length][board[0].length];
        for (int row = 0; row < board.length; row++){
            for (int column = 0; column < board[row].length; column++){
                startingBoard[row][column] = board[row][column];
            }
        }

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
    /**Klasa wewnętrzna zajmująca się interakcją między graczami (dokładniej między jednym graczem
	 następnie serwerem, który przekazuje informacje do reszty graczy) */
    class playerHandler implements Runnable {
    	/** Zmienna przechowujaca informacje o poprzednio ruszonym pionku */
        protected final int[] prev = new int[2];
        /** Zmienna przechowujaca informacje o poprzednio oznaczonym pionku */
        private final int[] prev_marked = new int[2];
        /** Zmienna przechowująca id gracza */
        protected final int id;
        /** Zmienna przechowujaca informacje o numerze gracza z kolei co nie jest tożsame z id */
        protected final int number;
        /** Zmienna typu Socket tworząca gniazdo */
        private final Socket socket;
        /** Zmienna typu Scanner zajmująca się wczytywanie informacji od użytkownika */
        private Scanner input;
        /** Zmienna typu PrintWriter zajmująca się wyświetlaniem informacji */
        private PrintWriter output;
        /** Czy oznaczono piona */
        protected boolean marked = false;
        /** Czy wykonano skok */
        protected boolean jump = false;
        /** Czy wygrano */
        protected boolean won = false;
        /** Konstrukt klasy playerHandler
         * @param socket gniazdo, do ktorego podłącza się gracz
         * @param id id gracza
         * */
        public playerHandler(Socket socket, int id){
            this.socket = socket;
            this.id = id;
            number = playerCounter;
            playerCounter++;
            players[number] = this;
        }
        /**Funkcja interfejsu Runnable odpowiedzialna za ruchy gracza */
        @Override
        public void run(){
            try{
                setup();
                processCommands();
            } catch (Exception ex){
                ex.printStackTrace();
            } finally {
            	//Jeśli gracz wyszedł to zakończ grę
                for (playerHandler opponent: players) {
                    if (opponent != null && opponent.output != null)
                        opponent.output.println("OTHER_PLAYER_LEFT");
                }
                try{
                    socket.close();
                } catch (IOException e) {}
            }
        }
        /** Funkcja ustawiająca graczy na pozycje i rozpoczynająca grę*/
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
            if (number == NUMOF - 1) {
                players[first].output.println("MESSAGE Your move!");
            }
        }
        /** Funkcja przekazująca turę dla nastepnego gracza */
        protected playerHandler nextPlayer(){
            playerHandler result = players[(number + 1) % NUMOF];
            while (result.won){
                result = players[(result.number + 1) % NUMOF];
            }
            result.output.println("MESSAGE Your turn!");
            return result;
        }
        /** Funkcja odpowiedzialna za przetwarzanie komend*/
        private void processCommands(){
        	//Serwer po otrzymaniu informacji od gracza aktualizuje wydarzenia i przesyła odpowiednie
        	//informacje do przeciwników gracza, który aktualnie wykonuje ruch
            while (input.hasNextLine()){
                var command = input.nextLine();
                //Opuszczenie gry przez gracza
                if (command.startsWith("QUIT")){
                    return;
                }
                //Wysyłanie informacji o zwycięstwie
                else if (won){
                    output.println("WIN");
                }
                //Wysyłanie informacji o pominięciu tury
                else if (command.startsWith("SKIP")){
                    if (!current.equals(this))
                        continue;
                    output.println("MESSAGE You folded!");
                    if (marked) {
                        output.println("UNMARK");
                        output.println("REPAINT");
                    }
                    current = nextPlayer();
                }
                else if (command.startsWith("CLICK")){
                	//implementacja mechanizmu przechwytywania informacji od klienta
                	//dotyczących ruchy w aktualnej turze
                    String str = command.substring(6);
                    String[] arr = str.split("\\s+");
                    int row = Integer.parseInt(arr[0]);
                    int column = Integer.parseInt(arr[1]);
                    try{
                        action(row, column, this);
                        if (!marked){
                            output.println("VALID_MARK");
                            for (int[] a: available){
                                if (a[0] != 0 || a[1] != 0)
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
                                    if (t[0] != 0 || t[1] != 0)
                                        tempCounter++;
                                }
                                prev_marked[0] = prev[0];
                                prev_marked[1] = prev[1];
                                if (tempCounter == 0){
                                    current = nextPlayer();
                                    jump = false;
                                }
                                else{
                                    output.println("VALID_MARK");
                                    for (int[] t: temp){
                                        if (t[0] != 0 || t[1] != 0)
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
                                        current = nextPlayer();
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
                                        current = nextPlayer();
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
                        //jesli warunki spelnione to gracz wygrał
                        if (!won && checkWin(id)){
                            output.println("WIN");
                            won = true;
                            for (playerHandler opponent: players) {
                                if (opponent.equals(this) || opponent.won)
                                    continue;
                                opponent.output.println("DEFEAT");
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