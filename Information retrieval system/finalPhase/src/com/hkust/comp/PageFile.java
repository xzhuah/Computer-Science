package com.hkust.comp;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;

import jdbm.RecordManagerFactory;
import jdbm.helper.FastIterator;
import jdbm.htree.HTree;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.beans.LinkBean;
import org.htmlparser.beans.StringBean;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.ParserException;

import com.hkust.comp.pageRelation.PageRelations;
import com.hkust.comp.urlPage.PageInfo;
import com.hkust.comp.wordLib.WordLib;

public class PageFile implements Serializable {

	private String title;
	private String url;
	private Date lastModification;
	private int size;
	private Vector<WordRecord> keywords;
	private Vector<String> Allkeyword;
	private String heading;
	public static Porter porterWorker =  new Porter();
	public static void main(String args[]) throws ParserException, IOException{
		PageFile p=new PageFile("http://www.cse.ust.hk/~ericzhao/COMP4321/TestPages/Movie.htm");
		//System.out.println("I am Here");
	}
	public PageFile(){
		keywords=new Vector<WordRecord>();
		Allkeyword=new Vector<String>();
		
	}
	public PageFile(String url) throws IOException, ParserException{
		this();
		Allkeyword=new Vector<String>();
		long time=System.currentTimeMillis();
		this.url=url;						///////////url
		
		URL u=new URL(this.url);
		URLConnection conn=u.openConnection();
		conn.setConnectTimeout(3000);
		String contents=Tools.getHtmlContent(u);
		this.size=conn.getContentLength()==-1?contents.length():conn.getContentLength(); /////size
		this.lastModification=conn.getLastModified()==0?new Date(conn.getDate()):new Date(conn.getLastModified());/////lastModification
		/////
		//Get heading
		/*contents=contents.replace("<h2","<h1");
		contents=contents.replace("</h2>","</h1>");
		contents=contents.replace("<h3","<h1");
		contents=contents.replace("</h3>","</h1>");*/
		int headingPosi=contents.indexOf("<h1");
		while(headingPosi!=-1){
			try{
			int end=contents.indexOf("</h1>");
			if(end>headingPosi){
				//System.out.println(contents.substring(headingPosi+4,end));
				this.heading+=contents.substring(headingPosi+4,end);
			}
			contents=contents.substring(end+5,contents.length());
			headingPosi=contents.indexOf("<h1");
			}catch(Exception e){
				break;
			}
			
		}
		//////
		System.out.println("Get request"+(System.currentTimeMillis()-time));
		time=System.currentTimeMillis();
		//////
		
		this.title=Tools.getHtmlTitle(u);  ///////////Title
		
		//////
		System.out.println("Get title"+(System.currentTimeMillis()-time));
		time=System.currentTimeMillis();
		//////
		
		 LinkBean lb=new LinkBean();
		 StringBean sb=new StringBean();
		 lb.setURL(url);
		 sb.setURL(url);
		 URL urls[]=lb.getLinks();
		 Vector<String> child=new Vector<String>();
		//////
			System.out.println("Get links"+(System.currentTimeMillis()-time));
			time=System.currentTimeMillis();
			//////
		 for(int i=0;i<urls.length;i++){
			 if(!urls[i].toString().contains("#")){
				 child.add(urls[i].toString());
			 }else{
				 child.add(urls[i].toString().substring(0,urls[i].toString().lastIndexOf("#")));
			 }
			
		 }											///////////Child
		int curr_id=PageInfo.getURLID(this.url);
		 for(int i=0;i<child.size();i++){
			 String childurl=child.get(i);
			 PageInfo.addURLID(childurl);
			 int childid = (int)PageInfo.url2ID.get(childurl);
			 PageInfo.addIDtoURL(childurl,childid);
			 PageRelations.addChild(curr_id, childid);
			 
		 }
			//////
			System.out.println("Add info"+(System.currentTimeMillis()-time));
			time=System.currentTimeMillis();
			//////
		//Key word list
		Vector<String> words=new Vector<String>();  //Get words into this vector
		String pageContent=sb.getStrings();
		StringTokenizer st=new StringTokenizer(pageContent);
		while(st.hasMoreTokens()){
			String word=st.nextToken();
			
			word = porterWorker.stripAffixes(word);
			
			WordLib.addWord(word);
			words.add(word);
			Allkeyword.add(word);
		}
		
		WordLib.commitChange();
		
		//words vector ready
		
		//The following code is to count the word number for each keyword and add new keyword(those word not in stopwords list) into WordLib
		HTree ht=HTree.createInstance(RecordManagerFactory.createRecordManager("wordRecord"));//The HTree word ID->number
		
		
		for(int i=0;i<words.size();i++){
			
			if(ht.get(WordLib.getWordID(words.get(i)))==null){ //A Word that we haven't encounted before
				
	
				if(WordLib.getWordID(words.get(i))!=-1){   //Already in WordLib
				
					ht.put(WordLib.getWordID(words.get(i)), 1); //Count that
				}
				
			}else{
				
				ht.put(WordLib.getWordID(words.get(i)), (int)ht.get(WordLib.getWordID(words.get(i)))+1);// update
			}
		}
		
		FastIterator it= ht.keys();
		int word;
		Object oo=it.next();
		word=(oo==null?-1:(int)oo);
		System.out.println("remaining "+(System.currentTimeMillis()-time));
		time=System.currentTimeMillis();
		//word is word id here
		while(word!=-1){			
			this.addKeyword(word, (int)ht.get(word));
			Object oob=it.next();
			word=(oob==null?-1:(int)oob);
		}
		//Key word list ready  								//////keywords
		
	//////
				System.out.println("Add keywords "+(System.currentTimeMillis()-time));
				time=System.currentTimeMillis();
				//////
	}
	public Vector<String> getAllKeyword(){
		return Allkeyword;
	}
	public String getTitle() {
		return title;
	}
	public String getHeading(){
		return this.title+" "+this.heading;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Date getLastModification() {
		return lastModification;
	}

	public void setLastModification(Date lastModification) {
		this.lastModification = lastModification;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Vector<WordRecord> getKeywords() {
		return keywords;
	}

	public void setKeywords(Vector<WordRecord> keywords) {
		for(int i=0;i<keywords.size();i++){
			//System.out.println(keywords.get(i).getWordID());
			this.keywords.add((WordRecord)keywords.get(i));
		}
		
	}
	public void addKeyword(String word,int frequency){
		int id = WordLib.getWordID(word.trim());
		addKeyword(id,frequency);
	}
	public void addKeyword(int wordid,int frequency){
		if(wordid!=-1){//Sorted
			//keywords.add(new WordRecord(wordid,frequency));
			keywords.insertElementAt(new WordRecord(wordid,frequency), find(frequency));
		}
	}
	//1 
	//5 4 1
	private int find(int frequency) {  
		for(int i=0;i<keywords.size();i++){
			if(frequency>=keywords.get(i).getFrequency()) return i;
		}
		return keywords.size();
		
        /*int start = 0;  
        int end = this.keywords.size() - 1;
        
        int index=0;  
  
        while (true){
        	
            index = (start + end) / 2; 
          //  System.out.println(index+" "+start+" "+end);
            int tempFre=keywords.get(index).getFrequency();
            if (tempFre==frequency) {  
                return index;  
            } else if (tempFre>frequency){
            	start=index+1;         	
            } else{
            	end=index;
            }
            if(start>=end) return index;
        }  */
       
    }  
	public WordRecord getWordRecordByWordID(int WordID){
		for(int i=0;i<this.keywords.size();i++){
			WordRecord wr=this.keywords.get(i);
			if(wr.getWordID()==WordID){
				return wr;
			}
		}
		return null;
	}
	public Vector<String> getChild() throws IOException {
		// TODO Auto-generated method stub
		Vector<String> result=new Vector<String>();
		int id=PageInfo.getURLID(this.url);
		Integer[] ch = PageRelations.getChildren(id);
		if (ch==null) return result;
		for(int i=0;i<ch.length;i++){
			result.add(PageInfo.getURLbyID(ch[i]).toString());
		}		
		return result;
	}

	@Override
	public String toString() {
		String result= this.title+"\r\n"+this.url+"\r\n"+this.lastModification.toString()+","+this.size+"\r\n";
		
		//long time=System.currentTimeMillis();
		for(int i=0;i<keywords.size();i++){
			result+=(keywords.get(i).toString()+";");
		}
		//System.out.println("Search word used "+(System.currentTimeMillis()-time)+" ms");
		result+="\r\n";
		try {
			Integer[] child;
			child = PageRelations.getChildren((PageInfo.getURLID(this.url)));
			if(child!=null) {
			
				for(int i=0;i<child.length;i++){
					
					result+=(PageInfo.getURLbyID(child[i])+"\r\n");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return result;
				
	}
	/*public static void main(String args[]){
		PageFile p=new PageFile("http://www.cse.ust.hk/~ericzhao/COMP4321/TestPages/Movie.htm");
		
	}*/
	
	

}