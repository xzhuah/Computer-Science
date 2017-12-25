package com.hkust.comp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.htmlparser.util.ParserException;

import com.hkust.comp.urlPage.PageInfo;
import com.hkust.comp.pageRelation.PageRelations;
import com.hkust.comp.pagerank.PageRank;
import com.hkust.comp.urlword.Indexer;
import com.hkust.comp.urlword.TitleIndexer;
import com.hkust.comp.wordLib.WordLib;
import java.io.BufferedReader;
import java.io.FileReader;
public class SpiderTest {
	
	public static String dbroot="D:/db/";

	//public static int upperLimit=30; //Upper limit for pages
	//public static int maxdepth=2;
	public static long fetchpages(String root,int upperLimit) throws IOException, ParserException
	{
		
		// use Set to record which pages have been fetched this time
		Set<String> visited = new HashSet<>();
		// use Queue to implement BFS
		Queue<String> currQueue =  new LinkedList<>();
		
		currQueue.add(root);
		visited.add(root);

		// fetch the remaining
		String tmp;
		long time0=System.currentTimeMillis();
		for(int i=0;i<upperLimit;++i)
		{
			System.out.print("Handling "+i);
			long time1=System.currentTimeMillis();
			tmp = currQueue.poll();
			if(tmp==null)
			{
				break; //if the queue is empty, stop fetching since all the pages has been fetched
			}
			System.out.println(" "+tmp);
			PageInfo.addPage(tmp);
			int id = PageInfo.getURLID(tmp);
			PageFile pf = (PageFile)PageInfo.getPage(id);
			if(currQueue.size()<upperLimit-i) 
			{
				for(String j:pf.getChild())
				{
					if(!(visited.contains(j)))
					{
						currQueue.add(j);
						visited.add(j);
					}
				}
			}
			System.out.println(" "+(System.currentTimeMillis()-time1)+"ms");
			Main.progressBar.setValue((int)(i*100.0/upperLimit));
			Main.progressBar.setString((int)(i*100.0/upperLimit)+"%");
		}
		System.out.println("Finish prasing");
		long i=System.currentTimeMillis();
		System.out.println("Fetching used "+(i-time0)+"ms in total");
		return System.currentTimeMillis()-time0;
	}
	
	
	public static void main(String[] args) throws IOException, ParserException {
		fetchpages("http://www.cse.ust.hk/~ericzhao/COMP4321/TestPages/testpage.htm",300);
		
		long i=System.currentTimeMillis();
		PageInfo.writeRecordToFile(SpiderTest.dbroot+"spider_result.txt");
		i=System.currentTimeMillis()-i;
		System.out.println("Write String used "+i+"ms");
		PageRank.updatePageRank();// This will update all pagerank
		
		PageRelations.close();
		Indexer.close();
		TitleIndexer.close();
		WordLib.close();
		PageRank.close();
		PageInfo.close();
		

//		long i=System.currentTimeMillis();
//////		String s=PageInfo.getString();
//////		i=System.currentTimeMillis()-i;
//////		System.out.println("Get String used "+i/1000+"s");
//////		
//////		i=System.currentTimeMillis();
//////		new BufferedWriter(new FileWriter("D:/4321info.txt")).write(s);
////		PageInfo.writeRecordToFile("D:/4321info.txt");
////		i=System.currentTimeMillis()-i;
////		System.out.println("Write String used "+i/1000+"s");
	}
}