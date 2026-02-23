package virtualcompanionforfinancialactivities.licenta.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.Executors;

import virtualcompanionforfinancialactivities.licenta.Category;
import virtualcompanionforfinancialactivities.licenta.Inventory;
import virtualcompanionforfinancialactivities.licenta.Pet;
import virtualcompanionforfinancialactivities.licenta.Shop;
import virtualcompanionforfinancialactivities.licenta.Transaction;
import virtualcompanionforfinancialactivities.licenta.dao.CategoryDao;
import virtualcompanionforfinancialactivities.licenta.dao.PetDao;
import virtualcompanionforfinancialactivities.licenta.dao.TransactionDao;

@Database(entities = {Transaction.class, Pet.class, Category.class, Shop.class, Inventory.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    public abstract TransactionDao transactionDao();
    public abstract PetDao petDao();
    public abstract CategoryDao categoryDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "finance_pet_database")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .addCallback(roomCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // This runs on a background thread when the DB is first created
            Executors.newSingleThreadExecutor().execute(() -> {
                CategoryDao dao = INSTANCE.categoryDao();

                Category c1 = new Category("Food", "ic_food", 500.0, "#FF5733");
                Category c2 = new Category("Transport", "ic_bus", 100.0, "#33C1FF");
                Category c3 = new Category("Entertainment", "ic_movie", 200.0, "#A033FF");
                Category c4 = new Category("Bills", "ic_bill", 1000.0, "#FFC300");
                Category c5 = new Category("Shopping", "ic_bag", 300.0, "#33FF57");

                dao.insertAll(c1, c2, c3, c4, c5);
            });
        }
    };
}