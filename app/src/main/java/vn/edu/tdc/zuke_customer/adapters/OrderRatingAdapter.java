package vn.edu.tdc.zuke_customer.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import vn.edu.tdc.zuke_customer.R;
import vn.edu.tdc.zuke_customer.data_models.Product;
import vn.edu.tdc.zuke_customer.data_models.Rating;

public class OrderRatingAdapter extends RecyclerView.Adapter<OrderRatingAdapter.ViewHolder> {
    Context context;
    ArrayList<Rating> items;
    DatabaseReference proRef = FirebaseDatabase.getInstance().getReference("Products");
    ArrayList<String> list;
    RecommendRatingAdapter adapter;
    String to = "";

    public OrderRatingAdapter(Context context, ArrayList<Rating> items, String to) {
        this.context = context;
        this.items = items;
        this.to = to;
    }

    @NonNull
    @Override
    public OrderRatingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderRatingAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_rating_product, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderRatingAdapter.ViewHolder holder, int position) {
        Rating item = items.get(position);
        // Lấy tên, ảnh từ products:
        proRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    product.setKey(snapshot.getKey());
                    if(product.getKey().equals(item.getProductID())) {
                        holder.txtName.setText(product.getName());
                        StorageReference imageRef = FirebaseStorage.getInstance().getReference("images/products/"
                                + product.getName() + "/" + product.getImage());
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri.toString()).resize(holder.imageView.getWidth(), holder.imageView.getHeight()).into(holder.imageView));
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // RecycleView:
        holder.recyclerView.setHasFixedSize(true);
        list = new ArrayList<String> ();
        list.add("Chất lượng sản phẩm tuyệt vời");
        list.add("Giá cả phù hợp");
        list.add("Rất đáng tiền");
        list.add("Sản phẩm tạm được");
        list.add("Sản phẩm kém chất lượng");
        adapter = new RecommendRatingAdapter(context, list);
//        adapter.setItemClickListener(itemClickListener);
        holder.recyclerView.setAdapter(adapter);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        Log.d("TAG", "onBindViewHolder: " + this.to);
        if(to.equals("read")) {
            holder.recyclerView.setEnabled(false);
            holder.ratingBar.setEnabled(false);
            holder.txtComment.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView txtName;
        RecyclerView recyclerView;
        RatingBar ratingBar;
        EditText txtComment;

        public ViewHolder(View view) {
            super(view);
            txtComment = view.findViewById(R.id.comment);
            recyclerView = view.findViewById(R.id.list);
            ratingBar = view.findViewById(R.id.simpleRatingBar);
            imageView = view.findViewById(R.id.img);
            txtName = view.findViewById(R.id.txt_name);
        }
    }
}
