package com.hkust.comp.pageRelation;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.htree.HTree;
import jdbm.helper.FastIterator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Vector;
import java.io.IOException;
import java.io.Serializable;

import com.hkust.comp.SpiderTest;

/*
 * This class stores parent of a page
 * Key: ID of a URL
 * Values: HashSet contains all page ID which have link to this page
 */

public class PageParent
{
	static private RecordManager recman;
	static private HTree hashtable;
	static private String dbname = "Parent";

	static
	{
		try 
		{
			recman = RecordManagerFactory.createRecordManager(SpiderTest.dbroot+dbname);
			long recid = recman.getNamedObject(dbname);
				
			if (recid != 0)
				hashtable = HTree.load(recman, recid);
			else
			{
				hashtable = HTree.createInstance(recman);
				recman.setNamedObject(dbname, hashtable.getRecid() );
			} 
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	static public void commit() throws IOException
	{
		recman.commit();	
	}
	static public void close() throws IOException
	{
		recman.commit();
		recman.close();
	}
	static public void addParent(int URL_id,int parent_id) throws IOException
	{
		if(hashtable.get(URL_id)==null)
		{
			HashSet<Integer> tmp = new HashSet<Integer>();
			tmp.add(parent_id);
			hashtable.put(URL_id, tmp);
		}
		else
		{
			HashSet<Integer> tmp = (HashSet<Integer>) hashtable.get(URL_id);
			tmp.add(parent_id);
			hashtable.put(URL_id, tmp);
		}
		
	}
	
	static public void addParents (int URL_id, Vector<Integer> parents) throws IOException
	{
		if(hashtable.get(URL_id)==null)
		{
			HashSet<Integer> tmp = new HashSet<Integer>();
			tmp.addAll(parents);
			hashtable.put(URL_id, tmp);
		}
		else
		{
			HashSet<Integer> tmp = (HashSet<Integer>) hashtable.get(URL_id);
			tmp.addAll(parents);
			hashtable.put(URL_id, tmp);
		}
		
	}
	
	static public void delEntry(int URL_id) throws IOException
	{
		hashtable.remove(URL_id);
		
	}
	
	static public void delParent(int URL_id, int parent_id) throws IOException
	{
		HashSet<Integer> tmp = (HashSet<Integer>) hashtable.get(URL_id);
		tmp.remove(parent_id);
		hashtable.put(URL_id, tmp);
		
	}

	static public Integer[] getParents(int URL_id) throws IOException
	{
		Object obj=hashtable.get(URL_id);
		if(obj==null) return null;
		HashSet<Integer> tmp = (HashSet<Integer>) obj;
		
		Integer[] a;
		a=tmp.toArray(new Integer[tmp.size()]);
		return a;
	}
	
	public static void main(String[] args) throws IOException
	{
        FastIterator iter = hashtable.keys();

        Object key;
        while( (key = iter.next())!=null)
        {
        	Integer[] tmp = getParents((int) key);
        	String tmpstr = new String();
        	for(Integer i:tmp)
        	{
        		tmpstr += i+" ";
        	}
        	// get and print the content of each key
        	System.out.println(key + " : " + tmpstr);
        }
	}
}