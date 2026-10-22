package com.example.clashroyalednshelper;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.provider.Settings;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.content.Context;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(24), dp(28), dp(24), dp(24));
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setBackgroundColor(0xFFF5F7FA);

        TextView title = new TextView(this);
        title.setText("Clash Royale DNS Helper");
        title.setTextSize(24);
        title.setTextColor(0xFF182230);
        title.setGravity(Gravity.CENTER);
        root.addView(title, matchWrap());

        TextView description = new TextView(this);
        description.setText("Помощник для проверки подключения. Не использует VPN, не меняет файлы игры и не гарантирует обход сетевых ограничений.");
        description.setTextSize(15);
        description.setTextColor(0xFF344054);
        description.setPadding(0, dp(16), 0, dp(18));
        root.addView(description, matchWrap());

        Button dnsButton = new Button(this);
        dnsButton.setText("Открыть настройки частного DNS");
        dnsButton.setOnClickListener(v -> openDnsSettings());
        root.addView(dnsButton, matchWrap());

        Button checkButton = new Button(this);
        checkButton.setText("Проверить подключение");
        checkButton.setOnClickListener(v -> showNetworkStatus());
        root.addView(checkButton, matchWrap());

        Button gameButton = new Button(this);
        gameButton.setText("Запустить Clash Royale");
        gameButton.setOnClickListener(v -> launchGame());
        root.addView(gameButton, matchWrap());

        status = new TextView(this);
        status.setText("Готово. Сначала проверьте подключение.");
        status.setTextSize(14);
        status.setTextColor(0xFF475467);
        status.setPadding(0, dp(18), 0, 0);
        root.addView(status, matchWrap());

        TextView note = new TextView(this);
        note.setText("Частный DNS может помочь только при проблеме DNS. Он не меняет маршрут игрового трафика и обычно не снижает пинг в бою. Используйте DNS-провайдера, которому доверяете.");
        note.setTextSize(13);
        note.setTextColor(0xFF667085);
        note.setPadding(0, dp(22), 0, 0);
        root.addView(note, matchWrap());

        setContentView(root);
    }

    private void openDnsSettings() {
        try {
            startActivity(new Intent(Settings.ACTION_PRIVATE_DNS_SETTINGS));
        } catch (Exception e) {
            try {
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            } catch (Exception ignored) {
                Toast.makeText(this, "Не удалось открыть настройки Android.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showNetworkStatus() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            status.setText("Не удалось получить сведения о сети.");
            return;
        }
        Network network = cm.getActiveNetwork();
        NetworkCapabilities caps = network == null ? null : cm.getNetworkCapabilities(network);
        if (caps == null) {
            status.setText("Нет активного подключения к интернету.");
            return;
        }
        String type;
        if (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) type = "Wi‑Fi";
        else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) type = "мобильная сеть";
        else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) type = "Ethernet";
        else type = "другой тип сети";

        String validated = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                ? "Android считает, что интернет доступен."
                : "Android не подтвердил доступ к интернету.";
        status.setText("Тип сети: " + type + ". " + validated +
                "\nЭто не измеряет пинг до серверов Clash Royale.");
    }

    private void launchGame() {
        Intent launch = getPackageManager().getLaunchIntentForPackage("com.supercell.clashroyale");
        if (launch == null) {
            status.setText("Clash Royale не найдена. Установите игру из официального магазина.");
            return;
        }
        try { startActivity(launch); }
        catch (Exception e) { status.setText("Не удалось запустить игру."); }
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
