package com.zalewskiwojtczak;

import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.io.*;
import java.net.Socket;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClientTest {
    Socket mockedSocket;
    ByteArrayOutputStream out;
    @Before
    public void setUpSocket() throws IOException{
        mockedSocket = mock(Socket.class);
        InputStream in = new ByteArrayInputStream("1\nSIM_CLICK 13 4\nVALID_MARK \nSIM_CLICK 12 4\nVALID_MOVE".getBytes());
        out = new ByteArrayOutputStream();

        when(mockedSocket.getInputStream()).thenReturn(in);
        when(mockedSocket.getOutputStream()).thenReturn(out);
    }

    @Test
    public void testClient() throws Exception{
        TrylmaClient client = new TrylmaClient(mockedSocket);
        assertEquals(client.getPanel().getCircles()[13][4].getColor(), Color.GREEN);
        assertEquals(client.getPanel().getCircles()[12][4].getColor(), Color.LIGHT_GRAY);
        client.play();
        assertEquals(client.getPanel().getCircles()[13][4].getColor(), Color.LIGHT_GRAY);
        assertEquals(client.getPanel().getCircles()[12][4].getColor(), Color.GREEN);
    }
}
