package com.hkust.comp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;

import org.htmlparser.beans.StringBean;

import com.hkust.comp.pageRelation.PageParent;
import com.hkust.comp.pagerank.PageRank;
import com.hkust.comp.urlPage.PageInfo;
import com.hkust.comp.urlword.InvertedIndexer;
import com.hkust.comp.wordLib.WordLib;

public class Tools {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static String getHtmlTitle(URL url) throws IOException{
		try{
			String result = "";
			URLConnection conn=url.openConnection();
			conn.setConnectTimeout(3000);
			BufferedReader bif=new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
			String element=bif.readLine();
			
			while(element!=null){
				result+=element;
				
				if(result.contains("<title>")&&result.contains("</title>")){
					String title=result.substring(result.indexOf("<title>")+7,result.indexOf("</title>"));
					if(title.equals("302 Found")){
						if(url.toString().contains("https")){
							return "NONE";
						}else{
							url=new URL(url.toString().replaceFirst("http", "https"));
							return getHtmlTitle(url);
						}
					}else{
						return title;
					}
					
				}
				element=bif.readLine();
			}
			
			return "NONE";
			}catch(Exception e){
						//Need Login   TODO
				return "NONE"; 
			}
	}
	public static String getHtmlContent(URL url){
	    
	    try{
	      String result = "";
	      URLConnection conn=url.openConnection();
	      conn.setConnectTimeout(3000);
	      BufferedReader bif=new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
	      String element=bif.readLine();
	      
	      while(element!=null){
	        result+=element;
	        element=bif.readLine();
	      }
	      if(result.toLowerCase().contains("<title>302 found</title>")){
	        if(url.toString().contains("https")) return "";
	        else{
	          url=new URL(url.toString().replaceFirst("http", "https"));
	          return getHtmlContent(url);
	        }
	      }else{
	        return result;
	      }
	    
	      }catch(Exception e){
	            
	        return ""; 
	      }
	  }

	  public static String getProfile(String url){
	    try{
	      String result="";
	       StringBean sb=new StringBean();
	       sb.setURL(url);
	      
	       result= sb.getStrings();
	       return result;
	    }catch(Exception e){
	      return "Unavailable";
	    }

	  }
	  public static String ExtractInfo(PageFile page,double score,boolean needProfile,Vector<String> queryWord){
	      String pagetitle=page.getTitle().toLowerCase();
	      for(int i=0;i<queryWord.size();i++){
	        String keyword=queryWord.get(i);
	         if(keyword.length()>=4&&!keyword.equals("span")&&!keyword.equals("class")){
	        	 pagetitle=pagetitle.replace(keyword,"<span class=\"key\">"+keyword+"</span>");
	         }
	       
	         
	      }
	      String pageurl=page.getUrl();
	      double pagerank=0;
	    try {
	      pagerank=PageRank.getRank(PageInfo.getURLID(pageurl));
	    } catch (IOException e1) {
	      // TODO Auto-generated catch block
	      e1.printStackTrace();
	    }
	      String lastModification=page.getLastModification().toString();
	      String sizeofpage=page.getSize()+"";
	      String keywordList="";
	      Vector<WordRecord> words=page.getKeywords();
	      for(int i=0;i<(words.size()>5?5:words.size());i++){
	        keywordList+=words.get(i).toString()+";&nbsp;&nbsp;&nbsp;";
	      }
	      String parentLink="";
	    
	      Integer pid[];
	    String pageID="";
	    String profileInfo="";
	    if(needProfile){
	      profileInfo=getProfile(pageurl).toLowerCase();
	      profileInfo=profileInfo.replaceAll("[^0-9a-zA-Z\u4e00-\u9fa5.£¬,¡£?\n' ']+", "");
	      profileInfo=profileInfo.replaceAll("\n"," ");
	       int a=0;
	       try{
	        a=profileInfo.indexOf(words.get(0).toString().substring(0,words.get(0).toString().indexOf(" ")));
	        //profileInfo=words.get(0).toString().substring(0,words.get(0).toString().indexOf(" "));
	        profileInfo=profileInfo.substring(a,a+300);
	        }catch(Exception e){

	        }
	      profileInfo+="...";
	      for(int i=0;i<queryWord.size();i++){
	      String keyword=queryWord.get(i);
	       if(keyword.length()>=4&&!keyword.equals("span")&&!keyword.equals("class"))
	        profileInfo=profileInfo.replace(queryWord.get(i).toString(),"<span class=\"key\">"+keyword+"</span>");
	        
	      }
	    }
	    
	      try {
	      pageID=PageInfo.getURLID(pageurl)+"";
	        pid = PageParent.getParents(PageInfo.getURLID(pageurl));
	        if(pid==null) pid=new Integer[0];
	      } catch (IOException e) {
	        // TODO Auto-generated catch block
	        pid=new Integer[0];
	        e.printStackTrace();
	      }
	      for(int i=0;i<pid.length;i++){
	        String url;
	        try {
	          url = PageInfo.getURLbyID(pid[i]);
	        } catch (IOException e) {
	          
	          url="ERROR ID: "+pid[i];
	        }
	        parentLink+="<a href=\""+url+"\" onclick=\"sentFeedBack(\'"+pageurl+"\');\">"+url+"</a><br>";
	      }
	      String childList="";
	      Vector<String> child;
	      try {
	        child=page.getChild();
	      } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	        child=new Vector<String>();
	      }
	      for(int i=0;i<child.size();i++){
	        String childu=child.get(i);
	        childList+="<a href=\""+childu+"\">"+childu+"</a><br>";
	      }
	      String result="<div class=\"resultUnit\">" +
	          "<!-- PopPart-->" +
	          "<div class=\"courseinfo\">" +
	          "<div class=\"popup\">" +
	          "<h3 class=\"pageTitle\"><a href=\""+pageurl+"\" class=\"pageLink\" >"+pagetitle+" ("+score+")</a></h3>" +
	          "<div class=\"page_url\"><a class=\"page_url\" href=\""+pageurl+"\" >"+pageurl+"</a></div>" +
	          "<div class=\"popupdetail\">" +
	          "<a href=\"search.jsp?similar="+pageID+"\">View similar page</a><br>"+  "<a href=\"search.jsp?similar="+pageID+"&detail=true\">View similar page with detail</a><br>" +"<div class=\"pagerank\">Page Rank: "+pagerank+"</div><br>"+profileInfo+
	          "</div>" +
	          "</div> " +
	          "</div>" +
	          "</div>" +
	          "<!-- PopPart END-->" +
	          "<div class=\"infomation\">Last Modified At: " +
	          lastModification+", Size of this page:"+sizeofpage +
	          "</div>" +
	          "<div class=\"profile\">"+keywordList+"<br>" +"<div class=\"parentLink\" id=\"parent"+pageID+"\">Parent Links</div><div class=\"parentlist\" id=\"parentlist"+pageID+"\">"+parentLink+"</div><div class=\"childLink\" id=\"child"+pageID+"\">Child Links</div><div class=\"childlist\" id=\"childlist"+pageID+"\">"+childList+"</div></div></div>";
	      
	      result+="<script>$(\"#parent"+pageID+"\").click(function(){$(\"#parentlist"+pageID+"\").slideToggle(1000)});$(\"#child"+pageID+"\").click(function(){$(\"#childlist"+pageID+"\").slideToggle(1000)});</script>";
	      
	      return result;
	   }
	public static HashMap<Integer,Integer> getHashQuery(int pageid){
		HashMap<Integer,Integer> result=new HashMap<Integer,Integer>();
		try {
			Vector<WordRecord> temp = PageInfo.getPage(pageid).getKeywords();
			for(int i=0;i<(temp.size()>=5?5:temp.size());i++){
				WordRecord record = temp.get(i);
				result.put(record.getWordID(),record.getFrequency());
			}
		} catch (IOException e) {
			
			return result;
		}
		return result;
	}
	//Stop remove, stem
	public static HashMap<Integer,Integer> getHashQuery(String input){
		input=input.replace("[^0-9a-zA-Z]"," ");
		HashMap<Integer,Integer> result=new HashMap<Integer,Integer>();
		StringTokenizer st=new StringTokenizer(input);
		Porter helper=new Porter();
		while(st.hasMoreTokens()){
			String word=st.nextToken();
			word=helper.stripAffixes(word);
			int wordid=WordLib.getWordID(word);
			if(wordid!=-1){
				if(result.get(wordid)!=null){
					result.put(wordid,result.get(wordid)+1);
				}else{
					result.put(wordid, 0);
				}
			}

		}
		return result;	
	}
	public static HashMap<Integer,Integer> getWordVector(String input){
		HashMap<Integer,Integer> result=new HashMap<Integer,Integer>();
		StringTokenizer st=new StringTokenizer(input);
		Porter stem=new Porter();
		while(st.hasMoreTokens()){
			String next=st.nextToken();
			next=stem.stripAffixes(next);
			int id=WordLib.getWordID(next);
			if(id!=-1){
				if(result.get(id)!=null){
					result.put(id, result.get(id)+1);
				}else{
					result.put(id, 1);
				}
			}
		}
		return result;	
	}
	public static HashMap<Vector<Integer>,Integer> getPhraseVector(String input){
		if(input.indexOf("\"")==input.lastIndexOf("\"")) return null;//0 or 1 "
		int phraseNumber=(input.length()-input.replaceAll("\"", "").length())/2;
		input=input.replace("\"", " \" ");
		input=input.substring(input.indexOf("\"")+1,input.lastIndexOf("\"")+1);
		Porter stem=new Porter();
		Vector<Vector<Integer>> query=new Vector<Vector<Integer>>();	
		for(int i=0;i<phraseNumber;i++){
			StringTokenizer st=new StringTokenizer(input);
			Vector<Integer> phrases=new Vector<Integer>();
			String next=st.nextToken();
			while(!next.equals("\"")){		
				next=stem.stripAffixes(next);
				int id=WordLib.getWordID(next);
				
				if(id!=-1){
					phrases.add(id);
				}
				if(st.hasMoreTokens()){
					next=st.nextToken();
				}else{
					break;
				}
			}
			input=input.replaceFirst("\"", "");
			input=input.substring(input.indexOf("\"")+1,input.lastIndexOf("\"")+1);
			if(phrases.size()!=0){
				query.add(phrases);
			}
		}
		HashMap<Vector<Integer>,Integer> result=new HashMap<Vector<Integer>,Integer>();
		for(int i=0;i<query.size();i++){
			if(result.get(query.get(i))==null){
				result.put(query.get(i), 1);
			}else{
				result.put(query.get(i), result.get(query.get(i))+1);
			}
		}
		return result;
		
	}
	 public static String getAllWord(double pagerank,double freq){
          String result="<div class=\"wordarea\">";
         
          HashSet<String> set=new HashSet<String>();
         for(int i=0;i<50;i++){
             try{
            	 if(PageRank.getRank(i)>=pagerank){
            		 Vector<WordRecord> temp = PageInfo.getPage(i).getKeywords();
            		 for(int j=0;j<temp.size()*freq;j++){
            			 set.add(WordLib.getWord(temp.get(j).getWordID()));
            		 }
            	 }
             }catch(Exception e){
            	 
             }
         }
        Iterator<String> it = set.iterator();
        while(it.hasNext()){
        	String word=it.next();
        	 result+="<a onclick=addTosearch(\""+word+"\")>"+word+"</a>";
        }
        
         result+="</div>";
         return result;
   }
	 
	public static void main(String args[]) throws IOException{
		/*System.out.println(getHtmlContent(new URL("http://www.cse.ust.hk/ug/hkust_only/4yr/")));
		System.out.println(getHtmlTitle(new URL("http://www.cse.ust.hk/ug/hkust_only/4yr/")));
		//System.out.println(PageInfo.getPage(5));
		System.out.println(Tools.ExtractInfo(PageInfo.getPage(1),5, false, null));*/
	/*	String s="\"Computer\"\"Computer Science\"   Department of \"Computer Science\" and \"Happy birthday\" as well as \"Computer Science\" \"  \" \" word record \"  a big like lime computer science \"Computer Science\"   ";
		HashMap<Vector<Integer>,Integer> h=getPhraseVector(s);
		if(h!=null)
		System.out.println(h);
		
		HashMap<Integer,Integer> hh=getWordVector(s.replace("\"", " "));
		if(hh!=null)
			System.out.println(hh);*/
		//System.out.print(getAllWord(0.5));
		//HashMap<Integer, Integer> a = InvertedIndexer.getURLTfByWordID(WordLib.getWordID(new Porter().stripAffixes("database")));
		PageFile p1=PageInfo.getPage(1);
		System.out.println(p1.getTitle());
		System.out.println(Tools.ExtractInfo(p1, 1, true, new Vector<String>()));
		
	}
}
