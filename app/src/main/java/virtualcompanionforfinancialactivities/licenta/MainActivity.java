package virtualcompanionforfinancialactivities.licenta;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager; // Import needed
import androidx.recyclerview.widget.RecyclerView;       // Import needed

import virtualcompanionforfinancialactivities.licenta.dao.PetDao;
import virtualcompanionforfinancialactivities.licenta.dao.TransactionDao;
import virtualcompanionforfinancialactivities.licenta.database.AppDatabase;

import java.util.List; // Import needed for the list

public class MainActivity extends AppCompatActivity {

    // --- UI Variables ---
    private EditText inputAmount, inputDesc;
    private TextView textTotal;
    private ImageView imagePet;
    private RecyclerView recyclerView; // <--- You were missing this

    // --- Logic Variables ---
    private TransactionDao transactionDao;
    private PetDao petDao;
    private Pet currentPet;
    private TransactionAdapter adapter; // <--- You were missing this

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

        // 1.5 Initialize RecyclerView (The List)
        recyclerView = findViewById(R.id.recycler_view_history);
        adapter = new TransactionAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 2. Initialize Database
        AppDatabase db = AppDatabase.getDatabase(this);
        transactionDao = db.transactionDao();
        petDao = db.petDao();

        // 3. Initialize Pet
        initializePet();

        // 4. Update displays
        updateUI();
        loadTransactionList(); // Load the list when app starts

        // 5. Button Logic
        btnSave.setOnClickListener(v -> saveTransaction());
    }

    private void initializePet() {
        currentPet = petDao.getActivePet();

        if (currentPet == null) {
            Pet newPet = new Pet(
                    "Bobi",
                    1, 0, 100.0f, 100.0f,
                    System.currentTimeMillis(), null
            );
            petDao.insert(newPet);
            currentPet = newPet;
        }
    }

    private void saveTransaction() {
        String amountText = inputAmount.getText().toString();
        String description = inputDesc.getText().toString();

        if (amountText.isEmpty()) return;

        double amount = Double.parseDouble(amountText);

        // 1. Save Transaction to DB
        Transaction t = new Transaction(amount, "EXPENSE", System.currentTimeMillis(), 1, description);
        transactionDao.insert(t);

        // 2. Update Pet Happiness
        if (currentPet != null) {
            float newHappiness = currentPet.getHappinessScore() - 5.0f;
            if (newHappiness < 0) newHappiness = 0;

            currentPet.setHappinessScore(newHappiness);
            currentPet.setLastActivityTime(System.currentTimeMillis());
            petDao.update(currentPet);
        }

        // 3. Clear Inputs and Refresh UI
        inputAmount.setText("");
        inputDesc.setText("");

        updateUI();              // Update the total and pet image
        loadTransactionList();   // <--- IMPORTANT: Refresh the list to show new item!

        Toast.makeText(this, "Expense Saved!", Toast.LENGTH_SHORT).show();
    }

    // --- Helper Methods ---

    // This method must be OUTSIDE saveTransaction
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