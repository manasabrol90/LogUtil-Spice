package com.id.spice.logutil;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.id.spice.logutil.model.CardList;
import com.id.spice.logutil.model.CardListResponseModel;
import com.id.spice.logutil.network.ApiEnumeration;
import com.id.spice.logutil.network.NetworkRequestVolley;
import com.id.spice.logutil.network.VolleyResponse;

import org.json.JSONException;

import java.util.ArrayList;


public class MyCardActivity extends AppCompatActivity implements View.OnClickListener, MyCardAdapter.CardInterface {

    private android.support.v7.widget.RecyclerView mRecyclerViewCard;
    private ArrayList<CardList> mCardListArrayList = null;
    private String mUserId = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_cards);
        fetchIntent();
        initializeView();
    }

    private void fetchIntent() {
        mUserId = "est131210";
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCardListArrayList == null || mCardListArrayList.size() == 0) {
            requestServerForAddedCardsList();
        } else {
            setAdapter(mCardListArrayList);
        }
    }

    private void setAdapter(ArrayList<CardList> cardList) {
        MyCardAdapter mAdapter = new MyCardAdapter(cardList, this, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerViewCard.setLayoutManager(mLayoutManager);
        mRecyclerViewCard.setAdapter(mAdapter);
    }

    private void initializeView() {
        this.mRecyclerViewCard = findViewById(R.id.rv_card);
        ((TextView) findViewById(R.id.tv_tiitle)).setText("My Cards");
        findViewById(R.id.cv_add_card).setOnClickListener(this);
        findViewById(R.id.btn_back).setOnClickListener(this);
    }


    private void requestServerForAddedCardsList() {
        try {
            ApiEnumeration.RU_LIST_CARD.setPostFixURL(this.mUserId + ApiConstants.KEY_LIST_CARD);
            new NetworkRequestVolley(new VolleyResponse<CardListResponseModel>() {
                @Override
                public void onResult(CardListResponseModel resultModel, ApiEnumeration apiEnumeration) {
                    if (resultModel != null && resultModel.getCardList() != null && resultModel.getCardList().size() > 0) {
                        mRecyclerViewCard.setVisibility(View.VISIBLE);
                        findViewById(R.id.tvError).setVisibility(View.GONE);
                        setAdapter(resultModel.getCardList());
                    } else {
                        mRecyclerViewCard.setVisibility(View.GONE);
                        findViewById(R.id.tvError).setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onHttpResultHandler(String errorMessage, ApiEnumeration apiEnumeration, int httpCode) {
                }

                @Override
                public void onFailure(String errorMessage, ApiEnumeration apiEnumeration) {
                    Log.e("error", errorMessage);
                    mRecyclerViewCard.setVisibility(View.GONE);
                    findViewById(R.id.tvError).setVisibility(View.VISIBLE);
                }
            }, this).sendRequest(ApiEnumeration.RU_LIST_CARD, null, true, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.cv_add_card) {
            finish();
        } else if (i == R.id.btn_back) {
            finish();
        }
    }

    @Override
    public void onDeleteCard(CardList cardModel) {

    }

    @Override
    public void onSetPrimary(CardList cardModel) {

    }
}
