package com.hkust.comp;


import java.io.IOException;

import jdbm.RecordManagerFactory;
import jdbm.htree.HTree;

public class RevisedFile {

	/**
	 * @param args
	 */
	private HTree rfile;
	private int fileNum;
	private int removedNum;
	public static void main(String[] args) {
		
		
	}
	public RevisedFile() throws IOException{
		rfile=HTree.createInstance(RecordManagerFactory.createRecordManager("Revised File"));
		fileNum=0;
		fileNum=0;
	}
	/*public addFile(PageFile pagefile){
		
	}*/

}