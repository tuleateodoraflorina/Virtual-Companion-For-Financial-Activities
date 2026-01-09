package virtualcompanionforfinancialactivities.licenta;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

//if a ShopItem is deleted from the game code,
//it is also removed from the user's inventory to prevent crashes.
@Entity(tableName = "inventory",
        foreignKeys = @ForeignKey(entity = Shop.class,
                parentColumns = "id",
                childColumns = "shopItemId",
                onDelete = ForeignKey.CASCADE))
public class Inventory {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long shopItemId;  //Links to the ShopItem table
    private boolean isEquipped; //Is the pet currently wearing this?

    public Inventory(long shopItemId, boolean isEquipped) {
        this.shopItemId = shopItemId;
        this.isEquipped = isEquipped;
    }


    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getShopItemId() { return shopItemId; }
    public void setShopItemId(long shopItemId) { this.shopItemId = shopItemId; }

    public boolean isEquipped() { return isEquipped; }
    public void setEquipped(boolean equipped) { isEquipped = equipped; }
}


