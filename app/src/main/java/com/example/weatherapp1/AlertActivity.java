package com.example.weatherapp1;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.weatherapp1.databinding.ActivityAlertBinding;
import com.example.weatherapp1.utils.DrawableUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AlertActivity extends AppCompatActivity {

    private static final String TAG = "AlertActivity";
    private ActivityAlertBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlertBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Điều chỉnh kích thước biểu tượng
        DrawableUtils.resizeImageButtonDrawable(binding.btnBack, 24, 24);
        DrawableUtils.resizeImageViewDrawable(binding.imageViewAlertIcon, 48, 48);

        // Đặt hình ảnh trực tiếp cho ImageButton từ tài nguyên drawable
        binding.btnBack.setImageResource(R.drawable.ic_back);
        // Loại bỏ tint để hiển thị màu gốc của PNG
        binding.btnBack.setColorFilter(null);
        // Đảm bảo ImageButton có thể nhìn thấy
        binding.btnBack.setVisibility(View.VISIBLE);

        // Điều chỉnh kích thước biểu tượng
        ImageButton backButton = binding.btnBack;
        DrawableUtils.resizeImageButtonDrawable(backButton, 24, 24);

        // Get alert data from intent
        String alertEvent = getIntent().getStringExtra("ALERT_EVENT") != null ?
                getIntent().getStringExtra("ALERT_EVENT") : "";
        String alertDescription = getIntent().getStringExtra("ALERT_DESCRIPTION") != null ?
                getIntent().getStringExtra("ALERT_DESCRIPTION") : "";
        long alertStart = getIntent().getLongExtra("ALERT_START", 0);
        long alertEnd = getIntent().getLongExtra("ALERT_END", 0);

        Log.d(TAG, "Alert event: " + alertEvent);

        // Display alert information
        displayAlertInfo(alertEvent, alertDescription, alertStart, alertEnd);

        binding.btnBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void displayAlertInfo(String event, String description, long start, long end) {
        // Set alert title
        binding.textViewAlertTitle.setText(event);

        // Set alert time period
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm - dd/MM/yyyy", new Locale("vi"));
        Date startDate = new Date(start * 1000);
        Date endDate = new Date(end * 1000);
        binding.textViewAlertTime.setText(dateFormat.format(startDate) + " | " + dateFormat.format(endDate));

        // Set alert description
        binding.textViewAlertDescription.setText(description.toUpperCase(Locale.forLanguageTag("vi")));

        // Set alert icon based on event type
        setAlertIcon(event);

        // Set recommendations based on alert type
        binding.textViewRecommendations.setText(getRecommendationsForAlert(event));
    }

    private void setAlertIcon(String alertType) {
        ImageView iconView = binding.imageViewAlertIcon;
        int iconResId;

        if (alertType.toLowerCase().contains("rain")) {
            iconResId = R.drawable.ic_rain;
        } else if (alertType.toLowerCase().contains("thunderstorm")) {
            iconResId = R.drawable.ic_thunderstorm;
        } else if (alertType.toLowerCase().contains("flood")) {
            iconResId = R.drawable.ic_rain; // Sử dụng icon mưa cho lũ lụt
        } else if (alertType.toLowerCase().contains("heat")) {
            iconResId = R.drawable.ic_clear_day; // Sử dụng icon nắng cho nóng
        } else if (alertType.toLowerCase().contains("fog")) {
            iconResId = R.drawable.ic_fog;
        } else if (alertType.toLowerCase().contains("snow")) {
            iconResId = R.drawable.ic_snow;
        } else if (alertType.toLowerCase().contains("tornado")) {
            iconResId = R.drawable.ic_thunderstorm; // Sử dụng icon bão cho lốc xoáy
        } else {
            iconResId = R.drawable.ic_notification;
        }

        iconView.setImageResource(iconResId);
    }

    private String getRecommendationsForAlert(String alertType) {
        alertType = alertType.toLowerCase();

        if (alertType.contains("rain") || alertType.contains("storm") || alertType.contains("thunderstorm")) {
            return "• Ở trong nhà, tránh xa cửa sổ\n" +
                    "• Không trú dưới cây cao, cột điện\n" +
                    "• Rút phích cắm thiết bị điện";
        } else if (alertType.contains("flood")) {
            return "• Di chuyển đến vùng cao hơn\n" +
                    "• Không lái xe qua vùng ngập nước\n" +
                    "• Chuẩn bị đồ dùng khẩn cấp";
        } else if (alertType.contains("heat")) {
            return "• Uống nhiều nước\n" +
                    "• Tránh hoạt động ngoài trời\n" +
                    "• Sử dụng quạt hoặc máy lạnh";
        } else if (alertType.contains("fog")) {
            return "• Lái xe chậm, bật đèn sương mù\n" +
                    "• Giữ khoảng cách an toàn\n" +
                    "• Tránh di chuyển nếu không cần thiết";
        } else if (alertType.contains("snow")) {
            return "• Mặc quần áo ấm nhiều lớp\n" +
                    "• Tránh di chuyển khi tuyết dày\n" +
                    "• Dự trữ thực phẩm và nước uống";
        } else if (alertType.contains("tornado")) {
            return "• Tìm nơi trú ẩn dưới mặt đất\n" +
                    "• Tránh xa cửa sổ và đồ vật có thể bay\n" +
                    "• Bảo vệ đầu và cổ";
        } else {
            return "• Theo dõi cập nhật thời tiết\n" +
                    "• Chuẩn bị cho tình huống khẩn cấp\n" +
                    "• Tuân thủ hướng dẫn từ cơ quan chức năng";
        }
    }
}
