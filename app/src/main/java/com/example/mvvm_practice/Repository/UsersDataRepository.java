package com.example.mvvm_practice.Repository;

import com.example.mvvm_practice.Model.User;
import com.example.mvvm_practice.Util.QueryLiveData;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class UsersDataRepository {
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static Query query = db.collection("users").orderBy("name");

    private static QueryLiveData<User> userQueryLiveData = new QueryLiveData<>(query,User.class);

    public static QueryLiveData<User> getUserQueryLiveData() {
        return userQueryLiveData;
    }
}
