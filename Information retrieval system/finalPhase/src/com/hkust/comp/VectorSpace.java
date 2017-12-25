/*
 *
 * Shang Hang:
 * 4. Vector Space Score Algorithm
 *  Input: Hashmap<Word_ID, freq> hash map returned from Query
 *      Hashmap<URL_ID, tf> returned by Phrase
  1. first get the tf*idf/maxtf for the query: 
  2. given the wordID -> <urlID, TF>, calculate the 
  3. 
 *  Output: Vector<TOP50 URL_ID>
 * 5. Title boost
 *  title_score = tf*idf/max(tf)
 *  final_score = score + title_score * 10?
 *
 */
package com.hkust.comp;

import java.util.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Collections;

import com.hkust.comp.pagerank.PageRank;
import com.hkust.comp.urlPage.PageInfo;
import com.hkust.comp.urlword.ForwardIndexer;
import com.hkust.comp.urlword.InvertedIndexer;
import com.hkust.comp.urlword.TitleInvertedIndexer;
import com.hkust.comp.urlword.TitleForwardIndexer;
import com.hkust.comp.Tuple;
import com.hkust.comp.PhraseSearch;
public class VectorSpace{
    
    
    private  static Vector<Integer> allURLList;
    public  static int listSize;

    static{
        try{
            allURLList = PageInfo.getAllCrawledID();
            listSize = allURLList.size();
            }
        catch(IOException e){
            e.printStackTrace();
        }
        
    }
    
    // given the Input <worID, tf> output the partail_score_sum<urlID,partial_socre_sum> 
    public static HashMap<Integer,Double> getPartialScoreQuery(HashMap<Integer, Integer> query, boolean title) throws IOException {
            HashMap<Integer,Double> partial_score_sum = new HashMap<Integer,Double>();
            HashMap<Integer,Double> query_weights = new HashMap<Integer,Double>();
            Iterator<Integer> it = query.keySet().iterator();
            
            //For every word in the query
            while(it.hasNext()){ 
                int wordID = it.next();
                int wordTF = query.get(wordID);
                HashMap<Integer,Integer> wordIDURLTf;
                if(!title){
                     wordIDURLTf = InvertedIndexer.getURLTfByWordID(wordID);
                }else{
                     wordIDURLTf = TitleInvertedIndexer.getURLTfByWordID(wordID);
                }
                if(wordIDURLTf==null) continue;
                double idf = (Math.log((double)listSize/wordIDURLTf.size())) / Math.log(2);
                int maxtf = Collections.max(wordIDURLTf.values());
                if(wordTF > maxtf){
                    maxtf = wordTF;
                }
                double wordWeight = wordTF*idf/maxtf;
                query_weights.put(wordID,wordWeight);
                Iterator<Integer> it2 = wordIDURLTf.keySet().iterator();
                while(it2.hasNext()){
                    
                    int urlid = it2.next();              
                    int docTf = wordIDURLTf.get(urlid);
                    double docWeight = docTf*idf/maxtf;
                    double partial_score = wordWeight*docWeight;
                    if(partial_score_sum.get(urlid)!=null){
                        partial_score_sum.put(urlid,partial_score_sum.get(urlid)+partial_score);
                    }else{
                        partial_score_sum.put(urlid,partial_score);
                    }
                }
            }
            
            return partial_score_sum;
    }

    //Very time consuming function
    public static HashMap<Integer,Double> getPageNorm(Set<Integer> urlIDset,boolean title) throws IOException{
    	
    	
        HashMap<Integer,Double> allPageWordWeight = new HashMap<Integer,Double>();
        Iterator<Integer> it = urlIDset.iterator();
        while(it.hasNext()){
            int urlID = it.next();
            HashMap<Integer,Vector<Integer>> rawPage;
            if(!title){            
                rawPage = ForwardIndexer.getWordPosiByUrlID(urlID); 
            }else{
                rawPage = TitleForwardIndexer.getWordPosiByUrlID(urlID);
            }
            HashMap<Integer,Integer> tfPage = new HashMap<Integer,Integer>();
            for(int k: rawPage.keySet()){
                Vector<Integer> positions = rawPage.get(k);
                int tf = positions.size();
                tfPage.put(k,tf);
            }
            int maxtf = Collections.max(tfPage.values());
            Iterator<Integer> it2 = tfPage.keySet().iterator();
            HashMap<Integer,Double> weightPage = new HashMap<Integer,Double>();
			
            while(it2.hasNext()){
                double idf = 0;
                double weight = 0;
                int wordID =  it2.next();
                int tf = tfPage.get(wordID);
                HashMap<Integer,Integer> wordIDURLTf;
                if(!title){
                    wordIDURLTf = InvertedIndexer.getURLTfByWordID(wordID);
                }else{
                    wordIDURLTf = TitleInvertedIndexer.getURLTfByWordID(wordID);
                }
                idf = (Math.log(listSize/wordIDURLTf.size())) / Math.log(2);
                weight = (tf*idf)/maxtf ;
                weightPage.put(wordID,weight);
            }
            
            double norm = getNorm(weightPage);
            allPageWordWeight.put(urlID,norm);
			
        }
        return allPageWordWeight;
    } 

    public static double getQueryNorm(HashMap<Integer,Integer> query) throws IOException{

        HashMap<Integer,Double> weightQuery = new HashMap<Integer,Double>();
        int maxtf = Collections.max(query.values());
        Iterator<Integer> it = query.keySet().iterator();
        while(it.hasNext()){
            int tf = 0;
            double idf = 0;
            double weight = 0;
            int wordID = it.next();
            tf = query.get(wordID);
            HashMap<Integer,Integer> wordIDURLTf = InvertedIndexer.getURLTfByWordID(wordID);
            idf = (Math.log(listSize/wordIDURLTf.size())) / Math.log(2);
            weight = (tf*idf)/maxtf;
            weightQuery.put(wordID,weight);
        }
        double norm1 = 0 ;
        for (int k : weightQuery.keySet()) norm1 += weightQuery.get(k)*weightQuery.get(k);
        return norm1;
    }

    public static HashMap<Integer,Double> getPhraseScore(HashMap<Integer,Integer> phrase,int freq) throws IOException{
        HashMap<Integer,Double> weightPhrase= new HashMap<Integer,Double>();
        int maxtf;
        try{
        maxtf = Collections.max(phrase.values());
        }catch(Exception e){
        	return null;
        }
        double idf  = (Math.log(listSize/phrase.size()))/Math.log(2);
        double queryWeight = freq*idf/maxtf;
        Iterator<Integer> it = phrase.keySet().iterator();
        while(it.hasNext()){
            int tf = 0;
            double weight = 0;
            int urlID = it.next();
            tf = phrase.get(urlID);
            weight = (tf*idf)/maxtf;
            weightPhrase.put(urlID,weight*queryWeight);
        }
        return weightPhrase;
    }
    //
    public static  TreeMap<Double,Integer> getQueryFinalSocore(HashMap<Integer, Integer> query,double titleWeight) throws IOException{
      
       //double rankweight=0.5;
        HashMap<Integer,Double> partial_score = getPartialScoreQuery(query,false);
        HashMap<Integer,Double> title_partial_score = getPartialScoreQuery(query,true);

        HashMap<Integer,Double> docNorm = getPageNorm(partial_score.keySet(),false);
        HashMap<Integer,Double> titleNorm = getPageNorm(title_partial_score.keySet(),true);
       

        double queryNorm = getQueryNorm(query);

        TreeMap<Double,Integer> allSim = new TreeMap<Double,Integer>();
        //add up score 
        for(int k:partial_score.keySet()){
            double div = Math.sqrt(docNorm.get(k)*queryNorm);
            double sim =0; 
            if(div!=0){
                 sim = partial_score.get(k)/div;
            }       
            double div_title = Math.sqrt(queryNorm*(titleNorm.get(k)==null?0:titleNorm.get(k)));
            double titlesim=0;
            if(div_title!=0){
                titlesim = (title_partial_score.get(k)==null?0:title_partial_score.get(k))/div_title;
            }
            //double score=sim+titleWeight*titlesim+PageRank.getRank(k)*rankweight;
            double score=sim+titleWeight*titlesim;
            allSim.put(score,k);
        }

        return allSim;
    }

    public static TreeMap<Double,Integer> getTop50Result( HashMap<Vector<Integer>,Integer> phrase, HashMap<Integer,Integer> query,double rankweight,double titleWeight,double PhraseWeigh){
    	boolean validQuery=(query!=null&&query.size()!=0);
    	boolean validPharse=(phrase!=null&&phrase.size()!=0);
		
    	
    	
    	long testtime=System.currentTimeMillis();
    	
        Vector<HashMap<Integer,Double>> phraseResult = new Vector<HashMap<Integer,Double>>();
        HashMap<Integer,Double> partial_score = new HashMap<Integer,Double>();
        HashMap<Integer,Double> title_partial_score = new HashMap<Integer,Double>();
        HashMap<Integer,Double> docNorm = new HashMap<Integer,Double>();
        HashMap<Integer,Double> titleNorm = new HashMap<Integer,Double>();
        double queryNorm = 0;
        TreeMap<Double,Integer> allSim = new TreeMap<Double,Integer>();
       

        if(validPharse){ 
            try{
                Vector<HashMap<Integer,Integer>> allPhrase = PhraseSearch.getAllTFByPage(phrase,(int)titleWeight);
               
              List<Integer> phraseTF = new ArrayList<Integer>(phrase.values());
             
              //Vector<HashMap<Integer,Integer>> phraseResult = new Vector<HashMap<Integer,Integer>>();
              for (int i=0;i<phrase.size();i++){
            	
                phraseResult.add(getPhraseScore(allPhrase.get(i),phraseTF.get(i)));
                
              }
              /////////////////////////////////////////
              System.out.println("Find Pages Containing Phrase and calculate score uses "+(System.currentTimeMillis()-testtime)+"ms");
              testtime=System.currentTimeMillis();
              //////////////////////////////////////////
          }catch(IOException e){
            //do nothing;
          }
              
        }
        if(validQuery){
        
        try{
            partial_score = getPartialScoreQuery(query,false);
            title_partial_score = getPartialScoreQuery(query,true);
            titleNorm = getPageNorm(title_partial_score.keySet(),true);
            queryNorm = getQueryNorm(query);      
            /////////////////////////////////////////
            System.out.println("Calculate titleNorm and Query Norm uses "+(System.currentTimeMillis()-testtime)+"ms");
            testtime=System.currentTimeMillis();
            //////////////////////////////////////////
            
        }catch(IOException e){
            //do nothing;
            }

        }
        if (validPharse){
            for(int i=0;i<phraseResult.size();i++){
                HashMap<Integer,Double> phrasetmp = phraseResult.get(i);
                if(phrasetmp==null) continue;
                for (int k: phrasetmp.keySet()){
                    if(partial_score.get(k)==null){
                        partial_score.put(k,phrasetmp.get(k)*PhraseWeigh);
                    }else{
                        partial_score.put(k,partial_score.get(k)+phrasetmp.get(k)*PhraseWeigh);
                    }
                }
            }
           
            /////////////////////////////////////////
            System.out.println("Add phrase score to partial score uses "+(System.currentTimeMillis()-testtime)+"ms");
            testtime=System.currentTimeMillis();
            //////////////////////////////////////////
        }

    try{
    	//After testing, we found the following statement takes the longest time in the current function
        docNorm = getPageNorm(partial_score.keySet(),false);
        
      
        
        //Take very little time in the following
        if(validPharse){
            for(int i=0;i<phraseResult.size();i++){
                HashMap<Integer,Double> phrasetmp = phraseResult.get(i);
                if(phrasetmp==null) continue;
                for (int k: phrasetmp.keySet()){
                    if(docNorm.get(k)==null){
                        docNorm.put(k,docNorm.get(k)*phrasetmp.get(k));
                    }else{
                        docNorm.put(k,docNorm.get(k)+phrasetmp.get(k)*phrasetmp.get(k));
                    }
                }
            }
           
            /////////////////////////////////////////
            System.out.println("Add phrase score to docNorm uses "+(System.currentTimeMillis()-testtime)+"ms");
            testtime=System.currentTimeMillis();
            //////////////////////////////////////////
        }
        //get the full docNorm;
        // if the query exist
        if(validQuery){

            for(int k:partial_score.keySet()){
                double div = Math.sqrt(docNorm.get(k)*queryNorm);
                double sim =0; 
                if(div!=0){
                     sim = partial_score.get(k)/div;
                }       
                double div_title = Math.sqrt(queryNorm*(titleNorm.get(k)==null?0:titleNorm.get(k)));
                double titlesim=0;
                if(div_title!=0){
                    titlesim = (title_partial_score.get(k)==null?0:title_partial_score.get(k))/div_title;
                }
                double score=sim+titleWeight*titlesim+PageRank.getRank(k)*rankweight;
                allSim.put(score,k);
            }
            
            /////////////////////////////////////////
            System.out.println("Final sorting with query "+(System.currentTimeMillis()-testtime)+"ms");
            testtime=System.currentTimeMillis();
            //////////////////////////////////////////
            
            
            return allSim;
        }else{
        	
            for(int k:partial_score.keySet()){
                double div = Math.sqrt(docNorm.get(k));
                //System.out.println(k+" "+docNorm.get(k)*queryNorm);
                double sim =0; 
                if(div!=0){
                     sim = partial_score.get(k)/div;
                }       
                double score=sim+PageRank.getRank(k)*rankweight;
                allSim.put(score,k);
            }
            
            
            /////////////////////////////////////////
            System.out.println("Final sorting without query "+(System.currentTimeMillis()-testtime)+"ms");
            testtime=System.currentTimeMillis();
            //////////////////////////////////////////
            
            
            return allSim;
        } 
    }catch(IOException e){
        //allURLList = getAllCrawledID();
        try{
                for(int i = 0;i<listSize;i++){
                double score = PageRank.getRank(allURLList.get(i))*rankweight;
                allSim.put(score,allURLList.get(i));
            }
                
				/////////////////////////////////////////
				System.out.println("Final sorting with error "+(System.currentTimeMillis()-testtime)+"ms");
				testtime=System.currentTimeMillis();
				//////////////////////////////////////////
                
                return allSim;
        }catch(IOException e1){
            return allSim;
        }
    }

    }

    // get the norm, which is not sqrt 
    public static double getNorm(HashMap<Integer,Double> input){
        double norm = 0;
        for (int k: input.keySet()) norm += input.get(k) * input.get(k);
        return norm;
    }
    
    
    
    public static void main(String args[]) throws IOException{
    	Porter helper=new Porter();
    	
        String s="computer science";
        //HashMap<Integer,Integer> query=Tools.getWordVector(s);
        HashMap<Integer,Integer> query=null;
        String ss="\"computer science\"";
        HashMap<Vector<Integer>, Integer> phrase=Tools.getPhraseVector(ss);
        //HashMap<Vector<Integer>, Integer> phrase=null;
		TreeMap<Double, Integer> result = VectorSpace.getTop50Result(phrase,query,0.3,3,20);
        System.out.println(result);
        Iterator<Double> it=result.descendingKeySet().iterator();
        while(it.hasNext()){
        	double id=it.next();
        	System.out.println(PageInfo.getURLbyID(result.get(id))+" "+id);
        }
        /*HashMap<Integer, Double> b = getPartialScoreQuery(query,false);
        HashMap<Integer, Double> c = getPageNorm(b.keySet(),true);
        System.out.println(c);*/
    
        //Set<Integer> urlIDset=new Set<Integer>();
    }



}