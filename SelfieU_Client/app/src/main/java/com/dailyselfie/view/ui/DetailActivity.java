package com.dailyselfie.view.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.dailyselfie.R;
import com.squareup.picasso.Picasso;

import java.io.File;


public class DetailActivity extends AppCompatActivity {

    private ImageView mImageView;
    private Toolbar toolbar;
    private String mImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        toolbar = (Toolbar) findViewById(R.id.detailbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Selfie View");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        mImagePath = intent.getStringExtra(Intent.EXTRA_TEXT);

        mImageView = (ImageView) findViewById(R.id.imageView);

        try {
            if (new File(mImagePath).exists()) {
                Picasso
                        .with(getApplicationContext())
                        .load(new File(mImagePath))
                        .fit() // will explain later
                        .into(mImageView);
            }else{
                Picasso
                        .with(getApplicationContext())
                        .load(R.drawable.ic_placeholder)
                        .fit() // will explain later
                        .into(mImageView);
            }

        }catch (Exception e){
            Picasso
                    .with(getApplicationContext())
                    .load(R.drawable.ic_placeholder)
                    .fit() // will explain later
                    .into(mImageView);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.home:
                Intent intent = NavUtils.getParentActivityIntent(this);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                NavUtils.navigateUpTo(this, intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
