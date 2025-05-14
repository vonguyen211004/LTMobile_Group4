package com.example.weatherapp1;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.weatherapp1.adapters.SearchResultAdapter;
import com.example.weatherapp1.databinding.ActivitySearchBinding;
import com.example.weatherapp1.models.SearchResult;
import com.example.weatherapp1.utils.NetworkUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;
    private SearchResultAdapter adapter;
    private final List<SearchResult> searchResults = new ArrayList<>();
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupRecyclerView();
        setupSearchView();

        binding.layoutUseLocation.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("REQUEST_LOCATION", true);
            startActivity(intent);
            finish();
        });

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });

        binding.textViewCancel.setOnClickListener(v -> {
            binding.editTextSearch.setText("");
        });
    }

    private void setupRecyclerView() {
        adapter = new SearchResultAdapter(searchResult -> {
            Intent intent = new Intent(this, WeatherActivity.class);
            intent.putExtra("CITY_NAME", searchResult.getName());
            intent.putExtra("USE_LOCATION", false);
            startActivity(intent);
        });

        binding.recyclerViewResults.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewResults.setAdapter(adapter);
    }

    private void setupSearchView() {
        binding.editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() >= 3) {
                    searchCities(s.toString());
                } else {
                    searchResults.clear();
                    adapter.submitList(new ArrayList<>(searchResults));
                    binding.progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void searchCities(String query) {
        binding.progressBar.setVisibility(View.VISIBLE);

        executor.execute(() -> {
            try {
                List<SearchResult> results = NetworkUtils.searchCities(query);

                runOnUiThread(() -> {
                    searchResults.clear();
                    searchResults.addAll(results);
                    adapter.submitList(new ArrayList<>(searchResults));
                    binding.progressBar.setVisibility(View.GONE);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(
                            SearchActivity.this,
                            "Error searching cities: " + e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                    binding.progressBar.setVisibility(View.GONE);
                });
            }
        });
    }
}