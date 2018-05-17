package voyager.voyager.models.FilterInterface;

import java.util.ArrayList;

import voyager.voyager.models.Activity;

public class Invoker {
    //Class Invoker help us to interact with
    ArrayList<Activity> filteredActivities;
    ArrayList<Filter> filters = new ArrayList();

    public void setFilter(Filter filter){
        filters.add(filter);
    }

    public ArrayList<Activity> applyFilters(){

        for(int i = 0;i<filters.size();i++){
           filteredActivities = filters.get(i).Execute(filteredActivities);
        }
        cleanFilters();
        return filteredActivities;
    }

    public void cleanFilters(){
        for(int i = 0;i<filters.size();i++){
            filters.remove(i);
        }
    }

    public void setFilteredActivities(ArrayList<Activity> mylist){
        filteredActivities = mylist;

    }


}
