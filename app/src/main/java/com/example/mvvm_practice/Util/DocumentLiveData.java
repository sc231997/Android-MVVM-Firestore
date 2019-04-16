package com.example.mvvm_practice.Util;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import androidx.lifecycle.LiveData;


public class DocumentLiveData<T> extends LiveData<Resource<T>>
        implements EventListener<DocumentSnapshot> {
    private final Class<T> type;
    private ListenerRegistration registration;
    private final DocumentReference ref;

    public DocumentLiveData(DocumentReference ref, Class<T> type) {
        this.ref = ref;
        this.type = type;
    }

    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            setValue(new Resource<>(e));
            return;
        }
        setValue(new Resource<>(snapshot.toObject(type)));
    }

    @Override
    protected void onActive() {
        super.onActive();
        registration = ref.addSnapshotListener(this);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        if (registration != null) {
            registration.remove();
            registration = null;
        }
    }
}