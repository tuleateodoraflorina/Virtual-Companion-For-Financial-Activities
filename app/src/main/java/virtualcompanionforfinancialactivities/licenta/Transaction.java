package virtualcompanionforfinancialactivities.licenta;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class Transaction {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private double amount;
    private String type;
    private long date;
    private long categoryId;
    private String description;

    public Transaction(double amount, String type, long date, long categoryId, String description) {
        this.amount = amount;
        this.type = type;
        this.date = date;
        this.categoryId = categoryId;
        this.description = description;
    }


    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public long getDate() {
        return date;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
