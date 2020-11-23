package com.aashdit.ipms.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aashdit.ipms.R;
import com.aashdit.ipms.models.TempBill;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * Created by Manabendu on 17/06/20
 */
public class RunningBillsAdapter extends RecyclerView.Adapter<RunningBillsAdapter.TempBillViewHolder> {

    private static final String TAG = RunningBillsAdapter.class.getSimpleName();

    private Context mContext;
    private List<TempBill> bills;


    public RunningBillsAdapter(Context context, List<TempBill> data) {
        this.mContext = context;
        this.bills = data;
    }


    @NonNull
    @Override
    public TempBillViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cell_running_bills, viewGroup, false);

        return new TempBillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TempBillViewHolder holder, int position) {
        // include binding logic here
        TempBill bill = bills.get(position);
        holder.amount.setText(mContext.getResources().getString(R.string.Rs).concat(" ").concat(bill.amount));
        holder.invoice.setText(bill.invoice);
        holder.title.setText(bill.title);
//        if (position % 2 ==0){
//            holder.title.setBackgroundColor(mContext.getResources().getColor(R.color.colorlightBlue));
//        }else {
//            holder.title.setBackgroundColor(mContext.getResources().getColor(R.color.colorLightBlack));
//        }
//        holder.fabUpdate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onNavigateListener.onNavigationClick("UPDATE");
//            }
//        });
    }

    OnNavigateListener onNavigateListener;

    public void setOnNavigateListener(OnNavigateListener onNavigateListener) {
        this.onNavigateListener = onNavigateListener;
    }

    public interface OnNavigateListener{
        void onNavigationClick(String type);
    }


    @Override
    public int getItemCount() {
        return bills.size();
    }


    public static class TempBillViewHolder extends RecyclerView.ViewHolder {
        TextView title,amount,invoice;
        FloatingActionButton fabUpdate;
        public TempBillViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_cell_bill_title);
            amount = itemView.findViewById(R.id.tv_cell_bill_amount);
            invoice = itemView.findViewById(R.id.tv_cell_bill_invoice);
            fabUpdate = itemView.findViewById(R.id.fab_update);
        }
    }
}