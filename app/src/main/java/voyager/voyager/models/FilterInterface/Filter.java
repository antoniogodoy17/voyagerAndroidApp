package voyager.voyager.models.FilterInterface;

import java.util.ArrayList;

import voyager.voyager.models.Activity;

public interface Filter {
    public void Execute( String how, String who, ArrayList<Activity> filterList);
}
