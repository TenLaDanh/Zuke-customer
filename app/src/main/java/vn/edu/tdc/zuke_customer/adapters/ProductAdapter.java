package vn.edu.tdc.zuke_customer.adapters;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import vn.edu.tdc.zuke_customer.R;
import vn.edu.tdc.zuke_customer.data_models.Catelogy;
import vn.edu.tdc.zuke_customer.data_models.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Product> items;

    public ProductAdapter(Context context, ArrayList<Product> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_product_goiy, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {
        Product item = items.get(position);
        holder.itemTitle.setText(item.getName());
        holder.itemImage.setImageResource(R.drawable.app);
        holder.itemPrice.setText(String.valueOf(item.getPrice() * 0.3));
        SpannableString noidungspanned = new SpannableString(String.valueOf(item.getPrice()));
        noidungspanned.setSpan(new StrikethroughSpan(), 0, String.valueOf(item.getPrice()).length(), 0);
        holder.itemPriceMain.setText(noidungspanned);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView itemImage;
        private TextView itemTitle, itemPrice, itemPriceMain, itemRating, itemRatingAmount;

        public ViewHolder(View view) {
            super(view);
            itemImage = view.findViewById(R.id.item_image);
            itemTitle = view.findViewById(R.id.item_title);
            itemPrice = view.findViewById(R.id.item_price);
            itemPriceMain = view.findViewById(R.id.item_price_main);
            itemRating = view.findViewById(R.id.item_rating);
            itemRatingAmount = view.findViewById(R.id.item_rating_amount);
        }
    }
}
