package voyager.voyager.models.FilterInterface;

import java.util.ArrayList;

import voyager.voyager.models.Activity;

public class CostFilter implements Filter{
    public ArrayList<Activity> Execute(String how, String who, ArrayList<Activity> filterList){


        return filterList;
    }
}
