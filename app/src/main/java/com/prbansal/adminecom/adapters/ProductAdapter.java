package com.prbansal.adminecom.adapters;


import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;

import android.text.style.ForegroundColorSpan;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.prbansal.adminecom.CatelogActivity;
import com.prbansal.adminecom.R;
import com.prbansal.adminecom.databinding.VariantBasedBinding;
import com.prbansal.adminecom.databinding.WeightBasedBinding;
import com.prbansal.adminecom.models.Products;

import java.util.ArrayList;
import java.util.List;



import static com.prbansal.adminecom.models.Products.VARIANT_BASED;
import static com.prbansal.adminecom.models.Products.WEIGHT_BASED;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    public List<Products> allProductsList,visibleProductsList;
    public boolean ClickCount =true;
    public int LastItemSelectedPosition;
    WeightBasedBinding weightBasedBinding;
    public VariantBasedBinding variantBasedBinding;
    public String Query= "";

    public ProductAdapter(Context context, List<Products> products ){
        this.context = context;
        allProductsList =products;
        this.visibleProductsList= new ArrayList<>(products);
    }

    // Inflating Layouts

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==WEIGHT_BASED){
            WeightBasedBinding weightBasedBinding = WeightBasedBinding.inflate(
                    LayoutInflater.from(context)
                    , parent
                    ,false);
            return new WeightBasedProductVH(weightBasedBinding);
        }
        else {
            VariantBasedBinding variantBasedBinding = VariantBasedBinding.inflate(
                    LayoutInflater.from(context)
                    ,parent
                    ,false);
            return new VariantBasedProductVH(variantBasedBinding);
        }

    }


    @Override
    public int getItemViewType(int position) {
        return visibleProductsList.get(position).type;
    }


    // Setting Contents of views
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final  Products p = visibleProductsList.get(position);

        if(p.type == WEIGHT_BASED){
            weightBasedBinding = ((WeightBasedProductVH) holder).wb;
            weightBasedBinding.WBproductName.setText(p.name);
            weightBasedBinding.WBminQty.setText("Min Qty - " + p.minQty + "\n(in kg)");
            weightBasedBinding.WBpriceKg.setText("Price/kg - " + p.pricePerKg);

            if (p.name.toLowerCase().contains(Query)) {
                int startPos = p.name.toLowerCase().indexOf(Query);
                int endPos = startPos + Query.length();
                Spannable spanString = Spannable.Factory.getInstance().newSpannable(p.name);
                spanString.setSpan(new ForegroundColorSpan(Color.RED), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                weightBasedBinding.WBproductName.setText(spanString);
            }
            setupContextMenu(weightBasedBinding.getRoot());
        }
        else {

            variantBasedBinding = ((VariantBasedProductVH) holder).vb;
            variantBasedBinding.VBproductName.setText(p.name);
            variantBasedBinding.VariantsList.setText(p.listOfVariants());
            variantBasedBinding.variantsNameList.setText(p.listOfNames);
            variantBasedBinding.variantsPriceList.setText("Rs."+p.listOfPrice.replaceAll("\\n","\nRs."));

            if (p.listOfNames.toLowerCase().contains(Query) && !Query.equals("")) {
                variantBasedBinding.variantsNameList.setVisibility(View.VISIBLE);
                variantBasedBinding.VariantsList.setVisibility(View.VISIBLE);
                variantBasedBinding.variantsPriceList.setVisibility(View.VISIBLE);
                ViewCompat.animate(variantBasedBinding.floatingActionButton)
                        .rotation(180.0F)
                        .withLayer()
                        .setDuration(300L)
                        .setInterpolator(new OvershootInterpolator(10.0F))
                        .start();

                int startPosInList = p.listOfNames.toLowerCase().indexOf(Query);
                int endPosInList = startPosInList + Query.length();
                Spannable spanStringInList = Spannable.Factory.getInstance().newSpannable(p.listOfNames);
                spanStringInList.setSpan(new ForegroundColorSpan(Color.RED), startPosInList, endPosInList, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                variantBasedBinding.variantsNameList.setText(spanStringInList);
            }
            if (p.name.toLowerCase().contains(Query)) {
                int startPos = p.name.toLowerCase().indexOf(Query);
                int endPos = startPos + Query.length();
                Spannable spanString = Spannable.Factory.getInstance().newSpannable(p.name);
                spanString.setSpan(new ForegroundColorSpan(Color.RED), startPos, endPos, Spannable.SPAN_COMPOSING);
                variantBasedBinding.VBproductName.setText(spanString);
            }
            rotateFabListViewer(variantBasedBinding);

            setupContextMenu(variantBasedBinding.getRoot());
                    }


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                LastItemSelectedPosition = holder.getAdapterPosition();
                return false;
            }

        });


                
            }

    public void  setupContextMenu(ConstraintLayout root){
        root.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                if(!(context instanceof CatelogActivity))
                    return;

                    CatelogActivity catelogActivity=((CatelogActivity)context);


                    if(!catelogActivity.DragMode){
                        catelogActivity.getMenuInflater().inflate(R.menu.ptoduct_edit_option_contextual_menu, menu);
                    }
            }
        });
    }


    @Override
    public int getItemCount() {
        return visibleProductsList.size();
    }

    public void getSpanText(String query) {
Query=query;
    }


    //View Holders of All type of Products

    public static class WeightBasedProductVH extends RecyclerView.ViewHolder {
        WeightBasedBinding wb;
        public WeightBasedProductVH(WeightBasedBinding weightBasedBinding) {
            super(weightBasedBinding.getRoot());
            this.wb=weightBasedBinding;
        }
    }

    private class VariantBasedProductVH extends RecyclerView.ViewHolder {
        VariantBasedBinding vb;
        public VariantBasedProductVH(VariantBasedBinding variantBasedBinding) {
            super(variantBasedBinding.getRoot());
            this.vb = variantBasedBinding;
        }
    }


    // Actions Related to Queries

    public void filter(String query) {
        query = query.toLowerCase();
        Query = query;
        visibleProductsList = new ArrayList<>();
        for (Products product : allProductsList) {
            if (product.type == VARIANT_BASED &&
                    product.listOfNames.toLowerCase().contains(query)) {
                visibleProductsList.add(product);

            } else {
                if (product.name.toLowerCase().contains(query)) {
                    visibleProductsList.add(product);
                }

            }

            notifyDataSetChanged();

        }
    }

    //methods Related to Buttons and their Animations
        private  void  rotateFabListViewer(VariantBasedBinding variantBasedBinding){
            variantBasedBinding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ClickCount == true) {

                        ViewCompat.animate(variantBasedBinding.floatingActionButton)
                                .rotation(180.0F)
                                .withLayer()
                                .setDuration(300L)
                                .setInterpolator(new OvershootInterpolator(10.0F))
                                .start();
                        variantBasedBinding.VariantsList.setVisibility(View.VISIBLE);
                        variantBasedBinding.variantsNameList.setVisibility(View.VISIBLE);
                        variantBasedBinding.variantsPriceList.setVisibility(View.VISIBLE);
                        ClickCount = false;
                    }
                    else {
                        ViewCompat.animate(variantBasedBinding.floatingActionButton)
                                .rotation(0.0F)
                                .withLayer()
                                .setDuration(300L)
                                .setInterpolator(new OvershootInterpolator(10.0F))
                                .start();
                        variantBasedBinding.VariantsList.setVisibility(View.GONE);
                        variantBasedBinding.variantsNameList.setVisibility(View.GONE);
                        variantBasedBinding.variantsPriceList.setVisibility(View.GONE);
                        ClickCount = true;
                    }
                }
            });

   }

}
