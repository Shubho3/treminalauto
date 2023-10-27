package com.pay.treminalauto;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.stripe.stripeterminal.external.models.Reader;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Our [RecyclerView.Adapter] implementation that allows us to update the list of events
 */
public class ReaderssAdapter extends RecyclerView.Adapter<ReaderssAdapter.EventHolder> {
    @NotNull private List<Reader> events;
    @NotNull private OnClicked clicked;
    @NotNull private Context context ;

    public ReaderssAdapter(Context context,OnClicked clicked) {
        super();
        events = new ArrayList<>();
        this.clicked= clicked;
        this.context= context;
    }

    void updateEvents(@NotNull List<Reader> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    @Override
    public void onBindViewHolder(@NotNull EventHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.bind(events.get(position));
        holder.itemView.setOnClickListener(v -> clicked.onItemClicked(events.get(position)));
    }

    @NotNull
    @Override
    public EventHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new EventHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_readers, parent, false));
    }

    public class EventHolder extends RecyclerView.ViewHolder {

        public EventHolder(
                @NotNull View itemView
        ) {
            super(itemView);
        }

        public void bind(@NotNull Reader event) {
            ((TextView) itemView.findViewById(R.id.label)).setText(event.getLabel());
        }
    }

   public interface OnClicked{
        void onItemClicked(Reader id);


    }
}
