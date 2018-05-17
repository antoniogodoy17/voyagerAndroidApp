package voyager.voyager.models.FilterInterface;

import android.app.ActivityOptions;

import java.util.ArrayList;

import voyager.voyager.models.Activity;

public class CostFilter implements Filter{
    public ArrayList<Activity> Execute(String how, String who, ArrayList<Activity> filterList){
        ArrayList<Activity> newList = new ArrayList<>();
        for(int i = 0; i < filterList.size(); i++){
            if( filterList.get(i).getCost() <= Integer.parseInt(who) ){
                newList.add(filterList.get(i));
            }
        }

        return newList;
    }
}
