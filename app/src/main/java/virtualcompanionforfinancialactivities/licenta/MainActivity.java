package virtualcompanionforfinancialactivities.licenta;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import virtualcompanionforfinancialactivities.licenta.dao.PetDao;
import virtualcompanionforfinancialactivities.licenta.dao.TransactionDao;
import virtualcompanionforfinancialactivities.licenta.database.AppDatabase;

public class MainActivity extends AppCompatActivity {

    private EditText inputAmount, inputDesc;
    private TextView textTotal;
    private ImageView imagePet;

    private TransactionDao transactionDao;
    private PetDao petDao;
    private Pet currentPet;

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

        // 2. Initialize Database
        AppDatabase db = AppDatabase.getDatabase(this);
        transactionDao = db.transactionDao();
        petDao = db.petDao();

        // 3. Initialize Pet (Check if one exists)
        initializePet();

        // 4. Update displays
        updateUI();

        btnSave.setOnClickListener(v -> saveTransaction());
    }

    private void initializePet() {
        currentPet = petDao.getActivePet();

        if (currentPet == null) {
            // MATCHING YOUR PET CLASS CONSTRUCTOR:
            // (name, speciesId, petCoins, happinessScore, healthScore, lastActivityTime, equippedItemId)
            Pet newPet = new Pet(
                    "Finchy",
                    1,                    // speciesId (e.g., 1 = Cat)
                    0,                    // petCoins
                    100.0f,               // happinessScore (Start full)
                    100.0f,               // healthScore
                    System.currentTimeMillis(),
                    null                  // equippedItemId (null means nothing worn)
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

        // Save Transaction
        // (Ensure Transaction constructor matches what you have in Transaction.java)
        Transaction t = new Transaction(amount, "EXPENSE", System.currentTimeMillis(), 1, description);
        transactionDao.insert(t);

        // --- UPDATE PET HAPPINESS ---
        if (currentPet != null) {
            // Decrease happiness by 5.0
            float newHappiness = currentPet.getHappinessScore() - 5.0f;
            if (newHappiness < 0) newHappiness = 0;

            currentPet.setHappinessScore(newHappiness);

            // Also update the "Last Active" time
            currentPet.setLastActivityTime(System.currentTimeMillis());

            petDao.update(currentPet);
        }

        inputAmount.setText("");
        inputDesc.setText("");
        updateUI();
        Toast.makeText(this, "Expense Saved!", Toast.LENGTH_SHORT).show();
    }

    private void updateUI() {
        // Update Total
        double total = transactionDao.getTotalSpent();
        textTotal.setText("Total Spent: $" + total);

        // Update Pet Image based on Happiness
        if (currentPet != null) {
            // Check if happiness is high (above 50)
            if (currentPet.getHappinessScore() > 50) {
                imagePet.setImageResource(R.drawable.pet_happy);
            } else {
                imagePet.setImageResource(R.drawable.pet_sad);
                Toast.makeText(this, "Your pet is sad!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}