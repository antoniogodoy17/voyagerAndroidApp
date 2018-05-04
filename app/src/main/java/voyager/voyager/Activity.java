package voyager.voyager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Activity implements Serializable {

    public HashMap<String, String> score;
    public int cost;
    public HashMap<String, String> images;
    public String category;
    public String description;
    public String status;
    public  String date;
    public String schedule;
    public HashMap<String, String> reviews;
    public ArrayList<Object> tags;
    public String type;
    public String title;
    public HashMap<String , Object> location;


    public Activity(){}
}
