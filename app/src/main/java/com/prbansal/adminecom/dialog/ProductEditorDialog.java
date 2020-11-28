package com.prbansal.adminecom.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.prbansal.adminecom.R;
import com.prbansal.adminecom.databinding.ProductEditorDialogBinding;
import com.prbansal.adminecom.models.Products;

import java.util.regex.Pattern;

public class
ProductEditorDialog {
    ProductEditorDialogBinding pedBinding;
    Products products;


    public interface OnProductEditedListener {
        void OnProductEdited(Products product);
        void OnCancelled();
    }

    public void  show(final Context context, final Products products, final OnProductEditedListener listener){
        pedBinding = ProductEditorDialogBinding.inflate(LayoutInflater.from(context));
        this.products =products;

        new AlertDialog.Builder(context)
                .setTitle("Edit Product")
                .setView(pedBinding.getRoot())
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(areProductDetailsValid()) {
                            String name = pedBinding.ProductName.getText().toString().trim();
                            products.name = name;
                            products.listOfNames= pedBinding.VariantNAME.getText().toString().trim();
                            products.listOfPrice = pedBinding.VariantPRICE.getText().toString().trim();
                            listener.OnProductEdited(ProductEditorDialog.this.products);
                        }
                        else
                            Toast.makeText(context, "Invalid details!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.OnCancelled();
                    }
                })
                .show();

        setupRadioGroup();
        prefillDetails();


}


   // Methods to Check Valid Details of Products
    private boolean areProductDetailsValid() {
        //Check name
        String name = pedBinding.ProductName.getText().toString().trim();
        if(name.isEmpty())
            return false;

        switch (pedBinding.radioGroup.getCheckedRadioButtonId()){
            case R.id.WeightBasedRadio :
                String pricePerKg = pedBinding.PricePerKG.getText().toString().trim()
                        , minQty = pedBinding.MinQTY.getText().toString().trim();

                //Check inputs
                if(pricePerKg.isEmpty() || minQty.isEmpty() || !minQty.matches("\\d+(kg|g)"))
                    return false;

                products.WeightBasedProduct(name
                        , Integer.parseInt(pricePerKg)
                        , extractMinQtyFromString(minQty));
                return true;

            case R.id.VariantBasedRadio :
                String variantsName = pedBinding.VariantNAME.getText().toString().trim();
                 String variantsPrice = pedBinding.VariantPRICE.getText().toString().trim();

                products.VariantsBasedProduct(name,variantsName,variantsPrice);

                return areVariantsValid(variantsName,variantsPrice);
        }
        return false;
    }


    //Check Whether the Variants are valid or not
    private boolean areVariantsValid(String variantsName, String variantsPrice) {
        if (variantsName.length() == 0 && variantsPrice.length()==0) {
            return false;
        }

        //Get strings of each variant
        String[] vn = variantsName.split("\n");
        String[] vp = variantsPrice.split("\n");

        if(vn.length != vp.length){
            return false;
        }

        //Check for each variant format using RegEx
        Pattern patternName = Pattern.compile("^\\w+(\\s|\\w)+$");
        Pattern patternPrice= Pattern.compile("^\\d+|(\\d+\\s)+|(\\d+\\s+)+$");
        for (String vName : vn) {
            if (!patternName.matcher(vName).matches()){
                return false;
            }
        }
        for (String vPrice : vp) {
            if (!patternPrice.matcher(vPrice).matches()) {
                return false;
            }
        }
        //Extracts Variants from String[]
        products.ExtractVariantsAndSet(vn,vp);

        return true;
    }
    private float extractMinQtyFromString(String minQty) {
        if(minQty.contains("kg"))
            return Integer.parseInt(minQty.replace("kg", ""));
        else
            return Integer.parseInt(minQty.replace("g", "")) / 1000f;
    }


    // Methods to setup and backup Details

    private void setupRadioGroup() {
        pedBinding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.WeightBasedRadio){
                    pedBinding.WeightBasedRoot.setVisibility(View.VISIBLE);
                    pedBinding.instructions.setVisibility(View.GONE);
                    pedBinding.VariantBasedRoot.setVisibility(View.GONE);
                } else {
                    pedBinding.instructions.setVisibility(View.VISIBLE);
                    pedBinding.VariantBasedRoot.setVisibility(View.VISIBLE);
                    pedBinding.WeightBasedRoot.setVisibility(View.GONE);
                }
            }
        });
    }
    public void prefillDetails() {

        pedBinding.ProductName.setText(products.name);
        pedBinding.radioGroup.check(products.type  == Products.WEIGHT_BASED ?
                R.id.WeightBasedRadio : R.id.VariantBasedRadio);

        if(products.type == Products.WEIGHT_BASED){
            pedBinding.PricePerKG.setText(products.pricePerKg + "");
            pedBinding.MinQTY.setText(products.minQtyToString());
        }
        else{
            pedBinding.VariantNAME.setText(products.listOfNames);
            pedBinding.VariantPRICE.setText(products.listOfPrice);
        }

    }

}
