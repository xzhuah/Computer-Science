package com.hkust.comp.wordLib;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import com.hkust.comp.SpiderTest;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.helper.FastIterator;
import jdbm.htree.HTree;

public class WordLib {

	/**
	 * @param args
	 */
	private static RecordManager recman = null;
	private final static String DBNAME = "Word";
	private static HTree idCountTree;
	private static HTree stopLib;
	private static HTree keyLib;
	private static HTree wordLib;//@by ZHU
	private final static String IDCOUNT = "KeyNumber";	// The name of the table storing current number of key
	private final static String IDCOUNTKEYNAME = "NUMBER";	//The key for the stored number of key in table
	private final static String KEYTABLE = "Keyword";
	private final static String STOPTABLE = "Stopword";
	private final static String WordTable="Word";//@by ZHU
	private final static String stopWordPath = SpiderTest.dbroot+"stopwords.txt";
	private static int wordNumber = 0;
	private static int removedNum = 0;

	static {
		// Initialize WordLib and tables
		try{
			recman = RecordManagerFactory.createRecordManager(SpiderTest.dbroot+DBNAME);
			long recid1 = recman.getNamedObject(KEYTABLE);
			long recid2 = recman.getNamedObject(STOPTABLE);
			long recid3 = recman.getNamedObject(IDCOUNT);
			long recid4 = recman.getNamedObject(WordTable);//@by ZHU
			// Setting up all the tables in db
			if(recid1!=0){
				keyLib = HTree.load(recman, recid1);
			}else{
				keyLib = HTree.createInstance(recman);
				recman.setNamedObject(KEYTABLE, keyLib.getRecid());
			}
			//@by ZHU
			if(recid4!=0){
				wordLib = HTree.load(recman, recid4);
			}else{
				wordLib = HTree.createInstance(recman);
				recman.setNamedObject(WordTable, wordLib.getRecid());
			}
			if(recid2!=0){
				stopLib = HTree.load(recman, recid2);
			}else{
				stopLib = HTree.createInstance(recman);
				recman.setNamedObject(STOPTABLE, stopLib.getRecid());
				BufferedReader bur = new BufferedReader(new FileReader(stopWordPath));
				String word = bur.readLine();
				int num = 0;
				while(word!=null){
					stopLib.put(word.trim(), num++);
					word = bur.readLine();
				}
				bur.close();
			}
			if(recid3!=0){
				idCountTree = HTree.load(recman, recid3);
				wordNumber = (int)idCountTree.get(IDCOUNTKEYNAME);
			}else{
				idCountTree = HTree.createInstance(recman);
				recman.setNamedObject(IDCOUNT, idCountTree.getRecid());
				idCountTree.put(IDCOUNTKEYNAME, 0);
			}
			recman.commit();
		}catch (IOException e) {
			//System.out.println(e.getMessage());
		}
	}

	
	private static boolean checkCNChar(char oneChar) {
		if ((oneChar >= '\u4e00' && oneChar <= '\u9fa5') || (oneChar >= '\uf900' && oneChar <= '\ufa2d'))
			return true;
		return false;
	}

	private static String deleteCNChar(String source) {
		
		char[] cs = source.toCharArray();
		int length = cs.length;
		char[] buf = new char[length];
		for (int i = 0; i < length; i++) {
			char c = cs[i];
			if (!checkCNChar(c)) {
				buf[i] = c;
			}
			// problem here, what if is CN char then i also increase and buf[i]
			// do not have any value
		}
		String ret = new String(buf);
		return ret.trim();
	}
	/**
	 * 
	 * @param word
	 *            The word to add
	 * @return whether succeed
	 * @throws IOException
	 */
	public static boolean addWord(String word) throws IOException {
		word = word.toLowerCase().trim();
		word = deleteCNChar(word);// remove chinese
		word = word.replaceAll("\\pP|\\pS", "");
		word = word.replaceAll("[^A-Za-z0-9 ]", "");
		word = word.replaceAll("\\d+", "");
		if (word.equals("")){
			return false;
		}
		// If the word is either a stop word or already in the lib, ignore
		if (stopLib.get(word) != null || keyLib.get(word) != null){
			return false;
		}	
		keyLib.put(word, wordNumber);
		wordLib.put(wordNumber++, word);//@by ZHU	
		idCountTree.put(IDCOUNTKEYNAME,getWordNum());//@by ZHU	
		return true;
	}
	
	//@by ZHU	
	public static void synchronize() throws IOException{
		Vector<Integer> wordID=getAllID();
		Vector<String> word=getAllWord();
		for(int i=0;i<wordID.size();i++){
			keyLib.put(getWord(wordID.get(i)), wordID.get(i));
		}
		for(int i=0;i<word.size();i++){
			wordLib.put(getWordID(word.get(i)),word.get(i));
		}
		wordID=getAllID();
		wordNumber=wordID.size();
		removedNum=0;
		idCountTree.put(IDCOUNTKEYNAME,getWordNum());
		recman.commit();
	}
	//@by ZHU	
	public static Vector<Integer> getAllID(){
		Vector<Integer> result=new Vector<Integer>();
		FastIterator it;
		try {
			it = wordLib.keys();
			Object word=it.next();
			while(word!=null){
				result.add((int)word);
				word=it.next();
			}
		} catch (IOException e) {
			return null;
		}
		return result;
	}
	//@by ZHU	
	public static Vector<String> getAllWord(){
		Vector<String> result=new Vector<String>();
		FastIterator it;
		try {
			it = keyLib.keys();
			Object word=it.next();
			while(word!=null){
				result.add(word.toString());
				word=it.next();
			}
		} catch (IOException e) {
			return null;
		}
		return result;
	}
	/**
	 * 
	 * @param words
	 *            a vector of words
	 * @throws IOException
	 */
	public static void addWords(Vector<String> words) throws IOException {
		for (int i = 0; i < words.size(); i++)
			addWord((String) words.get(i));
		recman.commit();
	}

	/**
	 * 
	 * @param path
	 *            the file path
	 * @throws IOException
	 */
	public static void addFromFile(String path) throws IOException 
	{
		BufferedReader buf = new BufferedReader(new FileReader(path)); 
		String word = buf.readLine();
		while (word != null) 
		{
			addWord(word);
			word = buf.readLine(); 
		}
		recman.commit();
		buf.close();
	}

	/**
	 * 
	 * @param path
	 *            the output file
	 */
	public static void writeToFile(String path) {
		try (BufferedWriter buw = new BufferedWriter(new FileWriter(path))) {
			FastIterator it = keyLib.keys();
			String word = (String) it.next();
			while (word != null && !word.equals("")) {
				buw.write(word + "\r\n");
				word = (String) it.next();
			}
		} catch (IOException e) {
		}

	}

	/**
	 * 
	 * @param word
	 *            the word to be removed
	 * @throws IOException
	 */
	public static void remove(String word) throws IOException {
		keyLib.remove(word);
		wordLib.remove(getWordID(word));//@by ZHU
		removedNum++;
	}

	/**
	 * 
	 * @param word
	 *            the word to query
	 * @return the word ID, -1 if word is not in the table
	 * @throws IOException
	 */
	public static int getWordID(String word) {
		word = word.toLowerCase();
		int id = -1;
		try {
			if(keyLib.get(word)==null)
				id = -1;
			else id =	(int)keyLib.get(word);
		} catch (IOException e) {
			
		}
		return id;
	}

	public static int getWordNum() {
		return wordNumber - removedNum;
	}

	//@by ZHU
	public static String getWord(int id) {
		Object ob;
		try {
			ob = wordLib.get(id);
			if(ob==null){
				return "";
			}else{
				return ob.toString();
			}
		} catch (IOException e) {
			return "";
		}
		
		/*

		FastIterator it;
		try {
			it = keyLib.keys();
		} catch (IOException e) {
			//System.out.println(e.getMessage());
			return null;
		}
		String find = (String) it.next();

		while (find != null && !find.equals("")) {

			if (getWordID(find) == id) {
				return find;
			}
			find = (String) it.next();
		}
		// //System.out.println("Didn't find");
		return null;*/
	}
	
	public static void commitChange(){
		try {
			recman.commit();
		} catch (IOException e) {
			//System.out.println(e.getMessage());
		}
	}
	public static void close() throws IOException
	{
		recman.commit();
		recman.close();
	}

	public static void main(String args[]) throws IOException {
		System.out.print(WordLib.getAllWord().size());
		
		/*String test1 = "taiyou";
		String test2 = "chen";
		String test3 = "tylor";
		//System.out.println("The word to add are:");
		//System.out.println(test1);
		//System.out.println(test2);
		//System.out.println(test3);
		addWord(test3);
		addWord(test2);
		addWord(test1);
		//System.out.println("Their IDs are now:");
		//System.out.println(test1+" :"+getWordID(test1));
		//System.out.println(test2+" :"+getWordID(test2));
		//System.out.println(test3+" :"+getWordID(test3));
		
		//System.out.println("\nTry adding repeated word or stopword:");
		addWord(test1);
		addWord("one");*/
		
		//System.out.println("\nTry to get the id from word:");
		//System.out.println(test1+" has key:"+getWordID(test1));
		//System.out.println(test2+" has key:"+getWordID(test2));
		//System.out.println(test3+" has key:"+getWordID(test3));
		
		return;
	}

}
