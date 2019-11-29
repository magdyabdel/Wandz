package be.magdyabdel.wandz;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ConnectionManager implements Serializable {

    private String ip;
    private int port;
    private static Socket socket;

    public ConnectionManager(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    public int connect(){
        try {
            socket = new Socket(ip, port);
        }catch(UnknownHostException uk){
            return 1; //Host could not be found
        }catch(IOException e){
            return 2; //IO error when creating the socket
        }

        return 0; //No errors, everything ok
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        ConnectionManager.socket = socket;
    }

    public int sendData(String data){
        if(socket!=null) {
            try {
                DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
                outToServer.writeBytes(data + "\n");
            } catch (IOException e) {
                return 1; //IO error when sending
            }

            return 0; //No errors, everything ok
        }else{
            return 2; //Socket is null
        }
    }

    public ArrayList<String> readAllData(){
        ArrayList<String> dataList = new ArrayList<>();
            try {
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while(inFromClient.ready()){
                    String nextLine = inFromClient.readLine();
                    dataList.add(nextLine);
                }

            }catch(IOException e){
                System.out.println("Error reading data from client.");
            }
        return dataList;
    }
}
