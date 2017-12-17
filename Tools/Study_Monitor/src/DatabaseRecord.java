/**
 * Created by xzhua on 2017/12/17.
 */
public class DatabaseRecord {
    private long time;
    private String text;
    private int type;

    public long getTime() {
        return time;
    }

    public String getText() {
        return text;
    }

    public int getType() {
        return type;
    }

    private static final String split="!#!";
    public DatabaseRecord(long time, String text, int type) {
        this.time = time;
        this.text = text;
        this.type = type;
    }

    public DatabaseRecord(String dbstring){
        dbstring = dbstring.trim();


        String[] temp  = dbstring.split(split);

        this.time = Long.parseLong(temp[0]);
        this.text=temp[1].replaceAll("<div>","\n");

        this.type=Integer.parseInt(temp[2]);
    }
    @Override
    public String toString() {
        return time+split+text+split+type;
    }


}
