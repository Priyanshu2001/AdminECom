package com.prbansal.adminecom;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;

import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prbansal.adminecom.adapters.ProductAdapter;
import com.prbansal.adminecom.databinding.ActivityCatelogBinding;

import com.prbansal.adminecom.dialog.ProductEditorDialog;
import com.prbansal.adminecom.models.Inventory;
import com.prbansal.adminecom.models.Products;

import java.util.ArrayList;

import java.util.Collections;
import java.util.Comparator;

import java.util.List;

public class CatelogActivity extends AppCompatActivity {
    ActivityCatelogBinding activityCatelogBinding;
    ArrayList<Products> products;
    ProductAdapter productAdapter;
    private SearchView searchView;
    public boolean DragMode;
    ItemTouchHelper itemTouchHelper;
   public MyApp app;
    ArrayList<Products> restoreRemovedProducts= new ArrayList<>();


    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCatelogBinding =ActivityCatelogBinding.inflate(getLayoutInflater());
        setContentView(activityCatelogBinding.getRoot());

        app =  (MyApp) getApplicationContext();
        loadPreviousData();

        FirebaseMessaging.getInstance().subscribeToTopic("admin");
        activityCatelogBinding.manageOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              startActivity(new Intent(CatelogActivity.this,ManageOrdersActivity.class));
            }
        });

    }

    private void saveOnFirebase() {


        if(app.isOffline()){
            app.showToast(this, "You are offline! Unable to save. Check your connection and try again.");
            return;
        }
        app.showLoadingDialog(this);

        Inventory myInventory = new Inventory(products);
        app.db.collection("My Inventory").document("Products List")
                .set(myInventory)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        app.showToast(CatelogActivity.this,"Saved!");
                        saveDataLocally();
                        app.hideLoadingDialog();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        app.showToast(CatelogActivity.this,"Failed to Save!");
                    }
                });


    }

    private void saveDataLocally() {
        SharedPreferences preferences = getSharedPreferences("products_data", MODE_PRIVATE);
        preferences.edit()
                .putString("data", new Gson().toJson(products))
                        .apply();


    }

    private void loadPreviousData() {

        SharedPreferences preferences = getSharedPreferences("products_data",MODE_PRIVATE);
        String jsonData = preferences.getString("data" ,null);
        if(jsonData!=null){
            products = new Gson().fromJson(jsonData, new TypeToken<ArrayList<Products>>(){}.getType());
            setupProductList();
        }
        else {
            loadFromCloud();
        }
    }

    private void loadFromCloud() {
        if(app.isOffline()){
            app.showToast(this, "You are offline! Unable to save. Check your connection and try again.");
            return;
        }
        app.showLoadingDialog(this);

        app.db.collection("My Inventory").document("Products List")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()&& documentSnapshot!=null){
                            Inventory inventory =documentSnapshot.toObject(Inventory.class);
                            products=  inventory.myProductsList;
                        }
                        else {
                            products = new ArrayList<>();
                            app.hideLoadingDialog();
                        }
                        setupProductList();
                        saveDataLocally();
                        app.hideLoadingDialog();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        app.showToast(CatelogActivity.this,"Failed to Fetch Data!");
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveDataLocally();
    }

    private void setupProductList() {

        productAdapter = new ProductAdapter(this,products);

        activityCatelogBinding.recyclerView.setAdapter(productAdapter);
        activityCatelogBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        activityCatelogBinding.recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        setupDragAndDrop();
    }

    private void setupDragAndDrop() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper
                .SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN
                | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                Collections.swap(productAdapter.visibleProductsList, fromPosition, toPosition);
                activityCatelogBinding.recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        };

        itemTouchHelper = new ItemTouchHelper(simpleCallback);
    }

    // Option Menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_product_options,menu);

        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String query) {
               productAdapter.getSpanText(query);
                productAdapter.filter(query);

                return true;

            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.addItem:
                showProductEditorDialog();
                return true;

            case R.id.sort :
                sortTheList();
                return true;


            case R.id.reorder:
                toggleReordering(item);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //Drag and drop to Re-order Products
    private void toggleReordering(@NonNull MenuItem item) {
        changeIconColour(item);

        if(DragMode)
            itemTouchHelper.attachToRecyclerView(null);
        else
            itemTouchHelper.attachToRecyclerView(activityCatelogBinding.recyclerView);

        DragMode = !DragMode;
    }

    private void changeIconColour(@NonNull MenuItem item) {
        Drawable icon = item.getIcon();
        if(DragMode){
            icon.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        } else {
            icon.setColorFilter(getResources().getColor(R.color.teal_200), PorterDuff.Mode.SRC_ATOP);
        }
        item.setIcon(icon);
    }

    //Context Menu
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.edit_option:

                editLastSelectedItem();

                return true;

            case R.id.remove_option :
                removeLastSelectedItem();

                return true;


        }

        return super.onContextItemSelected(item);
    }

    private void removeLastSelectedItem() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm to delete")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Products removeThisProduct = productAdapter.visibleProductsList.
                                get(productAdapter.LastItemSelectedPosition);
                        restoreRemovedProducts.add(removeThisProduct);
                        productAdapter.visibleProductsList.remove(removeThisProduct);
                        productAdapter.allProductsList.remove(removeThisProduct);
                        productAdapter.notifyItemRemoved(productAdapter.LastItemSelectedPosition);
                        Snackbar.make(activityCatelogBinding.getRoot(), "Product Deleted", Snackbar.LENGTH_LONG)
                                .setAction("UNDO", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        productAdapter.allProductsList.add(removeThisProduct);
                                        productAdapter.visibleProductsList.add(removeThisProduct);
                                        productAdapter.notifyItemInserted(productAdapter.LastItemSelectedPosition+1);
                                    }
                                })
                                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                                .show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();

    }

    private void editLastSelectedItem() {
        Products lastSelectedItem = productAdapter.visibleProductsList.get(productAdapter.LastItemSelectedPosition);
        new ProductEditorDialog().show(this, lastSelectedItem , new ProductEditorDialog.OnProductEditedListener() {
            @Override
            public void OnProductEdited(Products product) {
                if(!isQueryContains(product.name)){
                   productAdapter.visibleProductsList.remove(productAdapter.LastItemSelectedPosition);
                   productAdapter.notifyItemRemoved(productAdapter.LastItemSelectedPosition);
                }
                productAdapter.notifyItemChanged(productAdapter.LastItemSelectedPosition);
            }

            @Override
            public void OnCancelled() {
                Toast.makeText(CatelogActivity.this, "Cancelled!", Toast.LENGTH_SHORT).show();
            }
        });

        }


        // Actions related to Dialog
    private void showProductEditorDialog() {
        new ProductEditorDialog().show(this, new Products(), new ProductEditorDialog.OnProductEditedListener() {
            @Override
            public void OnProductEdited(Products product) {
                productAdapter.allProductsList.add(product);
                productAdapter.notifyItemInserted(products.size() - 1);

                if(isQueryContains(product.name)){
                   productAdapter.visibleProductsList.add(product);
                   productAdapter.notifyItemInserted(productAdapter.visibleProductsList.size() - 1);
                }
            }

            @Override
            public void OnCancelled() {
            }
        });
    }


    //functions related to actions of Option menu
    public boolean isQueryContains(String name) {
        String query = searchView.getQuery().toString().toLowerCase();
        return name.toLowerCase().contains(query);
    }


    private void sortTheList() {
        Collections.sort(productAdapter.visibleProductsList, new Comparator<Products>(){
            @Override
            public int compare(Products a, Products b) {
                return a.name.compareToIgnoreCase(b.name);
            }
        });
        productAdapter.notifyDataSetChanged();
        Toast.makeText(this, "List sorted!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Unsaved Changes Found!")
                .setMessage("Do you wan to save?")
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveOnFirebase();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (Products p : restoreRemovedProducts)
                            productAdapter.allProductsList.add(p);
                        app.showLoadingDialog(CatelogActivity.this);
                        finish();
                    }
                }).show();
    }
}

