package com.prbansal.adminecom.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.model.Document;
import com.prbansal.adminecom.ManageOrdersActivity;
import com.prbansal.adminecom.databinding.OrdersBinding;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context context;
    ArrayList<QueryDocumentSnapshot> orderList;
    int count=1;
    public static final int PLACED = 1, DELIVERED = 0, DECLINED = -1;

    public OrderAdapter(Context context, ArrayList<QueryDocumentSnapshot> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    OrdersBinding ordersBinding=OrdersBinding.inflate(LayoutInflater.from(context),parent,false);
        return new OrdersVH(ordersBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
          QueryDocumentSnapshot qds = orderList.get(position);
       OrdersBinding ordersBinding = ((OrdersVH) holder).ordersBinding;
        ordersBinding.OrderTxt.setText(qds.getId());
        ordersBinding.basic.setText("UserName= "+qds.get("userName")+"Subtotal= "+qds.get("subTotal").toString());
          ordersBinding.declineBtn.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                 ordersBinding.btnGrp.setVisibility(View.GONE);
                  ordersBinding.StatusTxt.setText("Status: Declined");
                 ordersBinding.StatusTxt.setVisibility(View.VISIBLE);
                 new ManageOrdersActivity().sendNotification(qds.getId(),"Your Order has been declined",DECLINED);
              }
          });
        ordersBinding.deliverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ordersBinding.btnGrp.setVisibility(View.GONE);
                ordersBinding.StatusTxt.setText("Status: Delivered");
                ordersBinding.StatusTxt.setVisibility(View.VISIBLE);
                new ManageOrdersActivity().sendNotification(qds.getId(),"Your Order has been delivered",DELIVERED);
            }
        });
        ordersBinding.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                ordersBinding.details.setText(qds.getData().toString());
                if (count % 2 == 0) {

                    ordersBinding.moreBtn.setText("View Less");
                    ordersBinding.details.setVisibility(View.VISIBLE);
                }
                else {
                    ordersBinding.moreBtn.setText("See more");
                    ordersBinding.details.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
    public class OrdersVH extends RecyclerView.ViewHolder {
        OrdersBinding ordersBinding;

        public OrdersVH(@NonNull OrdersBinding ordersBinding) {
            super(ordersBinding.getRoot());
            this.ordersBinding=ordersBinding;
        }
    }


}
