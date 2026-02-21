package virtualcompanionforfinancialactivities.licenta;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import virtualcompanionforfinancialactivities.licenta.dao.CategoryDao;
import virtualcompanionforfinancialactivities.licenta.dao.PetDao;
import virtualcompanionforfinancialactivities.licenta.dao.TransactionDao;
import virtualcompanionforfinancialactivities.licenta.database.AppDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // --- UI Variables ---
    private EditText inputAmount, inputDesc;
    private TextView textTotal;
    private ImageView imagePet;
    private RecyclerView recyclerView;
    private Spinner spinnerCategory;
    private Spinner spinnerInputCurrency;
    private Spinner spinnerDisplayCurrency;

    // --- Logic Variables ---
    private TransactionDao transactionDao;
    private PetDao petDao;
    private CategoryDao categoryDao;

    private Pet currentPet;
    private TransactionAdapter adapter;
    private List<Category> categoryList = new ArrayList<>();
    private String[] currencies = {"RON", "EUR", "USD"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- CHECK NOTIFICATION PERMISSION ---
        if (!isNotificationServiceEnabled()) {
            Toast.makeText(this, "Please enable Notification Access for auto-tracking", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
        }

        // 1. Initialize Views
        inputAmount = findViewById(R.id.input_amount);
        inputDesc = findViewById(R.id.input_desc);
        Button btnSave = findViewById(R.id.btn_save);
        textTotal = findViewById(R.id.text_total);
        imagePet = findViewById(R.id.image_pet);
        spinnerCategory = findViewById(R.id.spinner_category);
        spinnerInputCurrency = findViewById(R.id.spinner_input_currency);
        spinnerDisplayCurrency = findViewById(R.id.spinner_display_currency);

        // 1.5 Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_view_history);
        adapter = new TransactionAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 2. Initialize Database
        AppDatabase db = AppDatabase.getDatabase(this);
        transactionDao = db.transactionDao();
        petDao = db.petDao();
        categoryDao = db.categoryDao();

        // 3. Setup Logic & Spinners
        initializePet();
        loadCategories();
        setupCurrencySpinners();

        // 4. Load Data
        updateUI();
        loadTransactionList();

        // 5. Button Logic
        btnSave.setOnClickListener(v -> saveTransaction());
    }

    private boolean isNotificationServiceEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (String name : names) {
                final ComponentName cn = ComponentName.unflattenFromString(name);
                if (cn != null && TextUtils.equals(pkgName, cn.getPackageName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void loadCategories() {
        categoryList = categoryDao.getAllCategories();
        List<String> names = new ArrayList<>();
        for (Category c : categoryList) {
            names.add(c.getName());
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(spinnerAdapter);
    }

    private void setupCurrencySpinners() {
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerInputCurrency.setAdapter(currencyAdapter);
        spinnerDisplayCurrency.setAdapter(currencyAdapter);

        // Add a listener to recalculate total instantly when display currency is changed
        spinnerDisplayCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateUI();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void initializePet() {
        currentPet = petDao.getActivePet();
        if (currentPet == null) {
            Pet newPet = new Pet("Bobi", 1, 0, 100.0f, 100.0f, System.currentTimeMillis(), null);
            petDao.insert(newPet);
            currentPet = newPet;
        }
    }

    private void saveTransaction() {
        String amountText = inputAmount.getText().toString();
        String description = inputDesc.getText().toString();

        if (amountText.isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Get the numbers and strings
        double amount = Double.parseDouble(amountText);
        String selectedCurrency = spinnerInputCurrency.getSelectedItem().toString();

        // 2. Get Selected Category ID
        int selectedPosition = spinnerCategory.getSelectedItemPosition();
        long selectedCategoryId = 1; // Default fallback
        if (selectedPosition >= 0 && selectedPosition < categoryList.size()) {
            selectedCategoryId = categoryList.get(selectedPosition).getId();
        }

        // 3. Save to Database (Correct constructor with Currency)
        Transaction t = new Transaction(amount, selectedCurrency, "EXPENSE", System.currentTimeMillis(), selectedCategoryId, description);
        transactionDao.insert(t);

        // 4. Update Pet Happiness
        if (currentPet != null) {
            float newHappiness = currentPet.getHappinessScore() - 5.0f;
            if (newHappiness < 0) newHappiness = 0;

            currentPet.setHappinessScore(newHappiness);
            currentPet.setLastActivityTime(System.currentTimeMillis());
            petDao.update(currentPet);
        }

        // 5. Clear Inputs & Refresh UI
        inputAmount.setText("");
        inputDesc.setText("");

        updateUI();
        loadTransactionList();

        Toast.makeText(this, "Expense Saved!", Toast.LENGTH_SHORT).show();
    }

    private void loadTransactionList() {
        List<Transaction> list = transactionDao.getAllTransactions();
        adapter.setTransactions(list);
    }

    private void updateUI() {
        if (spinnerDisplayCurrency == null || spinnerDisplayCurrency.getSelectedItem() == null) return;

        String displayCurrency = spinnerDisplayCurrency.getSelectedItem().toString();
        List<Transaction> allTransactions = transactionDao.getAllTransactions();

        double totalSpent = 0.0;
        for (Transaction t : allTransactions) {
            totalSpent += convertCurrency(t.getAmount(), t.getCurrency(), displayCurrency);
        }

        textTotal.setText(String.format("Total: %.2f %s", totalSpent, displayCurrency));

        // Update Pet Image
        if (currentPet != null) {
            if (currentPet.getHappinessScore() > 50) {
                imagePet.setImageResource(R.drawable.pet_happy);
            } else {
                imagePet.setImageResource(R.drawable.pet_sad);
            }
        }
    }

    // --- Exchange Rate Helper ---
    private double convertCurrency(double amount, String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) return amount;

        // Step 1: Convert everything to a base currency (RON)
        double amountInRon = amount;
        if (fromCurrency.equals("EUR")) amountInRon = amount * 4.97;
        if (fromCurrency.equals("USD")) amountInRon = amount * 4.60;

        // Step 2: Convert from base (RON) to target currency
        if (toCurrency.equals("RON")) return amountInRon;
        if (toCurrency.equals("EUR")) return amountInRon / 4.97;
        if (toCurrency.equals("USD")) return amountInRon / 4.60;

        return amount; // Fallback
    }
}