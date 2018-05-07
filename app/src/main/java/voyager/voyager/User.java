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
    HashMap<String,ArrayList<HashMap<String,String>>> lists;

    public User(String id,String name,String lastname,String email,String birth_date,String nationality,String state,String city){
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        //this.password = password;
        this.birth_date = birth_date;
        this.nationality = nationality;
        this.state = state;
        this.city = city;
    }

    public User(){}


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public String getLastname(){
        return lastname;
    }
    public String getEmail() {
        return email;
    }

//    public String getPassword() {
//        return password;
//    }

    public String getBirth_date() {
        return birth_date;
    }

    public String getNationality() {
        return nationality;
    }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public String getPhone(){ return phone; }

    public HashMap<String, ArrayList<HashMap<String, String>>> getLists() {
        return lists;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

//    public void setPassword(String password) {
//        this.password = password;
//    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPhone(String phone) { this.phone = phone; }

    public void setLists(HashMap<String, ArrayList<HashMap<String, String>>> lists) {
        this.lists = lists;
    }
}
