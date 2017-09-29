package kr.edcan.lumihana.itravelu;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by kimok_000 on 2016-10-30.
 */
public class DetailDialog extends Dialog {
    private RealmInfoModel realmInfoModel;
    private FirebaseDatabase firebaseDatabase;

    private ImageView image_photo;
    private ImageView image_location;
    private ImageView image_share;
    private TextView text_tag;
    private TextView text_name;
    private TextView text_desc;
    private TextView text_add;

    public DetailDialog(Context context, RealmInfoModel realmInfoModel) {
        super(context);

        this.realmInfoModel = realmInfoModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_detail);

        image_photo = (ImageView) findViewById(R.id.detail_image_photo);
        image_location = (ImageView) findViewById(R.id.detail_image_location);
        image_share = (ImageView) findViewById(R.id.detail_image_share);
        text_add = (TextView) findViewById(R.id.detail_text_add);
        text_desc = (TextView) findViewById(R.id.detail_text_desc);
        text_name = (TextView) findViewById(R.id.detail_text_name);
        text_tag = (TextView) findViewById(R.id.detail_text_tag);

        final String photoUrl = realmInfoModel.getPhoto();
        final String desc = realmInfoModel.getAbout();
        final String name = realmInfoModel.getName();
        final String tag = realmInfoModel.getTag();
        final double lat = realmInfoModel.getLat();
        final double lon = realmInfoModel.getLong();

        Glide.with(getContext()).load(photoUrl).into(image_photo);
        text_desc.setText(desc + "");
        text_name.setText(name + "");
        text_tag.setText(tag + "");

        image_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("geo:" + lat + "," + lon);
                Intent map = new Intent(Intent.ACTION_VIEW, uri);
                getContext().startActivity(map);
            }
        });

        image_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.addCategory(Intent.CATEGORY_DEFAULT);
                share.putExtra(Intent.EXTRA_SUBJECT, tag);
                share.putExtra(Intent.EXTRA_TEXT, desc);
                share.putExtra(Intent.EXTRA_TITLE, name);
                share.setType("text/plain");

                getContext().startActivity(Intent.createChooser(share, "공유"));
            }
        });

        text_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddAction(name);
            }
        });
    }

    private void onAddAction(String name) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Setting", getContext().MODE_PRIVATE);
        final String userId = sharedPreferences.getString("userId","");

        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        final DatabaseReference root = firebaseDatabase.getReference().getRoot();
        final DatabaseReference user = root.getRef().child("user");
        final DatabaseReference myUserReference = user.child(userId+"");
        final DatabaseReference favorite = myUserReference.child("favorite");

        favorite.child(name).setValue("null", new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    Toast.makeText(getContext(), "task complete", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "an error occured : " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }
        });
    }
}
