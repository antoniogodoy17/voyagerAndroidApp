package voyager.voyager;

public class Card {
    private String imgUrl;
    private String title;
    private Activity activity;

    public Card(Activity activity){
        this.activity = activity;
        this.title = activity.title;
        this.imgUrl = "drawable://"+R.drawable.logo512;
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
