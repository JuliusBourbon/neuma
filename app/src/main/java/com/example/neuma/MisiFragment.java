package com.example.neuma;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neuma.adapters.MisiAdapter;
import com.example.neuma.models.Achievement;
import com.example.neuma.network.AchievementApi;
import com.example.neuma.utils.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MisiFragment extends Fragment {

    private RecyclerView rvMisiList;
    private AchievementApi achievementApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_misi, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvMisiList = view.findViewById(R.id.rv_misi_list);
        rvMisiList.setLayoutManager(new LinearLayoutManager(requireContext()));

        achievementApi = ApiClient.getAuthClient(requireContext()).create(AchievementApi.class);

        // Tampilkan Data Misi (dari API Achievements)
        loadMisiData();

    }

    private void loadMisiData() {
        List<Achievement> achievements = com.example.neuma.utils.DataManager.getInstance().getAchievements();
        if (achievements != null && !achievements.isEmpty()) {
            rvMisiList.setAdapter(new MisiAdapter(achievements));
        } else {
            Toast.makeText(requireContext(), "Data misi tidak tersedia. Silakan muat ulang aplikasi.", Toast.LENGTH_SHORT).show();
        }
    }
}


