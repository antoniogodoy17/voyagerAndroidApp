package voyager.voyager.models.FilterInterface;

import java.util.ArrayList;

import voyager.voyager.models.Activity;

public class CategoryFilter implements Filter {

    public ArrayList<Activity> Execute(String how, String who, ArrayList<Activity> filterList){

        ArrayList<Activity> newList = new ArrayList<>();

        for(int i = 0; i < filterList.size(); i++){
            if(filterList.get(i).getCategory() == who){
                newList.add(filterList.get(i));
            }
        }
        return newList;
    }
}

