package com.hkust.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.io.FileNotFoundException;

import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.htmlparser.util.ParserException;

import com.hkust.comp.pageRelation.PageRelations;
import com.hkust.comp.pagerank.PageRank;
import com.hkust.comp.urlPage.PageInfo;
import com.hkust.comp.urlword.Indexer;
import com.hkust.comp.urlword.TitleIndexer;
import com.hkust.comp.wordLib.WordLib;

	

public class Main extends JFrame implements WindowListener{
	private String rootURL="http://www.cse.ust.hk/~ericzhao/COMP4321/TestPages/testpage.htm";
	private int maxNumber=300;
	public static String rootDirectory="./db/";
	
	private JPanel center,bottom;
	private JButton start;
	private JLabel rotUrl,maxNum,rotDir,hint;
	private JTextField urlinput,numinput,dirinput;
	public static JProgressBar progressBar;
	private Main(){
		
		center=new JPanel();
		bottom=new JPanel();
		start=new JButton("Start");
		hint=new JLabel("<html><body>Make Sure your tomcat server is not running, make sure there is a<br>File <a href='http://ihome.ust.hk/~xzhuah/stopwords.txt'>stopwords.txt</a> Containing Stopwords In The Root Rirectory<br>stopwords.txt: http://ihome.ust.hk/~xzhuah/stopwords.txt</body></html>",JLabel.CENTER);
		rotUrl=new JLabel("Root URL: ",JLabel.CENTER);
		maxNum=new JLabel("Max Page Number: ",JLabel.CENTER);
		rotDir=new JLabel("Store In: ");
		urlinput=new JTextField(200);
		numinput=new JTextField(5);
		dirinput=new JTextField(200);
		
		int fontSize=20;
		start.setFont(new Font("����",Font.PLAIN,fontSize));
		hint.setFont(new Font("����",Font.PLAIN,fontSize));
		rotUrl.setFont(new Font("����",Font.PLAIN,fontSize));
		maxNum.setFont(new Font("����",Font.PLAIN,fontSize));
		rotDir.setFont(new Font("����",Font.PLAIN,fontSize));
		urlinput.setFont(new Font("����",Font.PLAIN,fontSize));
		numinput.setFont(new Font("����",Font.PLAIN,fontSize));
		dirinput.setFont(new Font("����",Font.PLAIN,fontSize));
		
		urlinput.setText(this.rootURL);
		numinput.setText(this.maxNumber+"");
		dirinput.setText(this.rootDirectory);
		Main.this.dirinput.setEditable(false);
		center.setLayout(new GridLayout(7,1));
		center.add(hint);
		center.add(rotUrl);
		center.add(urlinput);
		center.add(maxNum);
		center.add(numinput);
		center.add(rotDir);
		center.add(dirinput);
		bottom.add(start);
		
		start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try{
					Main.this.rootURL=urlinput.getText();
					Main.this.maxNumber=Integer.parseInt(Main.this.numinput.getText());
					Main.this.rootDirectory=Main.this.dirinput.getText();
					
					Main.this.progressBar=new JProgressBar();
					progressBar.setForeground(Color.green);
					progressBar.setStringPainted(true);
					progressBar.setValue(0);
					progressBar.setString("0%");
					SpiderTest.dbroot=Main.this.rootDirectory;
					Main.this.add(progressBar,BorderLayout.NORTH);
					Main.this.start.setEnabled(false);
					Main.this.setVisible(true);
					new Thread(new Runnable(){

						@Override
						public void run() {
							try {
								long totaltime=SpiderTest.fetchpages(Main.this.rootURL, Main.this.maxNumber);
								PageRank.updatePageRank();
								Main.this.progressBar.setValue(100);
								
								progressBar.setString("100%");
								
								Main.this.start.setEnabled(true);
								Main.this.hint.setText(totaltime+"ms"+" "+"Finish, When you exit the program, database will be updated");
							} catch (ParserException ee) {
								hint.setText("Spider finish with some errors\n"+ee.toString());
								Main.this.start.setEnabled(true);
							} catch (IOException e) {
								hint.setText("Spider finish with some errors\n"+e.toString());
								Main.this.start.setEnabled(true);
							}
							
						}
						
					}).start();
				}catch(Exception e){
					hint.setText("Please Check Your Input Carefully");
				}
				
			}
			
		});
		
		this.add(center);
		this.add(bottom,BorderLayout.SOUTH);
		
		this.setTitle("Welcome to Spider");
		this.setSize(1200, 600);
		this.setLocation(200,200);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(this);
		
	}
	public static void main(String args[]){
		new Main();
	}
	
	@Override
	public void windowClosing(WindowEvent arg0) {
		
		try {
			
			PageRelations.close();
			Indexer.close();
			TitleIndexer.close();
			WordLib.close();
			PageRank.close();
			PageInfo.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			System.exit(0);
		}
		
	
	}
	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
