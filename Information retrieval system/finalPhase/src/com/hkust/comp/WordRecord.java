package com.hkust.comp;

import java.io.IOException;
import java.io.Serializable;

import com.hkust.comp.wordLib.WordLib;


public class WordRecord implements Serializable{

	/**
	 * @param args
	 */
	private int wordID;
	private int frequency;
	
	public WordRecord(int wordID, int frequency) {
		super();
		this.wordID = wordID;
		this.frequency = frequency;
	}
	
	public int getWordID() {
		return wordID;
	}
	public void setWordID(int wordID) {
		this.wordID = wordID;
	}
	public int getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public String toString() {
		return (String)WordLib.getWord(wordID)+" "+frequency;
	}

}