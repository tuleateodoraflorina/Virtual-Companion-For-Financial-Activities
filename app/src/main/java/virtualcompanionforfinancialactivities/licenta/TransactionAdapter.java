package virtualcompanionforfinancialactivities.licenta;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactionList = new ArrayList<>();

    // Call this method to update the list whenever data changes
    public void setTransactions(List<Transaction> transactions) {
        this.transactionList = transactions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction current = transactionList.get(position);

        // 1. Set Description
        holder.descText.setText(current.getDescription());

        // 2. Set Amount AND Currency (e.g., "50.00 RON")
        holder.amountText.setText(String.format("%.2f %s", current.getAmount(), current.getCurrency()));

        // 3. Format Date (Uses getTimestamp() to match your updated database)
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault());
        holder.dateText.setText(sdf.format(new Date(current.getTimestamp())));
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    // This class holds the view elements for one row
    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView descText, dateText, amountText;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            descText = itemView.findViewById(R.id.text_desc);
            dateText = itemView.findViewById(R.id.text_date);
            amountText = itemView.findViewById(R.id.text_amount);
        }
    }
}