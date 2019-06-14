package com.chat.zipchat.clone.Adapter.ViewPagerAdapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chat.zipchat.clone.Activity.Payments.WithdrawMoneyActivity;
import com.chat.zipchat.clone.Model.Payments.GetWithdrawSavedCards;
import com.chat.zipchat.clone.R;

import java.util.List;

public class WithdrawSavedCardPagerAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    List<GetWithdrawSavedCards> mArrayList;

    public WithdrawSavedCardPagerAdapter(Context context, List<GetWithdrawSavedCards> arrayList) {
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

        TextView mWdSavedName, mWdSavedNumber, mWdSavedCardHolder, mWdSavedRoutingNumber, mWdSavedUseCard;

        View itemView = mLayoutInflater.inflate(R.layout.list_withdraw_saved_cards, container, false);

        mWdSavedName = itemView.findViewById(R.id.mWdSavedName);
        mWdSavedNumber = itemView.findViewById(R.id.mWdSavedNumber);
        mWdSavedCardHolder = itemView.findViewById(R.id.mWdSavedCardHolder);
        mWdSavedRoutingNumber = itemView.findViewById(R.id.mWdSavedRoutingNumber);
        mWdSavedUseCard = itemView.findViewById(R.id.mWdSavedUseCard);

        mWdSavedName.setText(mArrayList.get(position).getFirstname() + " " + mArrayList.get(position).getLastName());
        mWdSavedNumber.setText(mArrayList.get(position).getAccountNumber());
        mWdSavedCardHolder.setText(mArrayList.get(position).getAccountHolder());
        mWdSavedRoutingNumber.setText(mArrayList.get(position).getRoutingNumber());

        itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((WithdrawMoneyActivity) mContext).mWithdrawCardClick(mArrayList.get(position));
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
