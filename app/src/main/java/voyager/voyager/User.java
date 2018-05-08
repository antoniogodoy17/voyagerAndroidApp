package voyager.voyager;

import java.util.ArrayList;
import java.util.HashMap;

public class User {
    String id;
    String name;
    String lastname;
    String email;
    //String password;
    String birth_date;
    String nationality;
    String state;
    String city;
    String phone;
    String profile_picture;
    HashMap<String,ArrayList<HashMap<String,String>>> list;

    public User(String id, String name, String lastname, String email, String birth_date, String nationality){
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        //this.password = password;
        this.birth_date = birth_date;
        this.nationality = nationality;
//        this.state = state;
//        this.city = city;
    }
    public User(){}
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    public String getLastname(){
        return lastname;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail() {
        return email;
    }
    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }
    public String getProfile_picture() {
        return profile_picture;
    }
    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }
    public String getBirth_date() {
        return birth_date;
    }
    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
    public String getNationality() {
        return nationality;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getState() {
        return state;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getCity() {
        return city;
    }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPhone(){ return phone; }
    public void setLists(HashMap<String, ArrayList<HashMap<String, String>>> list) {
        this.list = list;
    }
    public HashMap<String, ArrayList<HashMap<String, String>>> getLists() {
        return list;
    }
}
