package io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public final class IO {
	
		private IO() {}
		
	public static Set<String> readTextFile(String path) throws IOException{
		Set<String> entries = new HashSet<>();
		
		try(Scanner sc = new Scanner(new File(path))){
			while (sc.hasNextLine()) {
				entries.add(sc.nextLine());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return entries;
	}
	
	public static void writeTextFile(String path) throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter(path, false));
		bw.write("test");
		bw.close();
	}
	
	public static <T> void writeImages(Collection<T> c, String path) {
		
	}
	
	public static void main(String[] args) throws IOException {
		String path = System.getProperty("user.home") + "\\Documents\\test\\asdef.txt";
		//writeTextFile(path);
		readTextFile(path);
	}
	
	public String getStringFromImage() {
		
		return "";
	}
	
	
}
