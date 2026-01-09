package virtualcompanionforfinancialactivities.licenta.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;
import virtualcompanionforfinancialactivities.licenta.Transaction;

@Dao
public interface TransactionDao<Transaction> {
    @Insert
    void insert(Transaction transaction);

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    List<Transaction> getAllTransactions();

    @Query("SELECT SUM(amount) FROM transactions")
    double getTotalSpent();
}

