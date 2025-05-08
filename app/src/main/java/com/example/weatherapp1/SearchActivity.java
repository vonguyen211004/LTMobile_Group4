package com.example.weatherapp1;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.weatherapp1.adapters.SearchResultAdapter;
import com.example.weatherapp1.databinding.ActivitySearchBinding;
import com.example.weatherapp1.models.SearchResult;
import com.example.weatherapp1.utils.DrawableUtils;
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



        // Đặt hình ảnh trực tiếp cho ImageButton từ tài nguyên drawable
        binding.btnBack.setImageResource(R.drawable.ic_back);
        // Loại bỏ tint để hiển thị màu gốc của PNG
        binding.btnBack.setColorFilter(null);
        // Đảm bảo ImageButton có thể nhìn thấy
        binding.btnBack.setVisibility(View.VISIBLE);

        // Điều chỉnh kích thước biểu tượng
        ImageButton backButton = binding.btnBack;
        DrawableUtils.resizeImageButtonDrawable(backButton, 24, 24);

        // Điều chỉnh kích thước biểu tượng tìm kiếm trong EditText
        DrawableUtils.resizeTextViewDrawables(binding.editTextSearch, 20, 20);

        // Điều chỉnh kích thước biểu tượng vị trí
        TextView locationText = binding.textViewUseLocation;
        DrawableUtils.resizeTextViewDrawables(locationText, 24, 24);

        setupRecyclerView();
        setupSearchView();



        // Xử lý khi click vào "Vị trí của bạn"
        binding.textViewUseLocation.setOnClickListener(v -> {
            // Tạo Intent để quay lại MainActivity và yêu cầu sử dụng vị trí
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("REQUEST_LOCATION", true);
            startActivity(intent);
            finish(); // Đóng màn hình tìm kiếm
        });

        binding.btnUseLocation.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });

        binding.btnBack.setOnClickListener(v -> {
            onBackPressed();
        });

        // Xử lý khi click vào nút "Hủy"
        binding.textViewCancel.setOnClickListener(v -> {
            // Xóa nội dung trong ô tìm kiếm
            binding.editTextSearch.setText("");
        });
    }

    private void setupRecyclerView() {
        adapter = new SearchResultAdapter(searchResult -> {
            // Handle click on search result
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
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not used
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
