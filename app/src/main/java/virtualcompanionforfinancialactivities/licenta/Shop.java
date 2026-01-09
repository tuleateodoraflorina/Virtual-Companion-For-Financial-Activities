package virtualcompanionforfinancialactivities.licenta;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "shop_items")
public class Shop {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;      //"Blue Hat"
    private String type;      //"HAT", "BACKGROUND", "GLASSES"
    private int cost;         //Price in PetCoins
    private int imageResId;   //Reference to image

    public Shop(String name, String type, int cost, int imageResId) {
        this.name = name;
        this.type = type;
        this.cost = cost;
        this.imageResId = imageResId;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getCost() { return cost; }
    public void setCost(int cost) { this.cost = cost; }

    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
}

