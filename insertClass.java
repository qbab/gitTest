package dagbok;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date.*;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Stack;

public class Insert extends DBConn {
	
	Statement stmt=null;
	private String startTid;
	private String sluttTid;
	private int varighet;
	
	
	public void init(){
		System.out.println("Du kan legge inn data i din dagbok her");
	}
	
	
	public void leggInnØvelse(Connection conn,Scanner sc) {
		System.out.println("Navn pØ Øvelse: ");
		String navn=sc.nextLine();
		System.out.println("Beskrivelse av Øvelse: ");
		String beskrivelse=sc.nextLine();
		System.out.println("Øvelsens kategori(langrenn,lØping,svØmming,e.l): ");
		String kategori=sc.nextLine();
		System.out.println("Type Øvelse(styrke,kondisjon): ");
		String ØvelseType=sc.nextLine();
		try{
			String sql = "INSERT INTO ØVELSE (navn,beskrivelse,kategori,ØvelseType)" + "VALUES (?,?,?,?)"; 
			
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, navn);
			statement.setString(2, beskrivelse);
			statement.setString(3, kategori);
			statement.setString(4, ØvelseType);
			System.out.println(statement);
			int a=statement.executeUpdate();
			if(a==1){
				System.out.println("Øvelse lagt til");
			}else{
				System.out.println("Operasjon misslykket");
			}
		}catch(SQLException se){
			se.printStackTrace();
		}
	}
	
	
	public void leggInnØkt(Connection conn,Scanner sc){
		System.out.println("Hvordan ville du rangert din egen form fra 1 til 10?");
		int personligForm=sc.nextInt();
		String throwaway = sc.nextLine();
		System.out.println("Har du noe du vil notere om treningsØkten?");
		String notat = sc.nextLine();
		System.out.println("Skriv inn datoen til Økten, samt nØr Økten startet og nØr den sluttet.");
		System.out.println("PØ formatet:");
		System.out.println("Dato: yyyy-mm-dd ");
		String dato = sc.nextLine();
		System.out.println("Startid: hh:mm");
		String start = sc.nextLine();
		System.out.println("Sluttid: hh:mm");
		String slutt = sc.nextLine();
	
		try{
			tid(dato,start,slutt);
			String sql = "INSERT INTO TRENINGSØKT (tidStart,tidSlutt,varighet,personligForm,notat)" 
					+ "VALUES (?,?,?,?,?)";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, getStart());
			statement.setString(2, getSluttTid());
			statement.setInt(3, getVarighet());
			statement.setInt(4, personligForm);
			statement.setString(5, notat);
			int a=statement.executeUpdate();
			if(a==1){
				System.out.println("TreningsØkt opprettet");
			}
			
		}catch(SQLException se){
			se.printStackTrace();
		}
	}
	public void ØktØvelse(Connection conn,Scanner sc, int ID){
		System.out.println("Hvilke Øvelser utfØrte du?");
		System.out.println("Velg ØvelsesID og trykk enter");
		System.out.println("NØr du er ferdig skriv exit");
		try{
			while(true){
				String Øvelse=sc.nextLine();
				if(Øvelse.equals("exit")){
					break;
				}
				else{
					try{
					String sql = "INSERT INTO ØKTØVELSE (ØktID,ØvelsesID)" 
							+ "VALUES (?,?)";
					PreparedStatement statement = conn.prepareStatement(sql);
					statement.setInt(1, ID);
					statement.setInt(2, Integer.parseInt(Øvelse));
					System.out.println(statement);
					int a=statement.executeUpdate();
					if(a==1){
						System.out.println("Øvelse lagt til");
					}
					Get g = new Get();
					g.øvelserGjennomfØrt(conn, ID);
					}catch(SQLException e){
						
					}
				}
			}
		}catch(InputMismatchException e){
			return;
		}
	}
	
	//Skal in insertklassen
	public void leggInnResultat(Connection conn, Scanner sc){
		Get g = new Get();
		int ID = g.sisteØktID(conn);
		Stack<Integer> s = g.øvelserGjennomfØrt(conn, ID);
		
		while (!(s.empty())){
			
			int øvelsesID = s.pop();
			String øvelseType = g.getØvelseType(conn, øvelsesID);
			String øvelsesNavn= g.getØvelseNavn(conn, øvelsesID);
			System.out.println("Registrer resultater for " + øvelsesNavn + ": ");
			System.out.println("Noter personlig prestasjon[1-10]: ");
			int pPrest = sc.nextInt();
			
			if (øvelseType.equals("kondisjon")){
				System.out.println("Noter distanse i meter: ");
				int distanse = sc.nextInt();
				System.out.println("Noter varighet i minutter og sekunder [mm:ss]: ");
				String varighet = sc.nextLine();
				int varighetTotal = stringToTimeConverter(varighet);
				
				try{
					String sql = "INSERT INTO RESULTAT (distanse, varighet,"
							+ " personligPrestasjon, øktID, øvelsesID)" 
							+ "VALUES (?,?,?,?,?)";
					PreparedStatement statement = conn.prepareStatement(sql);
					statement.setInt(1, distanse);
					statement.setInt(2, varighetTotal);
					statement.setInt(3, pPrest);
					statement.setInt(4, ID);
					statement.setInt(5, øvelsesID);
					System.out.println(statement);
					int a=statement.executeUpdate();
					if(a==1){
						System.out.println("Resultat registrert");
					}
					
					}catch(SQLException e){
						
					}
				
			}
			
			else if (øvelseType.equals("styrke")){
				System.out.println("Noter antall reps: ");
				int reps = sc.nextInt();
				String none = sc.nextLine();
				
				System.out.println("Noter antall set: ");
				int sets = sc.nextInt();
				none = sc.nextLine();
				
				System.out.println("Noter belastning i kg: ");
				int belastning = sc.nextInt();
				none = sc.nextLine();
				
				try{
					String sql = "INSERT INTO RESULTAT (reps, sets, belastning"
							+ " personligPrestasjon, øktID, øvelsesID)" 
							+ "VALUES (?,?,?,?,?,?)";
					PreparedStatement statement = conn.prepareStatement(sql);
					statement.setInt(1, reps);
					statement.setInt(2, sets);
					statement.setInt(3, belastning);
					statement.setInt(4, pPrest);
					statement.setInt(5, ID);
					statement.setInt(6, øvelsesID);
					System.out.println(statement);
					int a=statement.executeUpdate();
					if(a==1){
						System.out.println("Resultat registrert");
					}
					
					}catch(SQLException e){
						
					}
			}
			
		}
	}
	
	
	public int stringToTimeConverter(String s){
			
			int minutter = Integer.parseInt(s.substring(0,2));
			int sekunder = Integer.parseInt(s.substring(3,5));
			int total = (minutter * 60) + sekunder; 
			return total;
	}
	
	
	public void tid(String dato,String start,String slutt){
		startTid(dato,start);
		sluttTid(dato,slutt);
		varighet(start,slutt);
	}
	
	public void startTid(String dato, String tid){
		
		startTid = (dato+" " + tid+":00");
	}
	public String getStart(){
		return startTid;
	}
	public void sluttTid(String dato, String tid){
		
		 sluttTid = (dato+" " + tid+":00");
	}
	public String getSluttTid(){
		return sluttTid;
	}
	public void varighet(String start, String slutt){
		int start1 = Integer.parseInt(start.substring(0,2));
		int start2 = Integer.parseInt(start.substring(3,5));
		int slutt1 = Integer.parseInt(slutt.substring(0,2));
		int slutt2 = Integer.parseInt(slutt.substring(3,5));
		int h = (slutt1-start1)*60;
		int m = slutt2-start2;
		varighet = h+m;
	}
	public int getVarighet(){
		return varighet;
	}
	
	
	public static void main(String[] args) {
	}
		
		
	}
	
