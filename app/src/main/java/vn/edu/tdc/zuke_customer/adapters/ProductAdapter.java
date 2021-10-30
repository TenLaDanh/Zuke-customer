package vn.edu.tdc.zuke_customer.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import vn.edu.tdc.zuke_customer.R;
import vn.edu.tdc.zuke_customer.data_models.OfferDetail;
import vn.edu.tdc.zuke_customer.data_models.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Product> items;
    ProductAdapter.ItemClick itemClick;
    DatabaseReference offerDetailRef = FirebaseDatabase.getInstance().getReference("Offer_Details");

    public ProductAdapter(Context context, ArrayList<Product> items) {
        this.context = context;
        this.items = items;
    }

    public void setItemClickListener(ProductAdapter.ItemClick itemClickListener) {
        this.itemClick = itemClickListener;
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
        // Load ảnh
        StorageReference imageRef = FirebaseStorage.getInstance().getReference("images/products/" + item.getName() + "/" + item.getImage());
        imageRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri.toString()).fit().into(holder.itemImage));
        //Giá
        offerDetailRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int maxSale = 0;
                for (DataSnapshot node1 : snapshot.getChildren()) {
                    OfferDetail detail = node1.getValue(OfferDetail.class);
                    if (detail.getProductID().equals(item.getKey())) {
                        if (detail.getPercentSale() > maxSale) {
                            maxSale = detail.getPercentSale();
                        }
                    }
                }
                if (maxSale != 0) {
                    int discount = item.getPrice() / 100 * (100 - maxSale);
                    holder.itemPrice.setText(formatPrice(discount));
                    holder.itemPriceMain.setText(formatPrice(item.getPrice()));
                    holder.itemPriceMain.setPaintFlags(holder.itemPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    holder.itemPrice.setText(formatPrice(item.getPrice()));
                    holder.itemPriceMain.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //Rating:
        if (item.getRating() > 0) {
            holder.itemRating.setText(item.getRating() + "");
        } else {
            holder.itemRating.setVisibility(View.GONE);
        }
        //Đã bán:
        if (item.getSold() > 0) {
            holder.itemRatingAmount.setText(item.getSold() + " đã bán");
        } else {
            holder.itemRatingAmount.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(v -> {
            if(itemClick != null) {
                itemClick.getDetailProduct(item);
            } else return;
        });
    }

    private String formatPrice(int price) {
        String s = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"))
                .format(price);
        return s.substring(2, s.length()) + " ₫";
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

    public interface ItemClick {
        void getDetailProduct(Product item);
    }
}
