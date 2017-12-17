/**
 * Created by xzhua on 2017/12/17.
 */
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Timestamp;
import java.util.Vector;
import javax.swing.*;
public class GUPInterface extends JFrame implements ActionListener, WindowListener {
    JTextArea text;
    JPanel button;
    JButton start,end;
    JButton seeDetail;
    JScrollPane scoll;

    private boolean studying=false;

    public static void main(String[] args){

        new GUPInterface();
    }

    public void ShowDetail(){
        JFrame f=new JFrame();
        JTextArea a=new JTextArea();
        JScrollPane scoll=new JScrollPane(a);
        String result="";
        Vector<Study> s = Actions.retrieveStudy();
        String old_date ="";
        long daily_time=0;
        for(int i=0;i<s.size();i++){
            Study ss=s.get(i);

            String date = ss.getStart().toLocaleString().substring(0,ss.getStart().toLocaleString().indexOf(" "));
            if(!date.equals(old_date)){
                if(!old_date.equals("")) result+="\nDaily Study Time: "+daily_time/1000/60+" mins\n";
                result+="\n======================= "+date+" =======================\n";
                old_date=date;
                daily_time = 0;
            }

            result+=ss.toString()+"\n------------------------------------------\n";
            daily_time+=ss.getEnd().getTime()-ss.getStart().getTime();

        }
        result+="\nDaily Study Time: "+daily_time/1000/60+" mins\n";
        a.setFont(new Font("TimesRoman",Font.PLAIN,30));
        a.setText(result);
        a.setEditable(false);
        f.add(scoll);

        f.setTitle("Study History");
        f.setVisible(true);

        f.setLocation(nx(500),ny(50));
        f.setSize(nx(1000),ny(900));

        f.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    }

    public GUPInterface(){
        start=new JButton("Start");
        end=new JButton("End");
        seeDetail=new JButton("View History");

        start.addActionListener(this);
        start.setActionCommand("start");

        end.addActionListener(this);
        end.setActionCommand("end");
        seeDetail.addActionListener(this);
        seeDetail.setActionCommand("seeDetail");

        button=new JPanel();

        text= new JTextArea();
        text.setFont(new Font("TimesRoman",Font.PLAIN,30));

        start.setFont(new Font("TimesRoman",Font.PLAIN,30));

        end.setFont(new Font("TimesRoman",Font.PLAIN,30));
        seeDetail.setFont(new Font("TimesRoman",Font.PLAIN,30));

        scoll=new JScrollPane(text);
        //scoll2=new JScrollPane(text);


        button.setLayout(new FlowLayout(FlowLayout.CENTER));
        button.add(start);
        button.add(end);
        button.add(seeDetail);

        end.setEnabled(false);

        this.add(scoll);
        this.add(button,BorderLayout.SOUTH);

        this.setTitle("Study Monitor");

        this.setVisible(true);
        this.setLocation(nx(400),ny(50));
        this.setSize(nx(1000),ny(900));
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(this);


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()){
            case "start":
                studying=true;
                Actions.beginStudy(this.text.getText());
                end.setEnabled(true);
                start.setEnabled(false);
                this.setTitle("Studying");
                this.text.setText("");
                break;
            case "end":
                studying=false;
                Actions.endStudy(this.text.getText());
                start.setEnabled(true);
                end.setEnabled(false);
                this.setTitle("Study Monitor");
                this.text.setText("");
                break;
            case "seeDetail":
                ShowDetail();
                break;
        }
    }

    /**
     *
     * @param x
     * @return The new value of width x in the local screen
     */
    public int nx(int x){
        return (int)(x/(1920.0)*Toolkit.getDefaultToolkit().getScreenSize().width);
    }
    /**
     *
     * @param y
     * @return The new value of height y in the local screen
     */
    public int ny(int y){
        return (int)(y/(1080.0)* Toolkit.getDefaultToolkit().getScreenSize().height);
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        if(this.studying){
            Actions.endStudy(this.text.getText());
            System.exit(0);
        }else{
            System.exit(0);
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
