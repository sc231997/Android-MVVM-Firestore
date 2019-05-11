package com.example.mvvm_practice.Repository;

import com.example.mvvm_practice.Model.Post;
import com.example.mvvm_practice.Util.QueryLiveData;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class PostsDataRepository {
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static Query query = db.collection("posts").orderBy("title");

    private static QueryLiveData<Post> postQueryLiveData = new QueryLiveData<>(query, Post.class);

    public static QueryLiveData<Post> getPostQueryLiveData() {
        return postQueryLiveData;
    }
}
