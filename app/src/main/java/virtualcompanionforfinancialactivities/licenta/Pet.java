package virtualcompanionforfinancialactivities.licenta;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pets")
public class Pet {

    @PrimaryKey
    private long id = 1; //Assuming a single user/pet, fix the ID at 1

    private String name;
    private int speciesId;
    private int petCoins;
    private float happinessScore; // 0.0 to 1.0
    private float healthScore;    // 0.0 to 1.0
    private long lastActivityTime;
    private Long equippedItemId;  //Can be null if nothing is equipped

    public Pet(String name, int speciesId, int petCoins, float happinessScore, float healthScore, long lastActivityTime, Long equippedItemId) {
        this.name = name;
        this.speciesId = speciesId;
        this.petCoins = petCoins;
        this.happinessScore = happinessScore;
        this.healthScore = healthScore;
        this.lastActivityTime = lastActivityTime;
        this.equippedItemId = equippedItemId;
    }

    // --- Getters ---
    public long getId() { return id; }
    public String getName() { return name; }
    public int getSpeciesId() { return speciesId; }
    public int getPetCoins() { return petCoins; }
    public float getHappinessScore() { return happinessScore; }
    public float getHealthScore() { return healthScore; }
    public long getLastActivityTime() { return lastActivityTime; }
    public Long getEquippedItemId() { return equippedItemId; }


    public void setId(long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setSpeciesId(int speciesId) { this.speciesId = speciesId; }
    public void setPetCoins(int petCoins) { this.petCoins = petCoins; }
    public void setHappinessScore(float happinessScore) { this.happinessScore = happinessScore; }
    public void setHealthScore(float healthScore) { this.healthScore = healthScore; }
    public void setLastActivityTime(long lastActivityTime) { this.lastActivityTime = lastActivityTime; }
    public void setEquippedItemId(Long equippedItemId) { this.equippedItemId = equippedItemId; }
}
