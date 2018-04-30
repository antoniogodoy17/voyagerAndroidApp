package voyager.voyager;

import android.arch.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class homeVM extends ViewModel {
    private FirebaseDatabase database;
    private DatabaseReference usersDatabase;
    private FirebaseUser fbUser;
    private FirebaseAuth firebaseAuth;
    private User user;

    public homeVM(){
        init();
    }

    public void init(){
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        fbUser = firebaseAuth.getCurrentUser();
        usersDatabase = database.getReference("User");
    }

    public FirebaseDatabase getDatabase() {
        return database;
    }

    public FirebaseUser getFbUser() {
        return fbUser;
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public DatabaseReference getUsersDatabase() {
        return usersDatabase;
    }

    public User getUser() {
        return user;
    }

    public void setDatabase(FirebaseDatabase database) {
        this.database = database;
    }

    public void setFbUser(FirebaseUser fbUser) {
        this.fbUser = fbUser;
    }

    public void setFirebaseAuth(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
