package ie.bask.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ie.bask.PicassoTrustAll;
import ie.bask.R;
import ie.bask.models.Book;

public class BookAdapter extends ArrayAdapter<Book> {

    private static class ViewHolder {
        public ImageView ivCover;
        public TextView tvTitle;
        public TextView tvAuthor;
        public TextView tvDateAdded;
    }

    public BookAdapter(Context context, ArrayList<Book> aBooks) {
        super(context, 0, aBooks);
    }

    // Translates a particular Book object given a position
    // into a relevant row within an AdapterView
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Book book = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.book_item, parent, false);
            viewHolder.ivCover = convertView.findViewById(R.id.ivBookCover);
            viewHolder.tvTitle = convertView.findViewById(R.id.tvTitle);
            viewHolder.tvAuthor = convertView.findViewById(R.id.tvAuthor);
            viewHolder.tvDateAdded = convertView.findViewById(R.id.tvDateAdded);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate the data into the template view using the Book object
        viewHolder.tvTitle.setText(book.getTitle());
        viewHolder.tvAuthor.setText(book.getAuthor());

        // Use custom Picasso instance to fetch book cover
        PicassoTrustAll.getInstance(getContext())
                .load(Uri.parse(book.getImageLink()))
                .fit().centerInside().error(R.drawable.ic_nocover).into(viewHolder.ivCover);

//        // Use Picasso to fetch book cover
//        Picasso.get()
//                .load(Uri.parse(book.getImageLink()))
//                .fit()
//                .centerInside()
//                .error(R.drawable.ic_nocover)
//                .into(viewHolder.ivCover);

        if(book.getDateAdded()!=null){
            viewHolder.tvDateAdded.setText(book.getDateAdded().toString());
        }

        return convertView;
    }
}