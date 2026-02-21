package virtualcompanionforfinancialactivities.licenta;

import android.os.Bundle;
import android.widget.ArrayAdapter; // NEW
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;      // NEW
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import virtualcompanionforfinancialactivities.licenta.dao.CategoryDao; // NEW
import virtualcompanionforfinancialactivities.licenta.dao.PetDao;
import virtualcompanionforfinancialactivities.licenta.dao.TransactionDao;
import virtualcompanionforfinancialactivities.licenta.database.AppDatabase;

import java.util.ArrayList; // NEW
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // --- UI Variables ---
    private EditText inputAmount, inputDesc;
    private TextView textTotal;
    private ImageView imagePet;
    private RecyclerView recyclerView;
    private Spinner spinnerCategory; // <--- NEW: The Dropdown

    // --- Logic Variables ---
    private TransactionDao transactionDao;
    private PetDao petDao;
    private CategoryDao categoryDao; // <--- NEW: To talk to Category table

    private Pet currentPet;
    private TransactionAdapter adapter;

    // We need this list to match the dropdown selection to the real Category ID
    private List<Category> categoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Initialize Views
        inputAmount = findViewById(R.id.input_amount);
        inputDesc = findViewById(R.id.input_desc);
        Button btnSave = findViewById(R.id.btn_save);
        textTotal = findViewById(R.id.text_total);
        imagePet = findViewById(R.id.image_pet);
        spinnerCategory = findViewById(R.id.spinner_category); // <--- NEW

        // 1.5 Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_view_history);
        adapter = new TransactionAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 2. Initialize Database
        AppDatabase db = AppDatabase.getDatabase(this);
        transactionDao = db.transactionDao();
        petDao = db.petDao();
        categoryDao = db.categoryDao(); // <--- NEW

        // 3. Setup Logic
        initializePet();
        loadCategories(); // <--- NEW: Fill the dropdown!

        updateUI();
        loadTransactionList();

        // 5. Button Logic
        btnSave.setOnClickListener(v -> saveTransaction());
    }

    // --- NEW METHOD: Loads categories from DB into Spinner ---
    private void loadCategories() {
        // 1. Get raw list from DB
        categoryList = categoryDao.getAllCategories();

        // 2. We only want the NAMES to show in the dropdown (Strings)
        List<String> names = new ArrayList<>();
        for (Category c : categoryList) {
            names.add(c.getName());
        }

        // 3. Create the adapter (bridge between list and spinner)
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, names);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // 4. Set it
        spinnerCategory.setAdapter(spinnerAdapter);
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

        if (amountText.isEmpty()) return;

        double amount = Double.parseDouble(amountText);

        // --- NEW LOGIC: Get Selected Category ---
        int selectedPosition = spinnerCategory.getSelectedItemPosition();

        // Safety check: if list is empty (shouldn't happen), use default 1
        long selectedCategoryId = 1;
        if (selectedPosition >= 0 && selectedPosition < categoryList.size()) {
            selectedCategoryId = categoryList.get(selectedPosition).getId();
        }

        // 1. Save Transaction with REAL Category ID
        Transaction t = new Transaction(amount, "EXPENSE", System.currentTimeMillis(), selectedCategoryId, description);
        transactionDao.insert(t);

        // 2. Update Pet Happiness
        if (currentPet != null) {
            float newHappiness = currentPet.getHappinessScore() - 5.0f;
            if (newHappiness < 0) newHappiness = 0;

            currentPet.setHappinessScore(newHappiness);
            currentPet.setLastActivityTime(System.currentTimeMillis());
            petDao.update(currentPet);
        }

        // 3. Clear & Refresh
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
        double total = transactionDao.getTotalSpent();
        textTotal.setText("Total Spent: $" + total);

        if (currentPet != null) {
            if (currentPet.getHappinessScore() > 50) {
                imagePet.setImageResource(R.drawable.pet_happy);
            } else {
                imagePet.setImageResource(R.drawable.pet_sad);
            }
        }
    }
}