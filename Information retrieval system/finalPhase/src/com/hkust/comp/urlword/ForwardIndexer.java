package com.hkust.comp.urlword;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import com.hkust.comp.SpiderTest;
import com.hkust.comp.urlPage.PageInfo;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.helper.FastIterator;
import jdbm.htree.HTree;

/**
 * This class provide forward service [URLID->HashMap<WordID,Vector<position>>] (wordFrequency can be easily gotten by using URLID)
 * @author Xinyu
 *
 *This Class is deprecated to use, please use Indexer instead
 *
 */

public class ForwardIndexer {

	/**
	 * @param args
	 */
	private static RecordManager recman;
	private static HTree hashtable;
	private static final String db_name="ForwardIndex";
	private static final String tb_name="URL2Word";
	static{
		try{
		recman = RecordManagerFactory.createRecordManager(SpiderTest.dbroot+db_name);
		long recid = recman.getNamedObject(tb_name);
		if (recid != 0){
			hashtable = HTree.load(recman, recid);
		}
		else{
			hashtable = HTree.createInstance(recman);
			recman.setNamedObject( tb_name, hashtable.getRecid() );
		}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void saveToDisk() throws IOException{
		recman.commit();
	}
	
	public static void close() throws IOException{
		recman.commit();
		recman.close();
	}
	
	public static void addURLWord(int URLID,int wordID,int position) throws IOException{
		//Try to get a HashSet Object from wordID
		Object obj=hashtable.get(URLID);
		if(obj==null){
			//A new URLID encountered
			HashMap<Integer,Vector<Integer>> temp=new HashMap<Integer,Vector<Integer>>();
			Vector<Integer> tempPosi=new Vector<Integer>();
			tempPosi.add(position);
			temp.put(wordID,tempPosi);
			hashtable.put(URLID, temp);
		}else{
			//A saved URLID encountered
			//Get the map
			HashMap<Integer,Vector<Integer>> map=((HashMap<Integer,Vector<Integer>>)obj);
			
			Vector<Integer> v=map.get(wordID);
			if(v==null){
				//a new word encountered
				v=new Vector<Integer>();
				v.add(position);
				map.put(wordID, v);
				hashtable.put(URLID, map);
				return;
			}else{
				//a old word encountered
				//一般都是从小到大加，从后面遍历比较快
				for(int i=v.size()-1;i>=0;i--){
					int ele=v.get(i);
					if(ele<position){
						v.insertElementAt(position, i+1);
						map.put(wordID,v);
						hashtable.put(URLID, map);
						return;
					}else if(ele==position){
						return;
					}
				}	
				v.insertElementAt(position, 0);
				
				map.put(wordID,v);
				hashtable.put(URLID, map);	
			}
		}
	}
	
	
	public static void delEntry(int URLID){
		try {
			hashtable.remove(URLID);
		} catch (IOException e) {
			//may be no wordID is found
			e.printStackTrace();
		}
	}
	
	public static void delWord(int urlID,int wordID){
		try {
			HashMap<Integer,Vector<Integer>> hset=(HashMap<Integer,Vector<Integer>>)hashtable.get(urlID);
			hset.remove(wordID);
			hashtable.put(urlID,hset);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void delWordPosi(int urlID,int wordID,int position){
		try {
			HashMap<Integer,Vector<Integer>> hset=(HashMap<Integer,Vector<Integer>>)hashtable.get(urlID);
			Vector<Integer> posi=hset.get(wordID);
			if(posi.remove((Integer)position)){		
				if(posi.size()==0){
					delWord(urlID,wordID);
					return;
				}
				hset.put(wordID, posi);
				hashtable.put(urlID, hset);
			}
			
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

	
	public static HashMap<Integer,Vector<Integer>> getWordPosiByUrlID(int URLID) throws IOException{
		Object obj=hashtable.get(URLID);
		if(obj==null) return null;
		return (HashMap<Integer,Vector<Integer>>) obj;
	}
	public static Vector<Integer> getAllURLID() throws IOException{
		Vector<Integer> result=new Vector<Integer>();
		FastIterator it = hashtable.keys();
		Object setKey=it.next();
		while(setKey!=null){
			result.add((Integer)setKey);
			setKey=it.next();
		}
		return result;
	}
	
	
	public static String getString() {
		String result="";
		try {
			FastIterator it = hashtable.keys();
			Object setKey=it.next();
			while(setKey!=null){
				result+=(setKey+" -> ");
				result+=(HashMap<Integer,Vector<Integer>>)hashtable.get(setKey)+"\n";
				setKey=it.next();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return "ERROR";
			
		}
		return result;
	}
	public String toString(){
		return getString();
	}
	
	public static void main(String[] args) throws IOException {
		//A demo of how to use this class
				//ForwardIndexer ivi=new ForwardIndexer();
				/*ForwardIndexer.addURLWord(1, 1,1);
				ForwardIndexer.addURLWord(1, 1,1);
				ForwardIndexer.addURLWord(1, 1,7);
				ForwardIndexer.addURLWord(1, 2,5);
				ForwardIndexer.addURLWord(1, 3,4);
				ForwardIndexer.addURLWord(1, 3,1);
				ForwardIndexer.addURLWord(1, 3,2);
				ForwardIndexer.addURLWord(1, 3,3);
				ForwardIndexer.addURLWord(1, 4,2);
				
				
				ForwardIndexer.addURLWord(3, 1,4);
				ForwardIndexer.addURLWord(4, 1,1);
				ForwardIndexer.addURLWord(2, 1,2);
				
				ForwardIndexer.addURLWord(2, 2,3);
				ForwardIndexer.addURLWord(1, 3,5);
				ForwardIndexer.addURLWord(3, 3,2);
				ForwardIndexer.addURLWord(4, 3,3);
				ForwardIndexer.addURLWord(2, 3,4);
				ForwardIndexer.addURLWord(3, 4,5);
				ForwardIndexer.addURLWord(1, 4,1);
				ForwardIndexer.addURLWord(1, 3,7);
				ForwardIndexer.delWordPosi(1, 3, 4);
				ForwardIndexer.delWord(1, 2);
				ForwardIndexer.delEntry(4);*/
				
				System.out.println(ForwardIndexer.getString());
				
				
	}
	


	

}