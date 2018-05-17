package voyager.voyager.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

public class Activity implements Serializable, Comparator {

    private String _id;
    private String category;
    private int cost;
    private String date;
    private String description;
    private String image_principal;
    private HashMap<String, String> images;
    private HashMap<String , String> location;
    private ArrayList<HashMap<String, String>> ratings;
    private ArrayList<HashMap<String, String>> reviews;
    private String schedule;
    private double score;
    private String status;
    private ArrayList<HashMap<String,String>> tags;
    private String title;
    private String type;

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);


    public Activity(){}

    public int compare(Object o1, Object o2) {
        Activity activity1 = (Activity)o1;
        Activity activity2 = (Activity) o2;
        return activity1.getTitle().
                compareTo(activity2.getTitle());
    }

    public Double calculatedScore(){
        Double tempScore = 0.0;
        ArrayList<HashMap<String,String>> tempRatings = getReviews();

        if(reviews != null) {
            for (HashMap hm : tempRatings) {
                tempScore += Double.valueOf(hm.get("rating").toString());
            }
            return tempScore / tempRatings.size();
        }
        return 0.0;
    }

    public double getScore() {
        setScore(calculatedScore());
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
    @Override
    public String toString() {
        return title;
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

    public ArrayList<HashMap<String, String>> getReviews() {
        return reviews;
    }

    public ArrayList<HashMap<String,String>> getTags() {
        return tags;
    }

    public ArrayList<HashMap<String, String>> getRatings(){
        return ratings;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getImage_principal() {
        return image_principal;
    }

    public HashMap<String, String> getLocation() {
        return location;
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

    public void setReviews(ArrayList<HashMap<String, String>> reviews) {
        this.reviews = reviews;
    }

    public void setTags(ArrayList<HashMap<String,String>>tags) {
        this.tags = tags;
    }

    public void setRatings(ArrayList<HashMap<String,String>> ratings) { this.ratings = ratings; }

    public void setType(String type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLocation(HashMap<String, String> location) {
        this.location = location;
    }

    public void setImage_principal(String image_principal) {
        this.image_principal = image_principal;
    }
}
