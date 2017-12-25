package com.hkust.comp.pagerank;

import java.io.IOException;

import com.hkust.comp.PageFile;
import com.hkust.comp.pageRelation.PageChildren;
import com.hkust.comp.pageRelation.PageParent;
import com.hkust.comp.urlPage.PageInfo;

class Page{
		public int index;
		public Integer[] links;
		public double pageRank;
		public Page(int urlid) throws IOException{
			pageRank=1;
			this.index=urlid;
			this.links=PageChildren.getChildren(this.index);
			if(this.links==null) this.links=new Integer[0];
		}
		public String toString(){
			String result="";
			result+="Page"+index+" rank: "+this.pageRank+" contains "+links.length+" links: ";
			for(int i=0;i<links.length;i++){		
				result+=links[i]+" ";
			}			
			return result;
		}
		public double term(){
			return this.pageRank/this.links.length;
		}
		public boolean contain(int idx){
			for(int i=0;i<links.length;i++){		
				if(links[i]==idx) return true;
			}
			return false;
		}
		public int count(int id){
			
			int result=0;
			for(int i=0;i<this.links.length;i++){
				
				if(links[i]==id){
					result++;
				}
			}
			return result;
		}
		public static void main(String args[]) throws IOException{
			
			//Integer p[]=PageParent.getParents(1117);
			Integer pp[]=PageChildren.getChildren(9);
			String s=PageInfo.getPage(1086).getUrl();
			System.out.println(s);
		}
	}
