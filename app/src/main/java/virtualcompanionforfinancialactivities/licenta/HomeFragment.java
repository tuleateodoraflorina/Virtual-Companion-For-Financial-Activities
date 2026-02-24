package virtualcompanionforfinancialactivities.licenta;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;
import virtualcompanionforfinancialactivities.licenta.dao.*;
import virtualcompanionforfinancialactivities.licenta.database.AppDatabase;

public class HomeFragment extends Fragment {

    private EditText inputAmount, inputDesc;
    private TextView textTotal;
    private ImageView imagePet;
    private Spinner spinnerCategory, spinnerInputCurrency, spinnerDisplayCurrency;

    private TransactionDao transactionDao;
    private PetDao petDao;
    private CategoryDao categoryDao;

    private List<Category> categoryList = new ArrayList<>();
    private String[] currencies = {"RON", "EUR", "USD"};
    private Pet currentPet;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //init views
        inputAmount = view.findViewById(R.id.input_amount);
        inputDesc = view.findViewById(R.id.input_desc);
        Button btnSave = view.findViewById(R.id.btn_save);
        textTotal = view.findViewById(R.id.text_total);
        imagePet = view.findViewById(R.id.image_pet);
        spinnerCategory = view.findViewById(R.id.spinner_category);
        spinnerInputCurrency = view.findViewById(R.id.spinner_input_currency);
        spinnerDisplayCurrency = view.findViewById(R.id.spinner_display_currency);

        //init db
        AppDatabase db = AppDatabase.getDatabase(requireContext());
        transactionDao = db.transactionDao();
        petDao = db.petDao();
        categoryDao = db.categoryDao();

        loadCategories();
        setupCurrencySpinners();
        initializePet();
        updateUI();

        btnSave.setOnClickListener(v -> saveTransaction());

        return view;
    }

    private void loadCategories() {
        categoryList = categoryDao.getAllCategories();

        if (categoryList.isEmpty()) {
            Category c1 = new Category("Food", "ic_food", 500.0, "#FF5733");
            Category c2 = new Category("Transport", "ic_bus", 100.0, "#33C1FF");
            Category c3 = new Category("Entertainment", "ic_movie", 200.0, "#A033FF");
            Category c4 = new Category("Bills", "ic_bill", 1000.0, "#FFC300");
            Category c5 = new Category("Shopping", "ic_bag", 300.0, "#33FF57");

            categoryDao.insertAll(c1, c2, c3, c4, c5);

            categoryList = categoryDao.getAllCategories();
        }

        // setup adapter as usual
        List<String> names = new ArrayList<>();
        for (Category c : categoryList) {
            names.add(c.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void setupCurrencySpinners() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInputCurrency.setAdapter(adapter);
        spinnerDisplayCurrency.setAdapter(adapter);

        spinnerDisplayCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) { updateUI(); }
            public void onNothingSelected(AdapterView<?> p) {}
        });
    }

    private void initializePet() {
        currentPet = petDao.getActivePet();
        if (currentPet == null) {
            Pet newPet = new Pet("Bobi", 1, 0, 100f, 100f, System.currentTimeMillis(), null);
            petDao.insert(newPet);
            currentPet = newPet;
        }
    }

    private void saveTransaction() {
        String amountText = inputAmount.getText().toString();
        if (amountText.isEmpty()) return;

        double amount = Double.parseDouble(amountText);
        String selectedCurrency = spinnerInputCurrency.getSelectedItem().toString();
        String description = inputDesc.getText().toString();

        int selectedPosition = spinnerCategory.getSelectedItemPosition();
        long selectedCategoryId = 1;
        if (selectedPosition >= 0 && selectedPosition < categoryList.size()) {
            selectedCategoryId = categoryList.get(selectedPosition).getId();
        }

        Transaction t = new Transaction(amount, selectedCurrency, "EXPENSE", System.currentTimeMillis(), selectedCategoryId, description);
        transactionDao.insert(t);

        // pet logic
        if (currentPet != null) {
            float newHappiness = currentPet.getHappinessScore() - 5.0f;
            if (newHappiness < 0) newHappiness = 0;
            currentPet.setHappinessScore(newHappiness);
            petDao.update(currentPet);
        }

        inputAmount.setText("");
        inputDesc.setText("");
        updateUI();
        Toast.makeText(requireContext(), "Saved!", Toast.LENGTH_SHORT).show();
    }

    private void updateUI() {
        if (spinnerDisplayCurrency == null || spinnerDisplayCurrency.getSelectedItem() == null) return;

        String displayCurrency = spinnerDisplayCurrency.getSelectedItem().toString();
        List<Transaction> allTransactions = transactionDao.getAllTransactions();

        double totalSpent = 0.0;
        for (Transaction t : allTransactions) {
            totalSpent += convertCurrency(t.getAmount(), t.getCurrency(), displayCurrency);
        }

        textTotal.setText(String.format("%.2f %s", totalSpent, displayCurrency));

        if (currentPet != null) {
            imagePet.setImageResource(currentPet.getHappinessScore() > 50 ? R.drawable.pet_happy : R.drawable.pet_sad);
        }
    }

    private double convertCurrency(double amount, String from, String to) {
        if (from.equals(to)) return amount;
        double inRon = amount;
        if (from.equals("EUR")) inRon = amount * 5.10;
        if (from.equals("USD")) inRon = amount * 4.33;

        if (to.equals("RON")) return inRon;
        if (to.equals("EUR")) return inRon / 5.10;
        if (to.equals("USD")) return inRon / 4.33;
        return amount;
    }
}