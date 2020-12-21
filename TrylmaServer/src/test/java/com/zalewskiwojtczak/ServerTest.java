package com.zalewskiwojtczak;

import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class ServerTest {
    @Test
    public void testServerSocket() throws IOException {
        boolean cond = false;
        ServerSocket socket;

        socket = new ServerSocket(59090);
        try {
            TrylmaServer server = new TrylmaServer(2);
        } catch (IOException ex){
            cond = true;
        }
        socket.close();
        assertTrue(cond);

    }
}
