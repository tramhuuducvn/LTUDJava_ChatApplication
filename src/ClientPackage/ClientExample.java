package ClientPackage;

import java.awt.EventQueue;
import java.io.*;
import java.net.*;
import java.util.*;

public class ClientExample extends Thread{
	private InetAddress ip;
	private String serverName = "localhost";
	private Socket client;
	private int port = 7800;
	
	DataInputStream in;
	DataOutputStream out;
	
	MessageListen messagelisten;		
	boolean havingMess;
	
	public ClientExample() {
		try {
			ip = InetAddress.getLocalHost();
			serverName = ip.getHostAddress();
			client = new Socket(serverName, port);
			
			out = new DataOutputStream(client.getOutputStream());
			in = new DataInputStream(client.getInputStream());
			
//			messagelisten = new MessageListen(in, this);			
			havingMess = false;
			Thread t = new Thread(messagelisten);
			t.start();
		}
		catch(UnknownHostException e) {
			e.printStackTrace();
		}
		catch(IOException e1) {
			e1.printStackTrace();
		}		
	}
	
	private Scanner sc = new Scanner(System.in);
	private String mess;
	private String messRecei;
	
	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(100);
				if(havingMess == true) {
					System.out.println(messRecei);
					havingMess = false;
				}
				
				System.out.print("Enter a message: ");
				mess = sc.nextLine();
				out.writeUTF(mess);
				
				if(mess.equals("file")) {	
					String fname = "/home/adrew/TempotaryFile/Photos.zip";
					sendFile(fname);
				}			
				
			}
			catch(IOException e) {
				e.printStackTrace();
			}
			catch(InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public void sendFile(String fname) {
		try {
			FileInputStream file = new FileInputStream(fname);
			byte[] buffer = new byte[2048];
			
			while(file.read(buffer) > 0) {
				out.writeBoolean(true);
				out.write(buffer);
			}
			out.writeBoolean(false);
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch(IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void setMessRecei(String messRecei, boolean b) {
		this.havingMess = b;
		this.messRecei = messRecei;
	}
	
	
	public static void main(String[] args) {	
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				ClientExample t = new ClientExample();
				t.run();
			}
		});
		
	}
}

