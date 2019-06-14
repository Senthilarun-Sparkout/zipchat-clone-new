package com.chat.zipchat.clone.Adapter.ViewPagerAdapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.chat.zipchat.clone.Activity.Payments.AddMoneyActivity;
import com.chat.zipchat.clone.Model.Payments.GetUserSavedCards;
import com.chat.zipchat.clone.R;

import java.util.List;

public class AddmoneySavedCardPagerAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    List<GetUserSavedCards> mArrayList;

    public AddmoneySavedCardPagerAdapter(Context context, List<GetUserSavedCards> arrayList) {
        mContext = context;
        mArrayList = arrayList;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mArrayList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        TextView mListSavedCardNumber, mListSavedCardType, mListSavedCardExpDate, mListSavedCardName;
        EditText mListSavedCardCVV;

        View itemView = mLayoutInflater.inflate(R.layout.list_addmoney_saved_cards, container, false);

        mListSavedCardNumber = itemView.findViewById(R.id.mListSavedCardNumber);
        mListSavedCardName = itemView.findViewById(R.id.mListSavedCardName);
        mListSavedCardExpDate = itemView.findViewById(R.id.mListSavedCardExpDate);
        mListSavedCardType = itemView.findViewById(R.id.mListSavedCardType);
        mListSavedCardCVV = itemView.findViewById(R.id.mListSavedCardCVV);
        mListSavedCardCVV.setEnabled(false);

        mListSavedCardNumber.setText(mArrayList.get(position).getNumber());
        mListSavedCardName.setText(mArrayList.get(position).getHolder());
        mListSavedCardExpDate.setText(mArrayList.get(position).getExpiry());

        itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((AddMoneyActivity) mContext).mItemClick(mArrayList.get(position));
            }
        });

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
