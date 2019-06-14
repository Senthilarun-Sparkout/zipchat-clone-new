package com.chat.zipchat.clone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chat.zipchat.clone.Common.App;
import com.chat.zipchat.clone.Model.Contact.ResultItem;
import com.chat.zipchat.clone.Model.Contact.ResultItemDao;
import com.chat.zipchat.clone.Model.Favourites.FavouritePojo;
import com.chat.zipchat.clone.Model.Favourites.FavouritePojoDao;
import com.chat.zipchat.clone.R;

import org.greenrobot.greendao.query.DeleteQuery;

import java.util.List;

import static com.chat.zipchat.clone.Common.BaseClass.myToast;
import static com.chat.zipchat.clone.Common.BaseClass.requestOptionsD;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {


    ImageView mImgContact, mImgBackProfile;
    TextView mUserName, mUserMobile, mUserStatus, mBlockContact, mAddFavourite, mRemoveFavourite;
    String toId;

    String mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mImgContact = findViewById(R.id.mImgContact);
        mUserName = findViewById(R.id.mUserName);
        mUserMobile = findViewById(R.id.mUserMobile);
        mUserStatus = findViewById(R.id.mUserStatus);
        mBlockContact = findViewById(R.id.mBlockContact);
        mAddFavourite = findViewById(R.id.mAddFavourite);
        mRemoveFavourite = findViewById(R.id.mRemoveFavourite);
        mImgBackProfile = findViewById(R.id.mImgBackProfile);

        toId = getIntent().getStringExtra("toId");

        final List<ResultItem> resultItems = App.getmInstance().resultItemDao.queryBuilder().where(
                ResultItemDao.Properties.Id.eq(toId)).list();

        mImage = resultItems.get(0).getProfile_picture();

        mUserName.setText(resultItems.get(0).getName());
        Glide.with(this).setDefaultRequestOptions(requestOptionsD()).load(mImage).into(mImgContact);
        mUserMobile.setText(resultItems.get(0).getMobile_number());
        mUserStatus.setText(resultItems.get(0).getStatus());

        mImgBackProfile.setOnClickListener(this);
        mAddFavourite.setOnClickListener(this);
        mRemoveFavourite.setOnClickListener(this);
        mImgContact.setOnClickListener(this);


        List<FavouritePojo> favouritePojos = App.getmInstance().favouritePojoDao.queryBuilder().where(FavouritePojoDao.Properties.FavouriteId.eq(toId)).list();

        if (favouritePojos.size() > 0) {
            mAddFavourite.setVisibility(View.GONE);
            mRemoveFavourite.setVisibility(View.VISIBLE);
        } else {
            mAddFavourite.setVisibility(View.VISIBLE);
            mRemoveFavourite.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {

        FavouritePojo favouritePojo = new FavouritePojo();
        favouritePojo.setFavouriteId(toId);

        switch (v.getId()) {

            case R.id.mImgBackProfile:
                finish();
                break;

            case R.id.mImgContact:
                if ("null" != mImage) {
                    Intent mImageIntent = new Intent(this, ZoomActivity.class);
                    mImageIntent.putExtra("Value", 2);
                    mImageIntent.putExtra("URL", mImage);
                    startActivity(mImageIntent);
                }
                break;

            case R.id.mAddFavourite:
                App.getmInstance().favouritePojoDao.insertOrReplace(favouritePojo);
                mAddFavourite.setVisibility(View.GONE);
                mRemoveFavourite.setVisibility(View.VISIBLE);

                myToast(this, getResources().getString(R.string.added_to_favourites));
                break;

            case R.id.mRemoveFavourite:

                final DeleteQuery<FavouritePojo> tableDeleteQuery = App.getmInstance().favouritePojoDao.queryBuilder().where(FavouritePojoDao.Properties.FavouriteId.eq(toId)).buildDelete();
                tableDeleteQuery.executeDeleteWithoutDetachingEntities();

                mAddFavourite.setVisibility(View.VISIBLE);
                mRemoveFavourite.setVisibility(View.GONE);

                myToast(this, getResources().getString(R.string.remove_from_favourite));
                break;
        }

    }

}
