package virtualcompanionforfinancialactivities.licenta.database;


import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import org.tensorflow.lite.support.label.Category;

import virtualcompanionforfinancialactivities.licenta.Transaction;
import virtualcompanionforfinancialactivities.licenta.Pet;
import virtualcompanionforfinancialactivities.licenta.Shop;
import virtualcompanionforfinancialactivities.licenta.Inventory;
import virtualcompanionforfinancialactivities.licenta.dao.TransactionDao;

// List ALL your tables here
@Database(entities = {Transaction.class, Pet.class, Category.class, Shop.class, Inventory.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract TransactionDao transactionDao();
    // public abstract PetDao petDao(); // Add this when you create PetDao

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "finance_pet_database")
                            .allowMainThreadQueries() // Temporary for testing!
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

