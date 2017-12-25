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

import com.hkust.comp.urlPage.PageInfo;

public class PageRelations
{
	static public void commit() throws IOException
	{
		PageParent.commit();
		PageChildren.commit();
	}
	static public void close() throws IOException
	{
		PageParent.commit();
		PageChildren.commit();
		PageParent.close();
		PageChildren.close();
	}
	//add a child for current ID and for that child add current ID as its parent
	//need to call commit() to commit change
	static public void addChild(int curr_id,int child_id) throws IOException
	{
		if(curr_id==child_id)return;
		PageChildren.addChild(curr_id, child_id);
		PageParent.addParent(child_id, curr_id);
	}
	
	//add a parent for current ID and for that parent add current ID as its child
	//need to call commit() to commit change
	static public void addParent(int curr_id,int parent_id) throws IOException
	{
		if(curr_id==parent_id)return;
		PageParent.addParent(curr_id, parent_id);
		PageChildren.addChild(parent_id, curr_id);
	}
	
	//delete a child for current ID and for that child remove current ID from its parents
	static public void delChild(int curr_id,int child_id) throws IOException
	{
		PageChildren.delChild(curr_id, child_id);
		PageParent.delParent(child_id, curr_id);
		PageChildren.commit();
		PageParent.commit();
	}
	
	//delete a parent for current ID and for that parent remove current ID from its children
	static public void delParent(int curr_id,int parent_id) throws IOException
	{
		PageParent.delParent(curr_id, parent_id);
		PageChildren.delChild(parent_id, curr_id);
		PageChildren.commit();
		PageParent.commit();
	}
	
	//add children for current ID and for each child add current ID as parent
	static public void addChildren(int curr_id,Vector<Integer> children) throws IOException
	{
		PageChildren.addChildren(curr_id, children);
		for(int i:children)
		{
			PageParent.addParent(i, curr_id);
		}
		PageChildren.commit();
		PageParent.commit();
	}
	
	//add parent for current ID and for each parent add current ID as child
	static public void addParents(int curr_id,Vector<Integer> parents) throws IOException
	{
		PageParent.addParents(curr_id, parents);
		for(int i:parents)
		{
			PageChildren.addChild(i, curr_id);
		}
		PageChildren.commit();
		PageParent.commit();
	}
	
	//Remove all relationships in database that URL_id involves
	static public void deletePage(int URL_id) throws IOException
	{
		Integer[] tmp;
		//get all children of current page
		tmp = PageChildren.getChildren(URL_id);
		//for each child, remove the current ID from its parent
		if(tmp!=null)
		{
			for(int i:tmp)
			{
				PageParent.delParent(i, URL_id);
			}
		}
		//delete the children database of current page
		PageChildren.delEntry(URL_id);
		
		//get all parent of current page
		tmp = PageParent.getParents(URL_id);
		//for each parent, remove the current ID from its children
		if(tmp!=null)
		{
			for(int i:tmp)
			{
				PageChildren.delChild(i, URL_id);
			}
		}
		//delete the parent database of current page
		PageParent.delEntry(URL_id);
		
		PageChildren.commit();
		PageParent.commit();
	}
	
	// Remove all the children of the given URL ID, used when update a webpage
	static public void deleteChildren(int URL_id) throws IOException
	{
		Integer[] tmp;
		//get all children of current page
		tmp = PageChildren.getChildren(URL_id);
		//for each child, remove the current ID from its parent
		if(tmp!=null)
		{
	
			for(int i:tmp)
			{
				PageParent.delParent(i, URL_id);
			}
		
		}	
		//delete the children database of current page
		PageChildren.delEntry(URL_id);
		
		PageChildren.commit();
		PageParent.commit();
	}
	static public Integer[] getChildren(int URL_id) throws IOException{
		return PageChildren.getChildren(URL_id);
	}
	static public Integer[] getParents(int URL_id) throws IOException{
		return PageParent.getParents(URL_id);
	}
	public static void main(String[] args) throws IOException
	{
		/*addParent(1,3);
		addParent(1,4);
		addParent(1,5);
		delParent(1,4);*/
		String u=PageInfo.getURLbyID(4);
		System.out.println(u);
		Integer i[]=PageRelations.getChildren(4);
		if(i==null){
			System.out.println("F");
		}else{
			for(int k=0;k<i.length;k++){
				System.out.print(i[k]+" ");
			}
		}
	}

}