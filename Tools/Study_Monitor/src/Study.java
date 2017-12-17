/**
 * Created by xzhua on 2017/12/17.
 */
import java.sql.Timestamp;
public class Study {
    private Timestamp start;
    private Timestamp end;
    private String LearningGoal;
    private String SelfEvaluation;

    public Study(){
        start=new Timestamp(0);
        end=new Timestamp(0);
        LearningGoal="";
        SelfEvaluation="";

    }

    public Study(Timestamp start, Timestamp end, String learningGoal, String selfEvaluation) {
        this.start = start;
        this.end = end;
        LearningGoal = learningGoal;
        SelfEvaluation = selfEvaluation;
    }
    public Study(long start, long end, String learningGoal, String selfEvaluation) {
        this(new Timestamp(start),new Timestamp(end),learningGoal,selfEvaluation);

    }
    public Study(DatabaseRecord start, DatabaseRecord end){
        this(start.getTime(),end.getTime(),start.getText(),end.getText());

    }

    public Timestamp getStart() {
        return start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public String getLearningGoal() {
        return LearningGoal;
    }

    public String getSelfEvaluation() {
        return SelfEvaluation;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public void setLearningGoal(String learningGoal) {
        LearningGoal = learningGoal;
    }

    public void setSelfEvaluation(String selfEvaluation) {
        SelfEvaluation = selfEvaluation;
    }

    @Override
    public String toString() {
        return "Studied from "+start.toLocaleString()+" to "+end.toLocaleString()+" \nStudy Goal: \n"+this.getLearningGoal()+" \nEvaluation: \n"+this.getSelfEvaluation()+" \nDuration:"+(this.end.getTime()-this.start.getTime())/1000/60+" mins";
    }
}
