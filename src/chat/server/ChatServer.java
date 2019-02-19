package chat.server;
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
	private boolean start = false;
	private List<Client> Clients = new ArrayList<Client>();
	private ServerSocket ss = null;
	
	public static void main(String[] args) {
		new ChatServer().start();

	}

	public void start() {
		try {
			ss = new ServerSocket(8888);
			start = true;
		}catch(BindException e) {
			System.out.println("服务器已经启动，请关掉重试！");
			System.exit(0);
			e.printStackTrace();
		}catch(IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			while(start) {
				Socket s = ss.accept();
				Client c = new Client(s);
				
				System.out.println("client accepted");
				
				Clients.add(c);
				new Thread(c).start();
			}
		}catch(IOException e) {
			e.printStackTrace();
		}finally {
			try {
				ss.close();
				System.out.println("服务器异常关闭！");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	class Client implements Runnable{
		private Socket socket = null;
		private DataOutputStream dos = null;
		private DataInputStream dis = null;
		private boolean bConnected = false;
		
		Client(Socket s){
			socket = s;
			try {
				dis = new DataInputStream(socket.getInputStream());
				dos = new DataOutputStream(socket.getOutputStream());
				bConnected = true;
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		public void send(String s) {
			try {
				dos.writeUTF(s);
				//dos.writeUTF("\n");
			}catch(SocketException e) {
				//System.out.println("XX 已经下线！");
				Clients.remove(this);
			}
			catch (IOException e) {
				e.printStackTrace();
			} 
		}
		
		public void run() {
			try {
				while(bConnected) {
					
					String s = dis.readUTF();
					System.out.println(s);
					for(int i = 0;i < Clients.size();i++) {
						Client c = Clients.get(i);
						c.send(s);
					}
				}
			}catch(EOFException e) {
				System.out.println("xx 已经下线！");
			}
			catch(IOException e) {
				e.printStackTrace();
			}
			
		}
	}

}
