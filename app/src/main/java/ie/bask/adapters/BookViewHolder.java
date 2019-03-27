package ie.bask.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ie.bask.R;

public class BookViewHolder extends RecyclerView.ViewHolder {
    public final ImageView ivCover;
    public final TextView tvTitle;
    public final TextView tvAuthor;
    public final TextView tvDateAdded;

    public BookViewHolder(final View itemView) {
        super(itemView);
        ivCover = itemView.findViewById(R.id.ivBookCover);
        tvTitle = itemView.findViewById(R.id.tvTitle);
        tvAuthor = itemView.findViewById(R.id.tvAuthor);
        tvDateAdded = itemView.findViewById(R.id.tvDateAdded);
    }

}
