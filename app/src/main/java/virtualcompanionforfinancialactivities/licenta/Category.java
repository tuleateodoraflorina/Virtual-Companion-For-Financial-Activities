package virtualcompanionforfinancialactivities.licenta;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class Category {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;
    private String iconName;     // Changed to String (e.g. "ic_food")
    private double budgetLimit;
    private String colorHex;     // Added this field (e.g. "#FF5733")

    // Constructor matching your AppDatabase calls
    public Category(String name, String iconName, double budgetLimit, String colorHex) {
        this.name = name;
        this.iconName = iconName;
        this.budgetLimit = budgetLimit;
        this.colorHex = colorHex;
    }

    // --- Getters ---
    public long getId() { return id; }
    public String getName() { return name; }
    public String getIconName() { return iconName; }
    public double getBudgetLimit() { return budgetLimit; }
    public String getColorHex() { return colorHex; }

    // --- Setters ---
    public void setId(long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setIconName(String iconName) { this.iconName = iconName; }
    public void setBudgetLimit(double budgetLimit) { this.budgetLimit = budgetLimit; }
    public void setColorHex(String colorHex) { this.colorHex = colorHex; }
}