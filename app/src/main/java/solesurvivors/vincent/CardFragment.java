package solesurvivors.vincent;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


public class CardFragment extends Fragment {

    private CardView cardView;
    TextView title;
    ImageView imageView;
    public static Fragment getInstance(int position) {
        CardFragment f = new CardFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        f.setArguments(args);

        return f;
    }

    @SuppressLint("DefaultLocale")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_viewpager, container, false);

        cardView = (CardView) view.findViewById(R.id.cardView);
        cardView.setMaxCardElevation(cardView.getCardElevation() * CardAdapter.MAX_ELEVATION_FACTOR);




        title = (TextView) view.findViewById(R.id.title);
        imageView=(ImageView) view.findViewById(R.id.image);
      /*  for(int i=0;i<20;i++) {
            System.out.println("sjkdjksdhfkjshdfk" + i);
            System.out.println(AnimeInfoPage.key_arr[i]);
        }*/
        System.out.println(AnimeInfoPage.key_arr[getArguments().getInt("position")]);

        DatabaseReference mReference= FirebaseDatabase.getInstance().getReference().child("Anime").child( AnimeInfoPage.key_arr[getArguments().getInt("position")]).child("name");
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name=dataSnapshot.getValue(String.class);
                CardFragment.this.title.setText(name);
                setImage(AnimeInfoPage.key_arr[getArguments().getInt("position")],imageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

      //  title.setText(String.format("Card %s", AnimeInfoPage.key_arr[getArguments().getInt("position")]));

        return view;
    }

    public CardView getCardView() {
        return cardView;
    }

    public void setImage(String key,final ImageView img) {
        String temp=key+".jpg";
        System.out.println(temp);
        StorageReference filePath= FirebaseStorage.getInstance().getReference().child("AnimeImages").child(temp);

        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                System.out.println(uri);
                Picasso.get().load(uri).fit().centerCrop().into(img);
            }
        });




    }


}
