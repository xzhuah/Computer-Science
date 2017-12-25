package com.hkust.comp.pagerank;

import java.io.IOException;
import java.util.Vector;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.htree.HTree;

import com.hkust.comp.PageFile;
import com.hkust.comp.SpiderTest;
import com.hkust.comp.pageRelation.PageChildren;
import com.hkust.comp.pageRelation.PageRelations;
import com.hkust.comp.urlPage.PageInfo;
import com.hkust.comp.urlword.Indexer;
import com.hkust.comp.urlword.TitleIndexer;
import com.hkust.comp.wordLib.WordLib;

public class PageRank {

	/**
	 * @param args
	 */
	private static RecordManager recman;
	private static HTree hashtable;
	private static final String db_name="PageRank";
	private static final String tb_name="URLID2Rank";
	public static double d=0.85;
	
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
	
	public static double getRank(int urlID) throws IOException{
		Object obj=hashtable.get(urlID);
		if(obj!=null){
			return (double)obj ;
		}else{
			return -1;
		}
		
	}
	
	
	
	public static void updatePageRank() throws IOException{
		
		//index of all interesting page
			Vector<Integer> ids=PageInfo.getAllCrawledID();
			Page page[]=new Page[ids.size()];
			for(int i=0;i<page.length;i++){
				page[i]=new Page(ids.get(i));
				//System.out.println(page[i].toString());
			}
			
			for(int i=0;i<100;i++){
				boolean needMore=false;
				
				for(int j=0;j<page.length;j++){
					double ori=page[j].pageRank;
					page[j].pageRank=PageRank(j,page);
					if(ori!=page[j].pageRank) needMore=true;
				}
				
				if(!needMore){
					
					
					break;
				}
			}
			for(int i=0;i<page.length;i++){
				hashtable.put(page[i].index, page[i].pageRank);
			}
			saveToDisk();
			/*System.out.println("--------------------------------------------------------------");
			for(int i=0;i<page.length;i++){
				
				System.out.println(page[i].toString());
			}
			//System.out.println(all+"--------------------------------------------------------------");
			*/
			/*int sta[]=new int[page.length];
			for(int i=0;i<sta.length;i++){
				double rankSum=0;
				for(int j=0;j<page.length;j++){
					sta[i]+=page[j].count(i);
					if(page[j].contain(i)) rankSum+=page[j].count(i)*page[j].pageRank;
				}
				System.out.println("There are "+sta[i]+" links pointing to page"+page[i].index+" The total rank is "+rankSum+"; "+page[i].pageRank);
			}*/
				
				

	}
	public static void main(String[] args) throws IOException {
		updatePageRank();// This will update all pagerank
		close();
		/*PageRelations.close();
		Indexer.close();
		TitleIndexer.close();
		WordLib.close();
		PageInfo.close();*/
		/*for(int id=0;id<600;id++){
		System.out.println(id+PageInfo.getPage(id).getUrl()+" "+ getRank(id));
		}*/
		/*int p=PageInfo.getURLID("https://en.wikipedia.org/wiki/List_of_Google_easter_eggs");
		PageFile pp=PageInfo.getPage(p);
			
		
			System.out.println(pp.getKeywords());*/
		
		

	}
	public static double PageRank(int ind,Page[] page){
		double result=1-d;
		for(int i=0;i<page.length;i++){			
			if(i!=ind&&page[i].contain(ind)){
				result+=(d*page[i].term());	
			}
		}
		return result;
	}
	
}
