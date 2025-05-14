package com.example.weatherapp1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.weatherapp1.databinding.ActivityAlertBinding;
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

        Log.d(TAG, "onCreate called");

        String alertEvent = getIntent().getStringExtra("ALERT_EVENT") != null ?
                getIntent().getStringExtra("ALERT_EVENT") : "";
        String alertDescription = getIntent().getStringExtra("ALERT_DESCRIPTION") != null ?
                getIntent().getStringExtra("ALERT_DESCRIPTION") : "";
        long alertStart = getIntent().getLongExtra("ALERT_START", 0);
        long alertEnd = getIntent().getLongExtra("ALERT_END", 0);

        Log.d(TAG, "Alert event: " + alertEvent);

        displayAlertInfo(alertEvent, alertDescription, alertStart, alertEnd);


        binding.btnBack.setOnClickListener(v -> {
            Log.d(TAG, "onBackPressed called");

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        });
    }


    private void displayAlertInfo(String event, String description, long start, long end) {
        binding.textViewAlertTitle.setText(event);

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm - dd/MM/yyyy", new Locale("vi"));
        Date startDate = new Date(start * 1000);
        Date endDate = new Date(end * 1000);
        binding.textViewAlertTime.setText(dateFormat.format(startDate) + " | " + dateFormat.format(endDate));

        binding.textViewAlertDescription.setText(description.toUpperCase(Locale.forLanguageTag("vi")));

        setAlertIcon(event);

        binding.textViewRecommendations.setText(getRecommendationsForAlert(event));
    }

    private void setAlertIcon(String alertType) {
        ImageView iconView = binding.imageViewAlertIcon;
        int iconResId;
        alertType = alertType.toLowerCase();

        if (alertType.contains("rain")) {
            iconResId = R.drawable.ic_rain;
        } else if (alertType.contains("thunderstorm") || alertType.contains("storm")) {
            iconResId = R.drawable.ic_thunderstorm;
        } else if (alertType.contains("flood")) {
            iconResId = R.drawable.ic_rain;
        } else if (alertType.contains("heat")) {
            iconResId = R.drawable.ic_clear_day;
        } else if (alertType.contains("fog")) {
            iconResId = R.drawable.ic_fog;
        } else if (alertType.contains("snow")) {
            iconResId = R.drawable.ic_snow;
        } else if (alertType.contains("tornado")) {
            iconResId = R.drawable.ic_thunderstorm;
        } else {
            iconResId = R.drawable.ic_notification;
        }

        iconView.setImageResource(iconResId);
    }

    private String getRecommendationsForAlert(String alertType) {
        alertType = alertType.toLowerCase();

        if (alertType.contains("rain")) {
            return "• Ở trong nhà khi mưa lớn\n" +
                    "• Mang theo áo mưa khi ra ngoài\n" +
                    "• Tránh các khu vực dễ ngập lụt";
        } else if (alertType.contains("storm") || alertType.contains("thunderstorm")) {
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
