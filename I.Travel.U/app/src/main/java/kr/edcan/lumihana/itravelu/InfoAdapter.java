package kr.edcan.lumihana.itravelu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by kimok_000 on 2016-10-30.
 */
public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.ViewHolder> {
    private Context context;
    private ArrayList<RealmInfoModel> arrayList;

    public InfoAdapter(Context context, ArrayList<RealmInfoModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final RealmInfoModel model = arrayList.get(position);
        String image_url = model.getPhoto();
        String place_name = model.getName();

        holder.linear_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DetailDialog dialog = new DetailDialog(context, model);
                dialog.show();
            }
        });
        Glide.with(context).load(image_url).into(holder.image_photo);
        holder.text_name.setText(place_name + "");
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout linear_root;
        private ImageView image_photo;
        private TextView text_name;

        public ViewHolder(View itemView) {
            super(itemView);

            linear_root = (LinearLayout) itemView.findViewById(R.id.place_root);
            image_photo = (ImageView) itemView.findViewById(R.id.place_image_photo);
            text_name = (TextView) itemView.findViewById(R.id.place_text_name);
        }
    }
}
