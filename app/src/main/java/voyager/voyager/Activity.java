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

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);


    public Activity(){}

    public int compare(Object o1, Object o2) {
        Activity activity1 = (Activity)o1;
        Activity activity2 = (Activity) o2;
        return activity1.getTitle().
                compareTo(activity2.getTitle());


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

    public Date getDate() {
        Date d = new Date();
        String today;
        if(date == ""){

            today = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            try {
                d = formatter.parse(today);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return d;
        }else{
            try {
                d = formatter.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return d;
        }

    }

    public String getSchedule() {
        return schedule;
    }

    public HashMap<String, String> getReviews() {
        return reviews;
    }

    public ArrayList<Object> getTags() {
        return tags;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public HashMap<String, Object> getLocation() {
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

    public void setTags(ArrayList<Object> tags) {
        this.tags = tags;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLocation(HashMap<String, Object> location) {
        this.location = location;
    }
}
