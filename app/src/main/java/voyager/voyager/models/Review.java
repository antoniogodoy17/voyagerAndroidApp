package voyager.voyager.models;

import java.util.Date;

public class Review {
    String review;
    double rating;
    Date date;

    public Review(String review, double rating, Date date){
        this.review = review;
        this.rating = rating;
        this.date = date;
    }

    public String getReview() {
        return review;
    }

    public double getRating() {
        return rating;
    }

    public Date getDate() {
        return date;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setDate(Date date){
        this.date = date;
    }
}
