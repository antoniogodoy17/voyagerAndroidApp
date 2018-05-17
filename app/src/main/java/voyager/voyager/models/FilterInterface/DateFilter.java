package voyager.voyager.models.FilterInterface;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.SimpleFormatter;

import voyager.voyager.models.Activity;

public class DateFilter implements Filter {

    protected String who,how;
    public DateFilter(String how, String who){
        this.who = who;
        this.how = how;
    }

    public  ArrayList<Activity> Execute(ArrayList<Activity> filterList) {
        ArrayList<Activity> newList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try{
            Date date = sdf.parse(who);
            for(int i = 0; i < filterList.size();i++){
                Date listDate = sdf.parse(filterList.get(i).getDate());
                switch (how){
                    case "Before":
                        if(date.compareTo(listDate) < 0){
                            newList.add(filterList.get(i));
                        }
                        break;
                    case "Today":
                        if(date.compareTo(listDate) == 0){
                            newList.add(filterList.get(i));
                        }
                        break;
                    case "After":
                        if(date.compareTo(listDate) > 0){
                            newList.add(filterList.get(i));
                        }
                        break;
                }


            }

        }
        catch (Exception e){
            e.printStackTrace();
        }


        return filterList;
    }
}
