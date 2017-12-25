package com.hkust.comp;

import java.io.IOException;

import com.hkust.comp.urlPage.PageInfo;

public class PosItem{
		public int urlID;
		public WordRecord wordrecord;
		public PosItem(int urlID,int wordID) throws IOException{
			this.urlID=urlID;
			this.wordrecord=PageInfo.getPage(urlID).getWordRecordByWordID(wordID);
			
		}
		public String toString(){
			return this.urlID+": "+wordrecord.toString();
		}
		public static void main(String[] args) {
			
		}
	}