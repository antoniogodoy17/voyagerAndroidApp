package voyager.voyager.models.FilterInterface;

import java.util.ArrayList;

import voyager.voyager.models.Activity;

public class DateFilter implements Filter {

    protected String who;
    protected  ArrayList<Activity> filterList;
    public DateFilter(String who,ArrayList<Activity> filterList){
        this.who = who;
        this.filterList = filterList;
    }

    public  ArrayList<Activity> Execute(){


        return filterList;
    }
}
