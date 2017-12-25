package com.hkust.comp.urlword;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.text.Position;

import com.hkust.comp.PosItem;
import com.hkust.comp.SpiderTest;
import com.hkust.comp.VectorSpace;
import com.hkust.comp.WordRecord;
import com.hkust.comp.urlPage.PageInfo;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.helper.FastIterator;
import jdbm.htree.HTree;

/**
 * This class provide invert index service
 * wordID->HashMap<URLID,Vector<Position>>
 * wordID->HashMap<URLID,tf>
 *  
 * @author Xinyu
 *
 */


public class TitleInvertedIndexer {

	/**
	 * @param args
	 */
	private static RecordManager recman;
	private static HTree hashtable;
	
	private static HTree hashtableTf;
	
	private static final String db_name="TitleInvertIndex";
	private static final String tb_name="Word2URL";
	private static final String tb_name2="Word2URLtf";
	
	static{
		//Initialize jdbm
		try {
			recman = RecordManagerFactory.createRecordManager(SpiderTest.dbroot+db_name);
			long recid = recman.getNamedObject(tb_name);
			long recid2 = recman.getNamedObject(tb_name2);
			if (recid != 0){
				hashtable = HTree.load(recman, recid);
				
			}
			else{
				hashtable = HTree.createInstance(recman);
				recman.setNamedObject( tb_name, hashtable.getRecid() );
			}
			if(recid2 != 0){
				hashtableTf = HTree.load(recman, recid2);
			}else{
				hashtableTf = HTree.createInstance(recman);
				recman.setNamedObject( tb_name2, hashtableTf.getRecid() );
			}
		} catch (IOException e) {
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
	
	/**
	 * Add a single new relation 
	 * @param wordID
	 * @param URLID
	 * @throws IOException
	 */
	
	public static void addWordURL(int wordID,int URLID,int Position) throws IOException{
		//Try to get a HashSet Object from wordID
		Object obj=hashtable.get(wordID);
		if(obj==null){
			
			//A new wordID encountered
			HashMap<Integer,Vector<Integer>> temp=new HashMap<Integer,Vector<Integer>>();
			Vector<Integer> tempvector=new Vector<Integer>();
			tempvector.add(Position);
			temp.put(URLID, tempvector);
			hashtable.put(wordID, temp);
			
			//Update tf
			HashMap<Integer,Integer> urltf=new HashMap<Integer,Integer>();
			urltf.put(URLID, 1);
			hashtableTf.put(wordID, urltf);
			
			
		}else{
			
			//A saved wordID encountered
			HashMap<Integer,Vector<Integer>> map=(HashMap<Integer,Vector<Integer>>)obj;
			Vector<Integer> posi=map.get(URLID);
			if(posi==null){
			
				//A new URL encounted
				posi=new Vector<Integer>();
				posi.add(Position);
				map.put(URLID, posi);
				hashtable.put(wordID, map);
				
				//Update tf
				HashMap<Integer,Integer> urltf=(HashMap<Integer,Integer>)hashtableTf.get(wordID);
				urltf.put(URLID, 1);
				hashtableTf.put(wordID, urltf);
				
				return;
			}else{
				
				//An old URL encounted
				for(int i=posi.size()-1;i>=0;i--){
					//
					int ele=posi.get(i);
					if(ele<Position){
						posi.insertElementAt(Position, i+1);
						map.put(URLID,posi);
						
						hashtable.put(wordID, map);
						
						//Update tf
						HashMap<Integer,Integer> urltf=(HashMap<Integer,Integer>)hashtableTf.get(wordID);
						urltf.put(URLID, urltf.get(URLID)+1);
						hashtableTf.put(wordID, urltf);
						
						return;
					}else if(ele==Position){
						//No need to add
						
						return;
					}
				}	
				posi.insertElementAt(Position, 0);
				map.put(URLID,posi);
				//System.out.println("Add head New Map is "+map);
				hashtable.put(wordID, map);
				//System.out.println("Add head New hash table is "+getString());
				
				//Update tf
				HashMap<Integer,Integer> urltf=(HashMap<Integer,Integer>)hashtableTf.get(wordID);
				urltf.put(URLID, urltf.get(URLID)+1);
				hashtableTf.put(wordID, urltf);
				
				
			}
			
		}
	}
	
	
	
	/**
	 * Add a set of new relation
	 * @param wordID
	 * @param URLIDset
	 * @throws IOException
	 */
	
	/*
	public static void addWordURL(int wordID,HashSet<Integer> URLIDset) throws IOException{
		Object obj=hashtable.get(wordID);
		if(obj==null){
			//A new wordID encountered
			
			hashtable.put(wordID, URLIDset);
		}else{
			//A saved wordID encountered
			((HashSet<Integer>)obj).addAll(URLIDset);
			hashtable.put(wordID, obj);
		}
	}
	
	/**
	 * add a set of new relation
	 * @param wordID
	 * @param URLIDvector
	 * @throws IOException
	 */
	
	/*
	public static void addWordURL(int wordID,Vector<Integer> URLIDvector) throws IOException{
		Object obj=hashtable.get(wordID);
		if(obj==null){
			//A new wordID encountered
			HashSet<Integer> URLIDset=new HashSet<Integer>();
			URLIDset.addAll(URLIDvector);
			hashtable.put(wordID, URLIDset);
		}else{
			//A saved wordID encountered
			((HashSet<Integer>)obj).addAll(URLIDvector);
			hashtable.put(wordID, obj);
		}
	}
	*/
	public static void delEntry(int wordID){
		try {
			hashtable.remove(wordID);
			hashtableTf.remove(wordID);
		} catch (IOException e) {
			//may be no wordID is found
			e.printStackTrace();
		}
	}
	
	
	public static void delURL(int wordID,int urlID){
		try {
			HashMap<Integer,Vector<Integer>> hset = (HashMap<Integer,Vector<Integer>>)hashtable.get(wordID);
			hset.remove(urlID);
			hashtable.put(wordID, hset);
			
			HashMap<Integer,Integer> hset2 = (HashMap<Integer,Integer>)hashtableTf.get(wordID);
			hset2.remove(urlID);
			hashtableTf.put(wordID, hset2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void delURLPosi(int wordID,int urlID,int position){
		try{
			HashMap<Integer,Vector<Integer>> hset = (HashMap<Integer,Vector<Integer>>)hashtable.get(wordID);
			Vector<Integer> tempV=hset.get(urlID);
			if(tempV.remove((Integer)position)){
				if(tempV.size()==0){
					delURL(wordID,urlID);
					return;
				}
				hset.put(urlID, tempV);
				hashtable.put(wordID, hset);
				
				//Update tf
				HashMap<Integer,Integer> hset2 = (HashMap<Integer,Integer>)hashtableTf.get(wordID);
				hset2.put(urlID, hset2.get(urlID)-1);
				hashtableTf.put(wordID, hset2);
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static HashMap<Integer,Vector<Integer>> getURLPosiByWordID(int wordID) throws IOException{
		Object obj=hashtable.get(wordID);
		if(obj!=null) return (HashMap<Integer,Vector<Integer>>)obj;		
		else return null;	
	}
	
	public static HashMap<Integer,Integer> getURLTfByWordID(int wordID) throws IOException{
		Object obj=hashtableTf.get(wordID);
		if(obj!=null) return (HashMap<Integer,Integer>)obj;		
		else return null;
	}
	
	//To help vectorspace find idf
		public static HashMap<Integer,Double> getIdfByWordID(Set<Integer> wordidset) throws IOException{
			Iterator<Integer> it = wordidset.iterator();
			int pageNum=VectorSpace.listSize;
			HashMap<Integer,Double> result=new HashMap<Integer,Double>();
			while(it.hasNext()){
				int wordid=it.next();
				int relatedPageNum=getURLTfByWordID(wordid).keySet().size();
				double idf=Math.log((double)pageNum/relatedPageNum)/Math.log(2);
				result.put(wordid, idf);
			}
			return result;
		}

		
	public static Vector<Integer> getAllWordID() throws IOException{
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
				result+=((int)setKey+" -> ");
				result+=(HashMap<Integer,Vector<Integer>>)hashtable.get(setKey)+"\n";
				setKey=it.next();
			}
			result+="===============================Term Frequency==========================================\n";
			FastIterator it2 = hashtableTf.keys();
			Object setKey2=it2.next();
			while(setKey2!=null){
				result+=((int)setKey2+" -> ");
				result+=(HashMap<Integer,Integer>)hashtableTf.get(setKey2)+"\n";
				setKey2=it2.next();
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
	
		TitleInvertedIndexer.addWordURL(1, 1,4);
		TitleInvertedIndexer.addWordURL(1, 1,2);
		TitleInvertedIndexer.addWordURL(1, 1,1);
		TitleInvertedIndexer.addWordURL(1, 1,3);
		TitleInvertedIndexer.addWordURL(1, 2,1);
		TitleInvertedIndexer.addWordURL(1, 3,1);
		TitleInvertedIndexer.addWordURL(1, 4,1);
		
		TitleInvertedIndexer.addWordURL(2, 1,1);
		TitleInvertedIndexer.addWordURL(3, 1,2);
		TitleInvertedIndexer.addWordURL(2, 1,2);
		TitleInvertedIndexer.addWordURL(1, 1,3);
		TitleInvertedIndexer.addWordURL(3, 2,4);
		TitleInvertedIndexer.addWordURL(4, 3,5);
		TitleInvertedIndexer.addWordURL(4, 4,5);
		System.out.println(TitleInvertedIndexer.getString());
	
		
		
	}

}
