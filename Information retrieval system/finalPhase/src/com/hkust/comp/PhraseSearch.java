package com.hkust.comp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.hkust.comp.urlword.InvertedIndexer;
import com.hkust.comp.urlword.TitleInvertedIndexer;

// Input: Vector<Word_ID>, representing a phrase by the word_ID of the individual words
// Output: HashMap<URL_ID,TF>

public class PhraseSearch {
		
		
		public static Vector<HashMap<Integer,Integer>> getAllTFByPage(HashMap<Vector<Integer>,Integer> phrase,int titleWeight){
			Vector<HashMap<Integer,Integer>> result = new Vector<HashMap<Integer,Integer>>();
			Iterator it = phrase.keySet().iterator();
			while(it.hasNext()){
				Vector<Integer> phrasetmp = (Vector<Integer>) it.next();
				HashMap<Integer,Integer> phraseResult = getTFByPage(phrasetmp,false);
				HashMap<Integer,Integer> titlephraseResult = getTFByPage(phrasetmp,true);
				for(int i:titlephraseResult.keySet()){
					if(phraseResult.get(i)!=null){
						phraseResult.put(i,phraseResult.get(i)+titlephraseResult.get(i)*titleWeight);
					}else{
						phraseResult.put(i, titlephraseResult.get(i)*titleWeight);
					}
				}
				result.add(phraseResult);
			}
		
			System.out.println(phrase);
			System.out.println(result);
		
			
			return result;
		}
		// This method is for regular inverted file
		public static HashMap<Integer, Integer> getTFByPage(Vector<Integer> phrase, boolean isTitle){
			if(phrase.size()==0)
				return null;
			HashMap<Integer,Integer> result = new HashMap<Integer,Integer>();	// <urlID,tf> pairs
			Vector<HashMap<Integer,Vector<Integer>>> maps = new Vector<HashMap<Integer,Vector<Integer>>>();
			// for each word store a HashMap for their position information
			for(int i = 0;i<phrase.size();i++){
				try {
					if(!isTitle){
						if(InvertedIndexer.getURLPosiByWordID(phrase.get(i))!=null)
							maps.add(InvertedIndexer.getURLPosiByWordID(phrase.get(i)));
					}else{
						if(TitleInvertedIndexer.getURLPosiByWordID(phrase.get(i))!=null)
							maps.add(TitleInvertedIndexer.getURLPosiByWordID(phrase.get(i)));
					}
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
			}
			
			
			// Now check whether they are adjacent to each other
			// Iterate through all every urlID that contains the first word in the phrase
			if(maps.size()==0) return result;
			Iterator it = maps.get(0).keySet().iterator();
			while (it.hasNext()) {//urlids containing first word
				// Given url_ID, iterate through every position this word appears
				// and check its following words
				int tf = 0;
				int urlID = (Integer) it.next();
				Vector<Integer> positions = maps.get(0).get(urlID);//Position list
				for (int j = 0; j < positions.size(); j++) {
					//Search in begin at second hashmap 1
					if(searchWithPosition(maps,1,urlID, positions.get(j)+1)||searchWithPosition(maps,1,urlID, positions.get(j)+2)||searchWithPosition(maps,1,urlID, positions.get(j)+2))
						tf++;
				}
				if(tf!=0) result.put(urlID, tf);
			}
			System.out.println(result);
			return result;
		}

		// Helper method. WordOrder is the order of current word in the phrase
		//word1<urlid->position> word2<urlid->position>
		private static boolean searchWithPosition(Vector<HashMap<Integer,Vector<Integer>>> maps,int wordOrder, int urlID,int position){
			//System.out.println("What "+maps+" "+wordOrder+" "+urlID+" "+position);
			
			if(wordOrder==maps.size()){
				//System.out.println("Return True");
				return true;// we get a whole phrase in the current URLID
			}
			
				Vector<Integer> posilist = maps.get(wordOrder).get(urlID);
				if(posilist==null){
					//No url contain both the previous word and the current word
					//System.out.println("Return False");
					return false;
				}else{
					int posi=posilist.indexOf(position);
					if(posi<0){
						//No word
						System.out.println(position+" Return False "+urlID);
						
						return false;
						
					}else{
						//Window Size Set to three
						System.out.println("Found");
						return searchWithPosition(maps,wordOrder+1,urlID,posilist.get(posi)+1)||searchWithPosition(maps,wordOrder+1,urlID,posilist.get(posi)+2)||searchWithPosition(maps,wordOrder+1,urlID,posilist.get(posi)+3);
					}
				}
		
		
		}
	
};