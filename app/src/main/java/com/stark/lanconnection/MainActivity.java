package com.stark.lanconnection;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {

    private TextView tvClientMsg,tvServerIP,tvServerPort;
    private final int SERVER_PORT = 8080; //Define the server port
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvClientMsg = (TextView) findViewById(R.id.textViewClientMessage);
        tvServerIP = (TextView) findViewById(R.id.textViewServerIP);
        tvServerPort = (TextView) findViewById(R.id.textViewServerPort);
        tvServerPort.setText(Integer.toString(SERVER_PORT));
        //Call method
        getDeviceIpAddress();
Toast.makeText(getApplicationContext(),"tes",Toast.LENGTH_LONG).show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    //Create a server socket object and bind it to a port
                    ServerSocket socServer = new ServerSocket(SERVER_PORT);
                    //Create server side client socket reference
                    Socket socClient = null;
                    //Infinite loop will listen for client requests to connect
                    while (true) {
                        //Accept the client connection and hand over communication to server side client socket
                        socClient = socServer.accept();
                        //For each client new instance of AsyncTask will be created
                        ServerAsyncTask serverAsyncTask = new ServerAsyncTask();
                        //Start the AsyncTask execution
                        //Accepted client socket object will pass as the parameter
                        serverAsyncTask.execute(new Socket[] {socClient});

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    /**
     * Get ip address of the device
     */
    public void getDeviceIpAddress() {
        try {

            //Loop through all the network interface devices
            for (Enumeration<NetworkInterface> enumeration = NetworkInterface
                    .getNetworkInterfaces(); enumeration.hasMoreElements();) {
                NetworkInterface networkInterface = enumeration.nextElement();
                //Loop through all the ip addresses of the network interface devices
                for (Enumeration<InetAddress> enumerationIpAddr = networkInterface.getInetAddresses(); enumerationIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumerationIpAddr.nextElement();
                    //Filter out loopback address and other irrelevant ip addresses
                    if (!inetAddress.isLoopbackAddress() && inetAddress.getAddress().length == 4) {
                        //Print the device ip address in to the text view
                        tvServerIP.setText(inetAddress.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            Log.e("ERROR:", e.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * AsyncTask which handles the communication with clients
     */
    class ServerAsyncTask extends AsyncTask<Socket, Void, String> {
        //Background task which serve for the client
        @Override
        protected String doInBackground(Socket... params) {
            String result = null;
            //Get the accepted socket object
            Socket mySocket = params[0];
            try {

                //Get the data input stream comming from the client
                InputStream is = mySocket.getInputStream();
                //Get the output stream to the client
                PrintWriter out = new PrintWriter(
                        mySocket.getOutputStream(), true);

                //Write data to the data output stream
                out.println("Hello from server");
                out.println("Stark");
                //Buffer the data input stream
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(is));
                //Read the contents of the data buffer
                result = br.readLine();
                //Close the client connection
                mySocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            //After finishing the execution of background task data will be write the text view
            Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
            tvClientMsg.setText(s);
        }
    }
    class AsyncTaskForDevice extends AsyncTask<String, Void, String> {
        //Background task which serve for the client


        @Override
        protected String doInBackground(String... strings) {

            checkHosts("192.168.2");
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            //After finishing the execution of background task data will be write the text view
            //tvClientMsg.setText(s);
        }
    }
    public void checkHosts(String subnet){
        int timeout=1000;
        for (int i=1;i<255;i++){
            String host=subnet + "." + i;
            try {
                if (InetAddress.getByName(host).isReachable(timeout)){
                    System.out.println(host + " is reachable");

                }
            } catch (IOException e) {
                e.printStackTrace();
                //System.out.println(e + "error");
            }
        }
    }
}
