package virtualcompanionforfinancialactivities.licenta;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
//import androidx.room.vo.PrimaryKey;

@Entity(tableName = "categories")
public class Category {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;
    private int iconResId;   //category icon
    private boolean isDefault; //True if this is a built-in category
    private double budgetLimit; //The user's set monthly limit for this category

    // Constructor
    public Category(String name, int iconResId, boolean isDefault, double budgetLimit) {
        this.name = name;
        this.iconResId = iconResId;
        this.isDefault = isDefault;
        this.budgetLimit = budgetLimit;
    }

    // --- Getters ---
    public long getId() { return id; }
    public String getName() { return name; }
    public int getIconResId() { return iconResId; }
    public boolean isDefault() { return isDefault; }
    public double getBudgetLimit() { return budgetLimit; }

    // --- Setters ---
    public void setId(long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setIconResId(int iconResId) { this.iconResId = iconResId; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }
    public void setBudgetLimit(double budgetLimit) { this.budgetLimit = budgetLimit; }
}
