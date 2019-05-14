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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    MaterialSearchView searchView;
    private RecyclerView myList;
    private DatabaseReference mReference;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mReference= FirebaseDatabase.getInstance().getReference().child("Anime");
        mStorage= FirebaseStorage.getInstance().getReference();
        myList=(RecyclerView) findViewById(R.id.myList);
        myList.setHasFixedSize(true);
        myList.setLayoutManager(new LinearLayoutManager(this));

        searchView = (MaterialSearchView)findViewById(R.id.search_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Material Search");
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
        //viewPager.setAdapter(pagerAdapter);

    }


   /* ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            Query query = FirebaseDatabase.getInstance().getReference().child("Anime").orderByChild("episodes");
            mReference=query.getRef();
            setList(mReference);

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };*/

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
            protected void populateViewHolder(PostHolder viewHolder, Post model, int position) {

                viewHolder.setName(model.getName());
                viewHolder.setRating(model.getRating());

                final String ref=getRef(position).getKey();
                System.out.println(ref);
                // System.out.println(temp);
                viewHolder.setImage(ref);
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                      //  System.out.println("           "+ref);
                        Intent IndInfoPage = new Intent(MainActivity.this,AnimeInfoPage.class);
                        IndInfoPage.putExtra("anime_id",ref);
                        startActivity(IndInfoPage);
                    }
                });
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
