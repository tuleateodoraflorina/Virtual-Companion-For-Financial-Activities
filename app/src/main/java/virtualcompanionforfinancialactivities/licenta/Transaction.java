package virtualcompanionforfinancialactivities.licenta;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class Transaction {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private double amount;
    private String currency; // <--- The new currency field
    private String type;
    private long timestamp;  // <--- Renamed from date to timestamp
    private long categoryId;
    private String description;

    // Updated Constructor
    public Transaction(double amount, String currency, String type, long timestamp, long categoryId, String description) {
        this.amount = amount;
        this.currency = currency;
        this.type = type;
        this.timestamp = timestamp;
        this.categoryId = categoryId;
        this.description = description;
    }

    // --- Getters ---
    public long getId() { return id; }
    public double getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getType() { return type; }
    public long getTimestamp() { return timestamp; }
    public long getCategoryId() { return categoryId; }
    public String getDescription() { return description; }

    // --- Setters ---
    public void setId(long id) { this.id = id; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setType(String type) { this.type = type; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setCategoryId(long categoryId) { this.categoryId = categoryId; }
    public void setDescription(String description) { this.description = description; }
}