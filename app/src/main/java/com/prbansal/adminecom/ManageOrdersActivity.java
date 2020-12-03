package com.prbansal.adminecom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.prbansal.adminecom.adapters.OrderAdapter;
import com.prbansal.adminecom.databinding.ActivityManageOrdersBinding;
import com.prbansal.adminecom.fcm.FCMSender;
import com.prbansal.adminecom.fcm.MessageFormatter;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ManageOrdersActivity extends AppCompatActivity {

    private ActivityManageOrdersBinding manageOrdersBinding;
    public MyApp app;
    ArrayList<QueryDocumentSnapshot> orders = new ArrayList<>();
    OrderAdapter orderAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         manageOrdersBinding =ActivityManageOrdersBinding.inflate(getLayoutInflater());
        setContentView(manageOrdersBinding.getRoot());
        app =(MyApp)getApplicationContext();
        getOrderDetails();

    }

    private void getOrderDetails() {
        app.db.collection("orders").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document: queryDocumentSnapshots){
                            orders.add(document);
                        }
                        app.showToast(ManageOrdersActivity.this,"Success");
                       setOrderAdapter();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        app.showToast(ManageOrdersActivity.this,"Failed!");
                    }
                });
    }

    private void setOrderAdapter() {
        orderAdapter= new OrderAdapter(this,orders);
        manageOrdersBinding.reclyerViiew.setAdapter(orderAdapter);
        manageOrdersBinding.reclyerViiew.setLayoutManager(new LinearLayoutManager(this));
        manageOrdersBinding.reclyerViiew.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

    }
    public void sendNotification(String orderId,String msg,Integer status) {
        updateStatus(orderId,status);
        String message = MessageFormatter
                .getSampleMessage("users", "Order Id="+ orderId,orderId,msg);


        new FCMSender()
                .send(message
                        , new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull final IOException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(ManageOrdersActivity.this)
                                                .setTitle("Failure")
                                                .setMessage(e.toString())
                                                .show();
                                    }
                                });
                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
/*                                        new AlertDialog.Builder(ManageOrdersActivity.this)
                                                .setTitle("Success")
                                                .setMessage(response.toString())
                                                .show();*/
                                    }
                                });


                            }
                        });
    }

    private void updateStatus(String orderId,Integer status) {
        app.db.collection("orders").document(orderId).update("status",status)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        app.showToast(ManageOrdersActivity.this,"Updated");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        app.showToast(ManageOrdersActivity.this,"failed");
                    }
                });
    }
}