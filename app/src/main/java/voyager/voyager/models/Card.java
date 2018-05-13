package voyager.voyager.models;

public class Card {
    private String imgUrl;
    private String title;
    private Activity activity;

    public Card(Activity activity){
        this.activity = activity;
        this.title = activity.getTitle();
        this.imgUrl = activity.getImage_principal();
//        imgUrl = activity.getImages();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Activity getActivity() {
        return activity;
    }
}
