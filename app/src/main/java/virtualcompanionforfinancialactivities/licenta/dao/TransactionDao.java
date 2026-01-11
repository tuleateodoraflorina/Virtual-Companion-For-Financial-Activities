package virtualcompanionforfinancialactivities.licenta.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

// 1. Correct import pointing to your actual class
import virtualcompanionforfinancialactivities.licenta.Transaction;

@Dao
// 2. Removed "<Transaction>" so it uses the real class, not a placeholder
public interface TransactionDao {

    @Insert
    void insert(Transaction transaction);

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    List<Transaction> getAllTransactions();

    @Query("SELECT SUM(amount) FROM transactions")
    double getTotalSpent();
}

