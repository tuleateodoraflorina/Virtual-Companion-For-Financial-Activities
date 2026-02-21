package virtualcompanionforfinancialactivities.licenta.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

import virtualcompanionforfinancialactivities.licenta.Transaction;

@Dao
public interface TransactionDao {

    @Insert
    void insert(Transaction transaction);

    // CHANGED 'date' to 'timestamp' here to match the new Entity
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    List<Transaction> getAllTransactions();

    @Query("SELECT SUM(amount) FROM transactions")
    double getTotalSpent();
}

