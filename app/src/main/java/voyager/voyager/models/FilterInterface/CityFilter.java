package voyager.voyager.models.FilterInterface;

import java.util.ArrayList;

import voyager.voyager.models.Activity;

public class CityFilter implements Filter {
    protected String who;

    public CityFilter(String who){
        this.who = who;
    }

    public ArrayList<Activity> Execute(ArrayList<Activity> filterList){

        ArrayList<Activity> newList = new ArrayList<>();


        for(int i = 0; i < filterList.size(); i++){

            if(filterList.get(i).getLocation().get("city").equals(who)){
                newList.add(filterList.get(i));

            }
        }
        return newList;
    }
}

