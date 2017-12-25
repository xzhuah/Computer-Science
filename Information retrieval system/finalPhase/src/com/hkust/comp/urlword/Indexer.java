package com.hkust.comp.urlword;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import com.hkust.comp.wordLib.WordLib;


/**
 * This class synchronizes the InvertedIndexer and ForwardIndexer
 * @author Xinyu
 *
 */
public class Indexer {

	
	
	@SuppressWarnings("deprecation")
	public static void addWordURLPosition(int wordID,int URLID,int position) throws IOException{
		InvertedIndexer.addWordURL(wordID, URLID,position);
		ForwardIndexer.addURLWord(URLID, wordID,position);
	}
	

	
	
	@SuppressWarnings("deprecation")
	public static void delURLEntry(int urlID) throws IOException{
		ForwardIndexer.delEntry(urlID);
		Vector<Integer> temp=InvertedIndexer.getAllWordID();
		for(int i=0;i<temp.size();i++){
			InvertedIndexer.delURL(temp.get(i), urlID);
		}
	}
	
	
	@SuppressWarnings("deprecation")
	public static void delWordEntry(int wordID) throws IOException{
		InvertedIndexer.delEntry(wordID);
		Vector<Integer> temp=ForwardIndexer.getAllURLID();
		for(int i=0;i<temp.size();i++){
			ForwardIndexer.delWord(temp.get(i), wordID);
		}
	}
	
	
	
	
	@SuppressWarnings("deprecation")
	public static void delURLWord(int urlID,int wordID){
		ForwardIndexer.delWord(urlID, wordID);
		InvertedIndexer.delURL(wordID, urlID);
	}
	
	public static void delURLWordPosi(int urlid,int wordid,int position){
		ForwardIndexer.delWordPosi( urlid, wordid, position);
		InvertedIndexer.delURLPosi(wordid, urlid, position);
	}
	
	
	
	@SuppressWarnings("deprecation")
	public static String getString(){
		String result="";
		result+="WordID->{URLID=Vector<Position>}\n";
		result+=InvertedIndexer.getString();
		result+="URLID->{WordID=Vector<Position>}\n";
		result+=ForwardIndexer.getString();
		
		return result;
	}
	
	public String toString(){
		return getString();
	}
	
	@SuppressWarnings("deprecation")
	public static void saveToDisk() throws IOException{
		InvertedIndexer.saveToDisk();
		ForwardIndexer.saveToDisk();
	}
	@SuppressWarnings("deprecation")
	public static void close() throws IOException{
		InvertedIndexer.close();
		ForwardIndexer.close();
	}

	
	public static void main(String args[]) throws IOException{
		/*Indexer.addWordURLPosition(1, 1, 1);
		Indexer.addWordURLPosition(1, 1, 4);
		
		Indexer.addWordURLPosition(1, 2, 2);
		Indexer.addWordURLPosition(1, 2, 4);
		Indexer.addWordURLPosition(1, 2, 3);
		Indexer.addWordURLPosition(1, 2, 1);
		
		Indexer.addWordURLPosition(1, 3, 1);
		Indexer.addWordURLPosition(1, 3, 2);
		Indexer.addWordURLPosition(1, 3, 3);
		
		Indexer.addWordURLPosition(1, 4, 1);
		Indexer.addWordURLPosition(1, 4, 2);
		Indexer.addWordURLPosition(1, 4, 3);
		
		Indexer.addWordURLPosition(2, 1, 1);
		Indexer.addWordURLPosition(2, 1, 4);
		
		Indexer.addWordURLPosition(2, 2, 2);
		Indexer.addWordURLPosition(2, 2, 4);
		Indexer.addWordURLPosition(2, 2, 3);
		Indexer.addWordURLPosition(2, 2, 1);
		
		Indexer.addWordURLPosition(3, 3, 1);
		Indexer.addWordURLPosition(3, 3, 2);
		Indexer.addWordURLPosition(3, 3, 3);
		
		Indexer.addWordURLPosition(3, 4, 1);
		Indexer.addWordURLPosition(3, 4, 2);
		Indexer.addWordURLPosition(3, 4, 3);
		
		Indexer.delURLEntry(3);
		Indexer.delWordEntry(2);
		Indexer.delURLWordPosi(2, 1, 3);*/
		//(new BufferedWriter(new FileWriter("./IndexTest.txt"))).write(Indexer.getString());
		System.out.println(Indexer.getString());
		
		
	}
	
	

}
