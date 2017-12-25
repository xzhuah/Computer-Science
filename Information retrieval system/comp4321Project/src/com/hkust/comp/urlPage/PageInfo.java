package com.hkust.comp.urlPage;

import com.hkust.comp.PageFile;
import com.hkust.comp.Porter;
import com.hkust.comp.SpiderTest;
import com.hkust.comp.pageRelation.PageRelations;
import com.hkust.comp.urlword.Indexer;
import com.hkust.comp.urlword.TitleIndexer;
import com.hkust.comp.wordLib.WordLib;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.helper.FastIterator;
import jdbm.htree.HTree;
import org.htmlparser.util.ParserException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;

/*
 * This class stores URL to URLID tables 
 * Key: 
 * Values:
 * @Hang 
 */
public class PageInfo
{
	static private RecordManager recman;
	static private HTree urlid2File;
	static public HTree url2ID;
	static private HTree ID2url;
	static private final String DBNAME = "pageInfo";
	static private final String URL2PAGE = "url2Page";
	static private final String URL2ID = "url2ID";
	static private final String ID2URL = "ID2url";
	static private final String URLNUM = "http://urlNum"; // number of URLs  :( 
	static private int urlNumber = 0;
	static private int removedNum = 0;
	
	public static Porter porterWorker =  new Porter();
	
	private static final long ModifyTimeThread=-1;
	
	static{
		try {
			recman = RecordManagerFactory.createRecordManager(SpiderTest.dbroot+DBNAME);
			long recid1 = recman.getNamedObject(URL2PAGE);
			long recid2 = recman.getNamedObject(URL2ID);
			long recid3 = recman.getNamedObject(ID2URL);
				
			if (recid1 != 0){
				urlid2File = HTree.load(recman, recid1);
			}
			else{
				urlid2File = HTree.createInstance(recman);
				recman.setNamedObject(URL2PAGE, urlid2File.getRecid());
			} 
			if (recid2 != 0){
				url2ID = HTree.load(recman,recid2);
				
				urlNumber = (int)url2ID.get(URLNUM);
			}
			else{
				url2ID = HTree.createInstance(recman);
				recman.setNamedObject(URL2ID,url2ID.getRecid());
				
				url2ID.put(URLNUM,0);
			}
			if (recid3 != 0){
				ID2url = HTree.load(recman,recid3);
			}
			else{
				ID2url = HTree.createInstance(recman);
				recman.setNamedObject(ID2URL,ID2url.getRecid());
			}
			recman.commit();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	static public void commit() throws IOException{
		recman.commit();
	}
	static public void close() throws IOException
	{
		recman.commit();
		recman.close();
	}

	static private void doAddPage(String url,int id) throws IOException, ParserException
	{
		
		PageFile pf = new PageFile(url.toString());
		urlid2File.put(id,pf);
		recman.commit();
		
		//Add pageRelation and Indexer here
		addInfo(pf);
		
	}
	
	static public boolean addPage(String url) throws IOException, ParserException
	{
		
		if( url2ID.get(url) != null){
			// the url  exist, get the urlID 
			int id = (int)url2ID.get(url);
			if(urlid2File.get(id)==null){
				doAddPage(url,id);
				url2ID.put(URLNUM, PageInfo.getWordNum());
				return true;
	
			}
			else{
				//System.out.println("hit"+url);
				URLConnection conn=new URL(url).openConnection();
				Date lastModification=conn.getLastModified()==0?new Date(conn.getDate()):new Date(conn.getLastModified());/////lastModification
				PageFile pf = (PageFile) urlid2File.get(id);
				if(conn.getLastModified()!=0)
				{
					//page has LastModified date so that we can compare it and decide whether to update
					if(pf.getLastModification().getTime()-lastModification.getTime()<ModifyTimeThread)
					{
						//System.out.println("new: "+pf.getLastModification());
						//System.out.println("new: "+lastModification);
						PageRelations.deleteChildren(id);
						Indexer.delURLEntry(id);
						doAddPage(url,id);
						url2ID.put(URLNUM, PageInfo.getWordNum());
						return true;
					}
				}
						//System.out.println("hit "+url);
						return false;
			}
			
		}
		else{
			if(addURLID(url)){
				int id = (int)url2ID.get(url);
				addIDtoURL(url,id);
				doAddPage(url,id);
				url2ID.put(URLNUM, PageInfo.getWordNum());
				return true;
			}
			return false;
		}
	}
	
	//Add by ZHU Xinyu to add information to pageRelations and Indexer
	private static void addInfo(PageFile pf) throws IOException{
		Vector<String> temp = pf.getAllKeyword();
		int pageID=PageInfo.getURLID(pf.getUrl());
		for(int i=0;i<temp.size();i++){
			Indexer.addWordURLPosition(WordLib.getWordID(temp.get(i)),pageID,i);
			//System.out.println("Indexer: "+pageID+"->"+temp.get(i).getWordID()+" Position"+i+" word is "+WordLib.getWord(temp.get(i).getWordID()));
		}
		Indexer.saveToDisk();
		addTitleIndex(pf.getHeading(),pageID);
		
	}
	
	//Add by ZHU Xinyu to add Page title index
	private static void addTitleIndex(String title,int urlID) throws IOException{
		//System.out.println("What the title is: "+title+" UrlID:"+urlID);
		//System.out.println("title is "+title);
		title=title.replaceAll("[^a-zA-Z]", " ");
		StringTokenizer st=new StringTokenizer(title.toLowerCase());
		int i=0;//Position
		while(st.hasMoreTokens()){
			String word=st.nextToken();
			
			word = porterWorker.stripAffixes(word);
			
			int wordid=WordLib.getWordID(word);
			if(wordid!=-1){
				
				TitleIndexer.addWordURLPosition(wordid, urlID, i);
			}
			i++;
		}
		TitleIndexer.saveToDisk();
	}
	
	static public PageFile getPage(int urlID) throws IOException{
		if (urlid2File.get(urlID)!=null){
			PageFile pf = (PageFile)urlid2File.get(urlID);
			return pf;
		}
		return null;
	}
	
	
	static public boolean delPage(String url) throws IOException{
		try{
			if(urlid2File.get(url)!=null){
				urlid2File.remove(url);
				return true;
			}
			
		}catch(IOException e){
			e.printStackTrace();
		}
		return false;
	}

	static public int getURLID(String url) throws IOException{
		// get the URL from the HTree
		Object obj = url2ID.get(url);
		if(obj==null) return -1;
		int tmp = (int) obj;
		return tmp;
 	}

	static public boolean addURLID(String url) throws IOException{
		long time1=System.currentTimeMillis();
		if(url2ID.get(url)==null){
			if(System.currentTimeMillis()-time1>8000)
			{
				System.out.print("serach:"+(System.currentTimeMillis()-time1)+"ms  ");
				System.out.println(url);
			}
			url2ID.put(url,urlNumber++);
			//recman.commit();
			return true;
		}
		return false;	
	}

	static public void addIDtoURL(String url,int id) throws IOException
	{
			ID2url.put(id,url);
			//recman.commit();
	}
	
	static public void delIDtoURL(int id) throws IOException
	{
		ID2url.remove(id);
	}
	
	static public String getURLbyID(int id) throws IOException
	{
		if(ID2url.get(id)==null)return null;
		else return  ID2url.get(id).toString();
	}
	
	static public boolean delURL(String url) throws IOException{
		try{
			if ( url2ID.get(url)!=null){
				url2ID.remove(url);
				removedNum ++ ;
				return true;
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	public static int getWordNum() {
		return urlNumber - removedNum;
	}

	public static String getString(){
		String result = "";
		try{
			FastIterator it = urlid2File.keys();
			Object setKey = it.next();
			while(setKey!=null){
				result+=(int)setKey+" -> ";
				PageFile pf = (PageFile)urlid2File.get(setKey);
				result+= pf.toString();
				setKey = it.next();
			}
		}catch(IOException e){
			e.printStackTrace();
			return "ERROR";
		}
		return result;
	}
	public static Vector<Integer> getAllCrawledID() throws IOException{
		Vector<Integer> result=new Vector<Integer>();
		FastIterator it = urlid2File.keys();
		Object setKey = it.next();
		while(setKey!=null){
			result.add((int)setKey);
			setKey=it.next();
		}
		return result;
	}
	public static Vector<Integer> getAllID() throws IOException{
		Vector<Integer> result=new Vector<Integer>();
		FastIterator it = ID2url.keys();
		Object setKey = it.next();
		while(setKey!=null){
			result.add((int)setKey);
			setKey=it.next();
		}
		return result;
	}
	
	public static void writeRecordToFile(String file) throws IOException{
		
		BufferedWriter bw=new BufferedWriter(new FileWriter(file));
		try{
			FastIterator it = urlid2File.values();
			Object setKey = it.next();
			while(setKey!=null){
				//long time=System.currentTimeMillis();
				//System.out.print("Writing "+i++);
				String result="";
				//result+=(int)setKey+" -> ";
				
				result+= ((PageFile)setKey).toString();
				result+="-------------------------------------------------------------------------------------------\r\n";
				bw.write(result);
				//System.out.println(" "+(System.currentTimeMillis()-time)+"ms");
				setKey = it.next();
			}
		}catch(Exception e){
			e.printStackTrace();
			
		}finally{
			bw.close();
		}
		
	}
	public static void main(String[] args) throws IOException{
		/*String url = "http://www.cse.ust.hk/";
		String url2 = "http://www.ust.hk/";
		
	
		//PageInfo.addPage(new URL("http://www.ust.hk/prospective-students/"));
		//System.out.println(PageInfo.getPage(PageInfo.getURLID(new URL("http://www.ust.hk/prospective-students/"))));
		PageInfo.addPage(url);
		PageInfo.addPage(url2);
		int id = PageInfo.getURLID(url);
		int id2 = PageInfo.getURLID(url2);
		commit();
		System.out.println(id+"   "+id2);
		PageFile pf = (PageFile)PageInfo.getPage(id);
		System.out.print(pf.toString());
		PageFile pf2 = (PageFile)PageInfo.getPage(id2);
		System.out.print(pf2.toString());
		System.out.print(PageInfo.getString());
		//System.out.println(pi.getURLID(new URL("http://join.ust.hk/index.html")));
		commit();
		int childID=PageInfo.getURLID("http://www.ust.hk/faculty-staff/");
		System.out.println("Find child id :"+childID);
		Integer[] pa=PageParent.getParents(childID);
		PageFile page = PageInfo.getPage(pa[0]);
		System.out.println(page.getChild());
		System.out.println(page.getKeywords());*/
		System.out.println();

	}
}