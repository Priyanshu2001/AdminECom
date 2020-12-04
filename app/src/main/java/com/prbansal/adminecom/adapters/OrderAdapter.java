package com.prbansal.adminecom.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.prbansal.adminecom.MyApp;
import com.prbansal.adminecom.databinding.OrdersBinding;
import com.prbansal.adminecom.fcm.FCMSender;
import com.prbansal.adminecom.fcm.MessageFormatter;
import com.prbansal.adminecom.models.CartItem;
import com.prbansal.adminecom.models.Orders;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context context;
    Orders orders;
    int count=1;
    public static final int PLACED = 1, DELIVERED = 0, DECLINED = -1;
    MyApp app;
    ArrayList<String> orderIDs;

    public OrderAdapter(Context context, Orders orders, MyApp app, ArrayList<String> ordersIDs) {
        this.context = context;
        this.orders= orders;
        this.app = app;
        this.orderIDs=ordersIDs;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    OrdersBinding ordersBinding=OrdersBinding.inflate(LayoutInflater.from(context),parent,false);
        return new OrdersVH(ordersBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

       String orderId= orderIDs.get(position);

        OrdersBinding ordersBinding = ((OrdersVH) holder).ordersBinding;
        ordersBinding.OrderTxt.setText(orderId);
        ordersBinding.basic.setText("UserName= "+ orders.userName.toUpperCase() + "\nAdsress: "+ orders.userAddress);

        ordersBinding.Items.setText(getOrdersFromMap(orders));

        ordersBinding.declineBtn.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                 ordersBinding.btnGrp.setVisibility(View.GONE);
                  ordersBinding.StatusTxt.setText("Status:\nDeclined".toUpperCase());
                 ordersBinding.StatusTxt.setVisibility(View.VISIBLE);

                  updateStatus(orderId,DECLINED);
                 sendNotification(orderId,"Your Order has been declined");

              }
          });
        ordersBinding.deliverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ordersBinding.btnGrp.setVisibility(View.GONE);
                ordersBinding.StatusTxt.setText("Status:\n Delivered".toUpperCase());
                ordersBinding.StatusTxt.setVisibility(View.VISIBLE);

                updateStatus(orderId,DELIVERED);
                sendNotification(orderId,"Your Order has been delivered");

            }
        });
      /*  ordersBinding.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                ordersBinding.details.setText(orders.toString());
                if (count % 2 == 0) {

                    ordersBinding.moreBtn.setText("View Less");
                    ordersBinding.details.setVisibility(View.VISIBLE);
                }
                else {
                    ordersBinding.moreBtn.setText("See more");
                    ordersBinding.details.setVisibility(View.GONE);
                }
            }
        });*/
    }

/*
    private int getOrdersFromMap(Object cartItems) {
    }
*/


    private void updateStatus(String id, int status) {
        app.db.collection("orders").document(id).update("status",status);
    }

    @Override
    public int getItemCount() {
        return orderIDs.size();
    }
    public class OrdersVH extends RecyclerView.ViewHolder {
        OrdersBinding ordersBinding;

        public OrdersVH(@NonNull OrdersBinding ordersBinding) {
            super(ordersBinding.getRoot());
            this.ordersBinding=ordersBinding;
        }
    }

    public void sendNotification(String orderId,String msg) {
        String message = MessageFormatter
                .getSampleMessage("users", "Order Id="+ orderId,msg);


        new FCMSender()
                .send(message
                        , new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull final IOException e) {

                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {

                            }
                        });
    }
 public StringBuilder getOrdersFromMap(Orders orders){
        StringBuilder builder = new StringBuilder();
      for (Map.Entry<String, CartItem> entry : orders.cartItems.entrySet()){
          builder.append(entry.getKey()).append(entry.getValue().toString());
      }
        return builder;
    }
}
