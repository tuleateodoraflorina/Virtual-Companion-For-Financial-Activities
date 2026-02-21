package virtualcompanionforfinancialactivities.licenta;

import android.app.Notification;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import virtualcompanionforfinancialactivities.licenta.dao.PetDao;
import virtualcompanionforfinancialactivities.licenta.dao.TransactionDao;
import virtualcompanionforfinancialactivities.licenta.database.AppDatabase;

public class BankNotificationListener extends NotificationListenerService {

    private TransactionDao transactionDao;
    private PetDao petDao;

    @Override
    public void onCreate() {
        super.onCreate();
        // Connect to the database when the service starts
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        transactionDao = db.transactionDao();
        petDao = db.petDao();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        // 1. Get the notification details
        String packageName = sbn.getPackageName();
        Notification notification = sbn.getNotification();

        String title = notification.extras.getString(Notification.EXTRA_TITLE, "");
        String text = notification.extras.getString(Notification.EXTRA_TEXT, "");

        Log.d("BankListener", "Notification from: " + packageName + " | Text: " + text);

        // 2. Filter apps (Optional: Only listen to specific banking apps or SMS)
        // For testing, let's just listen to messages or dummy notifications
        // if (!packageName.contains("bank") && !packageName.contains("sms")) return;

        // 3. Extract Money Amount using Regex
        // (?i) makes it case-insensitive (matches LEI, lei, Lei)
        // It looks for currency BEFORE the number (e.g., $15.50, €20)
        // OR currency AFTER the number (e.g., 45.50 RON, 120 lei, 50 EUR)
        String regex = "(?i)(?:ron|lei|eur|euro|€|\\$|usd)\\s*(\\d+([.,]\\d{1,2})?)|(\\d+([.,]\\d{1,2})?)\\s*(?:ron|lei|eur|euro|€|\\$|usd)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            // If the currency was BEFORE the number, it's saved in Group 1.
            // If the currency was AFTER the number, it's saved in Group 3.
            String amountStr = matcher.group(1) != null ? matcher.group(1) : matcher.group(3);

            // European banks often use commas for decimals (e.g., 50,50 RON).
            // Java needs dots (50.50) to do math, so we replace it here.
            amountStr = amountStr.replace(",", ".");

            try {
                double amount = Double.parseDouble(amountStr);

                // 4. Save to Database on a background thread
                Executors.newSingleThreadExecutor().execute(() -> {
                    // ... (Keep your existing database saving code here) ...
                    Transaction t = new Transaction(amount, "EXPENSE", System.currentTimeMillis(), 1, "Auto: " + title);
                    transactionDao.insert(t);

                    Pet currentPet = petDao.getActivePet();
                    if (currentPet != null) {
                        float newHappiness = currentPet.getHappinessScore() - 5.0f;
                        if (newHappiness < 0) newHappiness = 0;
                        currentPet.setHappinessScore(newHappiness);
                        petDao.update(currentPet);
                    }

                    Log.d("BankListener", "Successfully logged auto-expense: " + amount);
                });

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // Not needed for now
    }
}