package virtualcompanionforfinancialactivities.licenta.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;
import virtualcompanionforfinancialactivities.licenta.Category;

@Dao
public interface CategoryDao {
    @Insert
    void insert(Category category);

    @Insert
    void insertAll(Category... categories);

    @Query("SELECT * FROM categories")
    List<Category> getAllCategories();

    @Query("SELECT COUNT(*) FROM categories")
    int getCount();
}
