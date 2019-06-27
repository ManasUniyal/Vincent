package solesurvivors.vincent;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    MaterialSearchView searchView;
    private RecyclerView myList;
    private DatabaseReference mReference;
    private StorageReference mStorage;
    private Button MovieButton;
    private Button TvButton;
    private Button OVAButton;
    private Button SpecialButton;
    private FirebaseAuth mAuth;
    private DatabaseReference userReference;
    FirebaseAuth RatingUser;
    private Button ResultButton;

    public static double userRating[]=new double[3000];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        String user_id= Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        mReference= FirebaseDatabase.getInstance().getReference().child("Anime");
        mStorage= FirebaseStorage.getInstance().getReference();
        myList=(RecyclerView) findViewById(R.id.myList);
        myList.setHasFixedSize(true);
        myList.setLayoutManager(new LinearLayoutManager(this));

        userReference=FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int ind=0;
                double t;
                String key;
                for(DataSnapshot userkey: dataSnapshot.getChildren()){

                    key=userkey.getKey();
                    if(key.equals("Image") || key.equals("Name"))
                        continue;
                    t=userkey.getValue(double.class);
                    ind=Integer.valueOf(key);
                    userRating[ind]=t;
                    if(t>0)
                        Log.d("Yes",key+ "  "+ t);



                }


                /*for(int i=0;i<=2991;i++) {
                    mReference.child(Integer.toString(i)).child("KeyRating").setValue(userRating[i]*-1);

                }*/
               /*DatabaseReference mref=FirebaseDatabase.getInstance().getReference().child("Anime");
               setList(mref.orderByChild("KeyRating"));*/



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        ResultButton=(Button)findViewById(R.id.resultButton);

        MovieButton=(Button)findViewById(R.id.MovieButton);
        TvButton=(Button)findViewById(R.id.TvButton);
        OVAButton=(Button)findViewById(R.id.OVAButton);
        SpecialButton=(Button)findViewById(R.id.SpecialButton);

        ResultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    CSVToMatrix obj = new CSVToMatrix(MainActivity.this);
                    double[] output = obj.getFinalMatrix();
                    for(int i=0;i<2990;i++) {
                        mReference.child(Integer.toString(i)).child("KeyRating").setValue(output[i]*-1);
                        Log.d("CHECK",i+"  "+output[i]);

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }



                DatabaseReference mref=FirebaseDatabase.getInstance().getReference().child("Anime");
                setList(mref.orderByChild("KeyRating"));
            }
        });

        MovieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference mref=FirebaseDatabase.getInstance().getReference().child("Anime");
                setList(mref.orderByChild("type").equalTo("Movie"));
            }
        });
        TvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference mref=FirebaseDatabase.getInstance().getReference().child("Anime");
                setList(mref.orderByChild("type").equalTo("TV"));
            }
        });
        OVAButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference mref=FirebaseDatabase.getInstance().getReference().child("Anime");
                setList(mref.orderByChild("type").equalTo("OVA"));
            }
        });
        SpecialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference mref=FirebaseDatabase.getInstance().getReference().child("Anime");
                setList(mref.orderByChild("type").equalTo("Special"));
            }
        });


        searchView = (MaterialSearchView)findViewById(R.id.search_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Vincent");
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        //    ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                System.out.println("Hello");
            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                // DatabaseReference mref=FirebaseDatabase.getInstance().getReference().child("Anime");
                // setList(mref.orderByChild("name").startAt(query).endAt(query+"\uf8ff"));
                //searchView.closeSearch();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                DatabaseReference mref=FirebaseDatabase.getInstance().getReference().child("Anime");
                setList(mref.orderByChild("name").startAt(newText).endAt(newText+"\uf8ff"));
                //   searchView.closeSearch();
                return true;
            }
        });


    }



    @Override
    protected void onStart() {
        super.onStart();



        setList(mReference);

    }

    public void setList(Query ref)
    {
        FirebaseRecyclerAdapter<Post,PostHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, PostHolder>(
                Post.class,
                R.layout.my_row,
                PostHolder.class,
                ref
        ) {
            @Override
            protected void populateViewHolder(PostHolder viewHolder, Post model, final int p) {

                viewHolder.setName(model.getName());
                viewHolder.setRating(model.getRating());

                final int position=model.getIndex();

                //    final String ref=getRef(position).getKey();
                viewHolder.setImage(Integer.toString(position));

                RatingBar rateStar=(RatingBar)viewHolder.mView.findViewById(R.id.rateStar);




                String user_id= Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

                rateStar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                        userReference.child(Integer.toString(position)).setValue(v);
                        userRating[position]=(double)v;
                        Log.d("listener  ","See   :"+position+"  "+ratingBar.getRating());
                    }
                });

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        //  System.out.println("           "+ref);
                        Intent IndInfoPage = new Intent(MainActivity.this,AnimeInfoPage.class);
                        IndInfoPage.putExtra("anime_id",Integer.toString(position));
                        startActivity(IndInfoPage);
                    }
                });

                viewHolder.setStars(position);

            }
        };

        myList.setAdapter(firebaseRecyclerAdapter);



    }

    public static class PostHolder extends RecyclerView.ViewHolder{
        View mView;
        public PostHolder(@NonNull View itemView) {
            super(itemView);

            mView=itemView;
        }

        public void setName(String title)
        {
            TextView post_name=(TextView)mView.findViewById(R.id.post_name);
            post_name.setText(title);
        }
        public void setRating(String rating){
            TextView post_rating=(TextView)mView.findViewById(R.id.post_rating);
            post_rating.setText(rating);
        }

        public void setImage(String key) {
            String temp=key+".jpg";
            System.out.println(temp);
            StorageReference filePath= FirebaseStorage.getInstance().getReference().child("AnimeImages").child(temp);
            final ImageView img=(ImageView)mView.findViewById(R.id.post_image);
            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    System.out.println(uri);
                    Picasso.get().load(uri).fit().centerCrop().into(img);
                }
            });

        }

        public void setStars(int position)
        {
            RatingBar rateStar=(RatingBar)mView.findViewById(R.id.rateStar);
            rateStar.setRating((float)userRating[position]);
            Log.d("Stars",position+"   "+userRating[position]);


        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

}
