package virtualcompanionforfinancialactivities.licenta;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import virtualcompanionforfinancialactivities.licenta.database.AppDatabase;

public class FinancesFragment extends Fragment {

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finances, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new TransactionAdapter();
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppDatabase db = AppDatabase.getDatabase(requireContext());
        adapter.setTransactions(db.transactionDao().getAllTransactions());
    }
}
