package voyager.voyager.models.FilterInterface;

import java.util.ArrayList;

import voyager.voyager.models.Activity;

public class CategoryFilter implements Filter {

    public void Execute(String how, String who, ArrayList<Activity> filterList){

        for(int i = 0; i < filterList.size(); i++){
            if(filterList.get(i).getCategory() != who){
                filterList.remove(i);
            }
        }

    }
}
