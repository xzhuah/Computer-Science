package com.hkust.comp.urlword;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;


/**
 * This class synchronizes the TitleInvertedIndexer and TitleForwardIndexer
 * @author Xinyu
 *
 */
public class TitleIndexer {

	
	
	@SuppressWarnings("deprecation")
	public static void addWordURLPosition(int wordID,int URLID,int position) throws IOException{
		TitleInvertedIndexer.addWordURL(wordID, URLID,position);
		TitleForwardIndexer.addURLWord(URLID, wordID,position);
	}
	

	
	
	@SuppressWarnings("deprecation")
	public static void delURLEntry(int urlID) throws IOException{
		TitleForwardIndexer.delEntry(urlID);
		Vector<Integer> temp=TitleInvertedIndexer.getAllWordID();
		for(int i=0;i<temp.size();i++){
			TitleInvertedIndexer.delURL(temp.get(i), urlID);
		}
	}
	
	
	@SuppressWarnings("deprecation")
	public static void delWordEntry(int wordID) throws IOException{
		TitleInvertedIndexer.delEntry(wordID);
		Vector<Integer> temp=TitleForwardIndexer.getAllURLID();
		for(int i=0;i<temp.size();i++){
			TitleForwardIndexer.delWord(temp.get(i), wordID);
		}
	}
	
	
	
	
	@SuppressWarnings("deprecation")
	public static void delURLWord(int urlID,int wordID){
		TitleForwardIndexer.delWord(urlID, wordID);
		TitleInvertedIndexer.delURL(wordID, urlID);
	}
	
	public static void delURLWordPosi(int urlid,int wordid,int position){
		TitleForwardIndexer.delWordPosi( urlid, wordid, position);
		TitleInvertedIndexer.delURLPosi(wordid, urlid, position);
	}

	
	
	
	@SuppressWarnings("deprecation")
	public static String getString(){
		String result="";
		result+="WordID->{URLID=Vector<Position>}\n";
		result+=TitleInvertedIndexer.getString();
		result+="URLID->{WordID=Vector<Position>}\n";
		result+=TitleForwardIndexer.getString();
		
		return result;
	}
	
	public String toString(){
		return getString();
	}
	
	@SuppressWarnings("deprecation")
	public static void saveToDisk() throws IOException{
		TitleInvertedIndexer.saveToDisk();
		TitleForwardIndexer.saveToDisk();
	}
	@SuppressWarnings("deprecation")
	public static void close() throws IOException{
		TitleInvertedIndexer.close();
		TitleForwardIndexer.close();
	}

	
	public static void main(String args[]) throws IOException{
		/*TitleIndexer.addWordURLPosition(1, 1, 1);
		TitleIndexer.addWordURLPosition(1, 1, 4);
		
		TitleIndexer.addWordURLPosition(1, 2, 2);
		TitleIndexer.addWordURLPosition(1, 2, 4);
		TitleIndexer.addWordURLPosition(1, 2, 3);
		TitleIndexer.addWordURLPosition(1, 2, 1);
		
		TitleIndexer.addWordURLPosition(1, 3, 1);
		TitleIndexer.addWordURLPosition(1, 3, 2);
		TitleIndexer.addWordURLPosition(1, 3, 3);
		
		TitleIndexer.addWordURLPosition(1, 4, 1);
		TitleIndexer.addWordURLPosition(1, 4, 2);
		TitleIndexer.addWordURLPosition(1, 4, 3);
		
		TitleIndexer.addWordURLPosition(2, 1, 1);
		TitleIndexer.addWordURLPosition(2, 1, 4);
		
		TitleIndexer.addWordURLPosition(2, 2, 2);
		TitleIndexer.addWordURLPosition(2, 2, 4);
		TitleIndexer.addWordURLPosition(2, 2, 3);
		TitleIndexer.addWordURLPosition(2, 2, 1);
		
		TitleIndexer.addWordURLPosition(3, 3, 1);
		TitleIndexer.addWordURLPosition(3, 3, 2);
		TitleIndexer.addWordURLPosition(3, 3, 3);
		
		TitleIndexer.addWordURLPosition(3, 4, 1);
		TitleIndexer.addWordURLPosition(3, 4, 2);
		TitleIndexer.addWordURLPosition(3, 4, 3);
		
		//TitleIndexer.delURLEntry(3);
		TitleIndexer.delWordEntry(2);
		//TitleIndexer.delURLWord(4, 3);
		TitleIndexer.addWordURLPosition(3, 4, 3);*/
		//Indexer.delURLWordPosi(2, 1, 2);
		//(new BufferedWriter(new FileWriter("./TitleIndexTest.txt"))).write(TitleIndexer.getString());
		System.out.println(TitleIndexer.getString());
		
	}
	
	

}
