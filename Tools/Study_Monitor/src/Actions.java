
import java.util.Vector;

/**
 * Created by xzhua on 2017/12/17.
 */
public class Actions {
    public static void main(String[] args){
        beginStudy("Good");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        endStudy("Good");
        Vector<Study> s = retrieveStudy();

        for(int i=0;i<s.size();i++){
            System.out.println(s.get(i).toString());
            System.out.println(i);
        }

    }
    public static void beginStudy(String goal){

        DatabaseRecord record=new DatabaseRecord(System.currentTimeMillis(),goal,0);
        Csvdatabase.insert(record);

    }
    public static void endStudy(String summary){
        DatabaseRecord record=new DatabaseRecord(System.currentTimeMillis(),summary,1);
        Csvdatabase.insert(record);
    }
    public static Vector<Study> retrieveStudy(){
        Vector<Study> result=new Vector<Study>();
        String[] all = Csvdatabase.readAll();
        //System.out.println("FUCK "+all[all.length-1]);
        System.out.println(all.length);


        for(int i=0;i+1<all.length;i+=2){
            DatabaseRecord st=new DatabaseRecord(all[i]);
            DatabaseRecord ed = new DatabaseRecord(all[i+1]);
            if (st.getType()==0&&ed.getType()==1) {
                result.add( new Study(st, ed));
            }else{
                i++;
                continue;
            }
            System.out.println(all[i]+" "+all[i+1]);

        }
        return result;
    }
}
