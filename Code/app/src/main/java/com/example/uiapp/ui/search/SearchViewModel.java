package com.example.uiapp.ui.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.uiapp.model.MoodEntry;
import com.example.uiapp.model.UserModel;
import com.example.uiapp.utils.HelperClass;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchViewModel extends ViewModel {

    private final MutableLiveData<List<UserModel>> users = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<UserModel>> filteredUsers = new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference userRef = db.collection("Users");

    public LiveData<List<UserModel>> getFilteredUsers() {
        return filteredUsers;
    }

    /**
     * This function goes through the public document for the user to fetch any required information
     * of other searched or categorized user
     */
    public void fetchUsers() {
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                QuerySnapshot snapshot = task.getResult();
                String loggedInUsername = HelperClass.users.getUsername();
                List<UserModel> allUsers = new ArrayList<>();
                for (QueryDocumentSnapshot document : snapshot) {
                    UserModel mood = document.toObject(UserModel.class);
                    if (!mood.getUsername().contains(loggedInUsername)) {
                        allUsers.add(mood);
                    }
                }
                users.setValue(allUsers);
                filterUsers();
            }
        });
    }

    private void filterUsers() {
        List<UserModel> originalList = users.getValue();
        if (originalList == null) return;

        List<UserModel> filteredList = new ArrayList<>();

        String query = searchQuery.getValue() != null ? searchQuery.getValue().toLowerCase() : "";

        for (UserModel user : originalList) {
            if (user == null) continue;

            boolean searchMatches = query.isEmpty() || userContainsSearchText(user, query);

            if (searchMatches) {
                filteredList.add(user);
            }
        }

        filteredUsers.setValue(filteredList);
    }

    private boolean userContainsSearchText(UserModel user, String searchText) {
        String userName = user.getUsername() != null ? user.getUsername().toLowerCase() : "";

        return userName.contains(searchText);
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
        filterUsers();
    }

}