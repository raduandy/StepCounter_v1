package counter.step.ro.stepcounter;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

@IgnoreExtraProperties
public class Steps {

    private int number;
//    private int date;
    private String ID = "USER-1";

    public final int THRESHOLD = 5;


    public Steps(){

    }

    public Steps(int number){
        this.number = number;
//        this.date = date;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

//    public int getDate() {
//        return date;
//    }
//
//    public void setDate(int date) {
//        this.date = date;
//    }

    @Override
    public String toString() {
        return ID + " " + number;
    }

}
