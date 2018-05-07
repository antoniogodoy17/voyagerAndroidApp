package voyager.voyager;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class Activity implements Serializable, Comparator {

    private HashMap<String, String> score;
    private int cost;
    private HashMap<String, String> images;
    private String category;
    private String description;
    private String status;
    private String date;
    private String schedule;
    private HashMap<String, String> reviews;
    private ArrayList<HashMap<String,String>> tags;
    private String type;
    private String title;
    private HashMap<String , String> location;

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);


    public Activity(){}

    public int compare(Object o1, Object o2) {
        Activity activity1 = (Activity)o1;
        Activity activity2 = (Activity) o2;
        return activity1.getTitle().
                compareTo(activity2.getTitle());


    }

    @Override
    public String toString() {
        return title;
    }

    public HashMap<String, String> getScore() {
        return score;
    }

    public int getCost() {
        return cost;
    }

    public HashMap<String, String> getImages() {
        return images;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getDate() { return date; }

    //    public Date getDate() {
//        Date d = new Date();
//        String today;
//        if(date == ""){
//
//            today = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
//            try {
//                d = formatter.parse(today);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            return d;
//        }else{
//            try {
//                d = formatter.parse(date);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            return d;
//        }
//
//    }

    public String getSchedule() {
        return schedule;
    }

    public HashMap<String, String> getReviews() {
        return reviews;
    }

    public ArrayList<HashMap<String,String>> getTags() {
        return tags;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public HashMap<String, String> getLocation() {
        return location;
    }

    public void setScore(HashMap<String, String> score) {
        this.score = score;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setImages(HashMap<String, String> images) {
        this.images = images;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDate(String date) {

        this.date = date;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public void setReviews(HashMap<String, String> reviews) {
        this.reviews = reviews;
    }

    public void setTags(ArrayList<HashMap<String,String>>tags) {
        this.tags = tags;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLocation(HashMap<String, String> location) {
        this.location = location;
    }
}
