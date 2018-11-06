package com.id.spice.logutil;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.id.spice.logutil.model.CardList;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;


public class MyCardAdapter extends RecyclerView.Adapter<MyCardAdapter.MyViewHolder> {

    private ArrayList<CardList> mCardArrayList;
    private Context mContext;
    private CardInterface mCardInterface;
    private ImageLoader imageLoader;

    public MyCardAdapter(ArrayList<CardList> mListData, Context context, CardInterface cardInterface) {
        this.mCardArrayList = mListData;
        this.mContext = context;
        this.mCardInterface = cardInterface;
        imageLoader = ImageLoader.getInstance();
        initImageLoader();
    }

    private void initImageLoader() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                mContext.getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .build();
        imageLoader.init(config);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_my_cards_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final CardList cardModel = mCardArrayList.get(position);
        holder.tvExpiry.setText(cardModel.getExpiredMonth() + "/" + cardModel.getExpiredYear());
        holder.tvCardHolderName.setText(cardModel.getCardHolderName());
        holder.tvCardNumber.setText(cardModel.getCardMaskNumber());
        holder.imgDelete.setVisibility(View.VISIBLE);
        imageLoader.displayImage(cardModel.getSchemeLogo(), holder.imgCardType);
        if (cardModel.getIsPrimary() != null && cardModel.getIsPrimary().equalsIgnoreCase("1")) {
            holder.checkboxPrimary.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.selected_checkbox));
            holder.tvPrimary.setVisibility(View.VISIBLE);
        } else {
            holder.checkboxPrimary.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.unselected_checkbox));
            holder.tvPrimary.setVisibility(View.GONE);
        }
        holder.checkboxPrimary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCardInterface != null) {
                    mCardInterface.onSetPrimary(cardModel);
                }
            }
        });
        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCardInterface != null) {
                    mCardInterface.onDeleteCard(cardModel);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCardArrayList.size();
    }

    public interface CardInterface {

        void onDeleteCard(CardList cardModel);

        void onSetPrimary(CardList cardModel);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvExpiry, tvPrimary, tvCardNumber, tvCardHolderName, tvSetPrimary;
        private ImageView imgDelete, imgCardType;
        private ImageView checkboxPrimary;

        private MyViewHolder(View view) {
            super(view);
            this.tvExpiry = itemView.findViewById(R.id.tv_expiry);
            this.tvPrimary = itemView.findViewById(R.id.tv_primary);
            this.tvCardNumber = itemView.findViewById(R.id.tv_card_number);
            this.imgDelete = itemView.findViewById(R.id.img_delete);
            this.tvCardHolderName = itemView.findViewById(R.id.tv_card_holder_name);
            this.tvSetPrimary = itemView.findViewById(R.id.tv_set_primary);
            checkboxPrimary = itemView.findViewById(R.id.checkbox_primary);
            imgCardType = itemView.findViewById(R.id.imgCardType);
        }

    }
}
