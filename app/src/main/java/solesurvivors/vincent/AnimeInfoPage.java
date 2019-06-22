package solesurvivors.vincent;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class AnimeInfoPage extends AppCompatActivity {

    private DatabaseReference mDatabase;

    private String post_key=null;
    private Context context;

    TextView anime_name;
    TextView anime_genre;
    TextView anime_episode;
    TextView anime_rating;
    TextView anime_type;

    public static String[] key_arr=new String[25]; //This array will store the top 20 more like this card view keys

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_info_page);

        anime_name = (TextView) findViewById(R.id.anime_name);
        anime_genre = (TextView) findViewById(R.id.anime_genre);
        anime_episode = (TextView) findViewById(R.id.anime_episode);
        anime_rating = (TextView) findViewById(R.id.anime_rating);
        anime_type = (TextView) findViewById(R.id.anime_type);


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Anime");

        post_key = getIntent().getExtras().getString("anime_id");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = (String) dataSnapshot.child(post_key).child("name").getValue();
                String genre = (String) dataSnapshot.child(post_key).child("genre").getValue();
                String episode = (String) dataSnapshot.child(post_key).child("episode").getValue();
                String rating = (String) dataSnapshot.child(post_key).child("rating").getValue();
                String type = (String) dataSnapshot.child(post_key).child("type").getValue();

                anime_name.setText(name);
                anime_genre.setText(genre);
                anime_episode.setText(episode);
                anime_rating.setText(rating);
                anime_type.setText(type);
                setImage(post_key);


                Iterable<DataSnapshot> anime_Children = dataSnapshot.getChildren();
                ArrayList<Pair<String,Float>> track_it = new ArrayList<Pair <String,Float>>();
                for(DataSnapshot anime_child : anime_Children) {

                    try {


                        Map<String, Object> map = new HashMap<String, Object>();

                        GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {
                        };
                        map = (HashMap<String, Object>) anime_child.getValue(genericTypeIndicator);
                        String mkey = anime_child.getKey();
                        String mgenre = (String) map.get("genre").toString();
                        String mrating = (String) map.get("rating").toString();
                        String mname = (String) map.get("name").toString();
                        float float_rating;
                        if (mrating.compareToIgnoreCase("nan") == 0)
                            float_rating = 0.0f;
                        else
                            float_rating = Float.parseFloat(mrating);
                        String text[] = mgenre.split(", ");
                        String pattern[] = genre.split(", ");
                        //System.out.println("asdads   "+mgenre);
                        float score = float_rating * 5;
                        for (int i = 0; i < text.length; i++) {
                            for (int j = 0; j < pattern.length; j++) {
                                if (name.compareToIgnoreCase(mname) == 0) {
                                    continue;
                                } else if (text[i].compareToIgnoreCase(pattern[j]) == 0) {
                                    score += 100;
                                }
                            }
                        }
                        track_it.add(new Pair<String, Float>(mkey, score));

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }


                }
                Collections.sort(track_it, new Comparator<Pair<String, Float>>() {
                    @Override
                    public int compare(Pair<String, Float> a, Pair<String, Float> b) {


                        if(Float.compare(a.second,b.second)>0)     {
                            return -1;
                        } else if(Float.compare(a.second,b.second)<0)
                            return 1;
                        else
                            return 0;

                    }
                });

                //System.out.println(track_it);


                //To extract top 20

             for(int i=0;i<20;i++) {
                 key_arr[i] = track_it.get(i).first;

                 //System.out.println(key_arr[i]);
             }

                ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);

                CardFragmentPagerAdapter pagerAdapter = new CardFragmentPagerAdapter(getSupportFragmentManager(), dpToPixels(2, AnimeInfoPage.this));
                ShadowTransformer fragmentCardShadowTransformer = new ShadowTransformer(viewPager, pagerAdapter);
                fragmentCardShadowTransformer.enableScaling(true);

                viewPager.setAdapter(pagerAdapter);
                viewPager.setPageTransformer(false, fragmentCardShadowTransformer);
                viewPager.setOffscreenPageLimit(3);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }


    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }

    public void setImage(String key) {
        String temp = key + ".jpg";
        System.out.println(temp);
        StorageReference filePath = FirebaseStorage.getInstance().getReference().child("AnimeImages").child(temp);
        final ImageView img = (ImageView) findViewById(R.id.post_image);
        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                System.out.println(uri);
                Picasso.get().load(uri).fit().centerCrop().into(img);
            }
        });
    }

}

