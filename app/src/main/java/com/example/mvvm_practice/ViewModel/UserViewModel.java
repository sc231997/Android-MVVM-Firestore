package com.example.mvvm_practice.ViewModel;

import com.example.mvvm_practice.Model.User;
import com.example.mvvm_practice.Repository.UsersDataRepository;
import com.example.mvvm_practice.Util.Resource;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {
    private LiveData<Resource<List<User>>> users;

    public LiveData<Resource<List<User>>> getUsers() {
        if (users == null) {
            loadUsers();
        }
        return users;
    }

    private void loadUsers() {
        users = UsersDataRepository.getUserQueryLiveData();
    }
}
