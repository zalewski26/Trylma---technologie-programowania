package com.zalewskiwojtczak;

import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;


import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PlayerHandlerTest {
    ServerSocket mockedServerSocket;
    Socket mockedSocket;
    BufferedReader mockedReader;
    ByteArrayOutputStream out;

    @Before
    public void setUpSocket() throws IOException{
        mockedServerSocket = mock(ServerSocket.class);
        mockedSocket = mock(Socket.class);
        mockedReader = mock(BufferedReader.class);
        InputStream in = new ByteArrayInputStream(" \nCLICK 13 4\nCLICK 12 4\nQUIT".getBytes());
        out = new ByteArrayOutputStream();

        when(mockedServerSocket.accept()).thenReturn(mockedSocket);
        when(mockedSocket.getInputStream()).thenReturn(in);
        when(mockedSocket.getOutputStream()).thenReturn(out);

    }

    @Test
    public void testHandler() throws IOException{
        TrylmaGame game = new TrylmaGame();
        TrylmaGame.playerHandler player = game.new playerHandler(mockedSocket, 1);
        TrylmaGame.playerHandler player2 = game.new playerHandler(mockedSocket, 6);
        assertEquals(game.board[13][4], 1);

        player.setup();
        player2.setup();
        player.run();
        assertEquals(game.board[12][4], 1);
        assertEquals(game.board[13][4], 0);
        //System.out.println(out.toString(Charset.defaultCharset()));

    }
}

