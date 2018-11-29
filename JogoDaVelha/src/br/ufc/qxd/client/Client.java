package br.ufc.qxd.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;


public class Client {
	private Socket socket;
	private OutputStream output;
	private Writer writer; 
	private BufferedWriter buffer;
	private static final Client instance = new Client();
	public String [][] board = new String[3][3];
	private static Scanner input;
	
	private Client() {}

	public static void main(String[] args) {
		input = new Scanner(System.in);
		Client.getInstance().connect();
		Client.getInstance().to_listen();	
	}
	
	public void connect() {
		try {
			this.socket = new Socket("127.0.0.1", 3000);
			this.output = this.socket.getOutputStream();
			this.writer = new OutputStreamWriter(this.output);
			this.buffer = new BufferedWriter(this.writer);
			this.buffer.flush();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendMessage(JSONObject json) {
		try {
			this.buffer.write(json.toString());
			this.buffer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void to_listen() {	
	    try {		    
		    InputStream input = this.socket.getInputStream();
		    InputStreamReader input_reader = new InputStreamReader(input);
		    BufferedReader buffer = new BufferedReader(input_reader);
		    JSONObject json;
		    while(true) {
		        if(buffer.ready()){		        	
		        	json = new JSONObject(buffer.readLine());
		        	if(json.has("flag")) {
		        		print(convertJsonToArrayString(json));
		        		if(checkWinner(json.get("flag").toString())) { break; }
		        	}
		        	else {
			        	print(convertJsonToArrayString(json));	
			        	getData();
			        	sendMessage(convertBoardToJson());
		        	}
		        }
		    }		    
	    } catch(IOException e) {
		    e.printStackTrace();
	    } catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static Client getInstance() {
		return Client.instance;
	}
	
	public void makeMatrix() {
		for(int i = 0; i < board.length; i++) {
			for(int j = 0; j < board.length; j++) {
				board[i][j] = " ";
			}
		}
	}
	
	public void getData() {
		int row, column;
		System.out.println("Faça sua jogada: ");
		System.out.println("Digite a linha: ");
		row = input.nextInt();
		System.out.println("Digite a coluna: ");
		column = input.nextInt();
		
		while(!verifyRowColumn(row, column)) {
			System.out.println("Posição inválida, digite novamente!");
			System.out.println("Digite a linha: ");
			row = input.nextInt();
			System.out.println("Digite a coluna: ");
			column = input.nextInt();
		}
		board[row][column] = "x";
	}
	
	public JSONObject convertBoardToJson() {
		JSONObject json = new JSONObject();
		List<String> list = new ArrayList<>();
		for(int i = 0; i < board.length; i++) {
			for(int j = 0; j < board.length; j++) {
				list.add(board[i][j]);
			}		
			try {
				json.put(String.valueOf(i + 1), list.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			list.clear();
		}
		return json;
	}
	
	public void print(List<String[]> list) {
		makeMatrix();		
		for(int i = 0; i < board.length; i++) {
			for(int j = 0; j < board.length; j++) {
				if(list.get(i)[j].equals("\"\""))
					board[i][j] = board[i][j];
				else
					board[i][j] = list.get(i)[j].replace("\"", "").replace("\"", "");
			}
		}
		
		String mat = "+---+---+---+\n";
		for(int i = 0; i < board.length; i++) {
			mat += "|";
			for(int j = 0; j < board.length; j++) {
				mat += " " + board[i][j] + " |";
			}
			mat += "\n";
			mat += "+---+---+---+\n";
		}
		System.out.println("Servidor jogou: ");
		System.out.print(mat);
	}
	
	public boolean checkWinner(String flag) {
		if(flag.equals("x")) {
			System.out.println("Jogador ganhou !!!");
			return true;
		}
		else if(flag.equals("e")) {
			System.out.println("Partida empatada.");
			return true;
		}
		else if(flag.equals("o")) {
			System.out.println("Servidor ganhou !");
			return true;
		}
		else {
			return false;
		}
	}
	
	private List<String[]> convertJsonToArrayString(JSONObject json){
		try {
			String lineOne = json.getString("1");
			String lineTwo = json.getString("2");
	    	String lineTree = json.getString("3");
	    	
	    	List<String[]> list = new ArrayList<String[]>();	
	    	String [] aux1 = lineOne.replace("[", "").replace("]", "").split(",");
	    	String [] aux2 = lineTwo.replace("[", "").replace("]", "").split(",");
	    	String [] aux3 = lineTree.replace("[", "").replace("]", "").split(",");
	    	list.add(aux1);
	    	list.add(aux2);
	    	list.add(aux3);
	    	return list;
		} catch (JSONException e) {
			e.printStackTrace();
		}    	
    	return null;
	}
	
	private boolean verifyRowColumn(int row, int column) {
		if(column < 0 || column > 2)
			return false;
		else if(row < 0 || row > 2)
			return false;
		else if(!board[row][column].equals(" "))
			return false;
		else
			return true;
	}
}
