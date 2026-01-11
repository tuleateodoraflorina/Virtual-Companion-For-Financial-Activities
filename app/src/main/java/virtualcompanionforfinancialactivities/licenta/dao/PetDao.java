package virtualcompanionforfinancialactivities.licenta.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import virtualcompanionforfinancialactivities.licenta.Pet;

@Dao
public interface PetDao {
    @Insert
    void insert(Pet pet);

    @Update
    void update(Pet pet);

    // We only need the one active pet for now
    @Query("SELECT * FROM pets LIMIT 1")
    Pet getActivePet();
}
