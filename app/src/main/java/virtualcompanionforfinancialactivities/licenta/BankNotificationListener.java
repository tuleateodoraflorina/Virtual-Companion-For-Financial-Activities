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
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        transactionDao = db.transactionDao();
        petDao = db.petDao();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();
        Notification notification = sbn.getNotification();

        String title = notification.extras.getString(Notification.EXTRA_TITLE, "");
        String text = notification.extras.getString(Notification.EXTRA_TEXT, "");

        Log.d("BankListener", "Notification from: " + packageName + " | Text: " + text);

        // extract using regex
        String regex = "(?i)(?:ron|lei|eur|euro|€|\\$|usd)\\s*(\\d+([.,]\\d{1,2})?)|(\\d+([.,]\\d{1,2})?)\\s*(?:ron|lei|eur|euro|€|\\$|usd)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            String amountStr = matcher.group(1) != null ? matcher.group(1) : matcher.group(3);
            amountStr = amountStr.replace(",", ".");

            try {
                double amount = Double.parseDouble(amountStr);

                //detect currency
                String lowerText = text.toLowerCase();
                String detectedCurrency = "RON"; // Default to RON
                if (lowerText.contains("eur") || lowerText.contains("euro") || lowerText.contains("€")) {
                    detectedCurrency = "EUR";
                } else if (lowerText.contains("usd") || lowerText.contains("$")) {
                    detectedCurrency = "USD";
                }

                final String finalCurrency = detectedCurrency;

                // save to db
                Executors.newSingleThreadExecutor().execute(() -> {

                    Transaction t = new Transaction(amount, finalCurrency, "EXPENSE", System.currentTimeMillis(), 1, "Auto: " + title);
                    transactionDao.insert(t);

                    Pet currentPet = petDao.getActivePet();
                    if (currentPet != null) {
                        float newHappiness = currentPet.getHappinessScore() - 5.0f;
                        if (newHappiness < 0) newHappiness = 0;
                        currentPet.setHappinessScore(newHappiness);
                        petDao.update(currentPet);
                    }

                    Log.d("BankListener", "Successfully logged auto-expense: " + amount + " " + finalCurrency);
                });

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
    }
}