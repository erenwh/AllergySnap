package com.example.brhee.allergysnap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class ResultActivity extends AppCompatActivity {

    public String txtJson = "";
    public String ingredients = "";

    public class Sample {
        public class Store {
            public String store_name;
            public String store_price;
            public String product_url;
            public String currency_code;
            public String currency_symbol;
        }

        public class Review {
            public String name;
            public String rating;
            public String title;
            public String review;
            public String datetime;
        }

        public class Product {
            public String barcode_number;
            public String barcode_type;
            public String barcode_formats;
            public String mpn;
            public String model;
            public String asin;
            public String product_name;
            public String title;
            public String category;
            public String manufacturer;
            public String brand;
            public String label;
            public String author;
            public String publisher;
            public String artist;
            public String actor;
            public String director;
            public String studio;
            public String genre;
            public String audience_rating;
            public String ingredients;
            public String nutrition_facts;
            public String color;
            public String format;
            public String package_quantity;
            public String size;
            public String length;
            public String width;
            public String height;
            public String weight;
            public String release_date;
            public String description;
            public Object[] features;
            public String[] images;
            public Store[] stores;
            public Review[] reviews;
        }

        public class RootObject {
            public Product[] products;
        }
    }

    public TextView barcodeResult;
    public TextView barcodeIngredients;

    public AsyncTask data2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        barcodeResult = (TextView)findViewById(R.id.barcode_result);

        Bundle bundle = getIntent().getExtras();
        barcodeResult.setText(bundle.getString("barcode_value"));

        barcodeIngredients = (TextView)findViewById(R.id.barcode_ingredients);

    }

    public void scanBarcode(View v) {
        Intent intent = new Intent(this, Camera2.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra("barcode");

                    new JsonTask().execute("https://api.barcodelookup.com/v2/products?barcode=" + barcode.displayValue + "&formatted=y&key=6jefu0ayi6urzdqq1k4j8bljb6rn7u");

                    barcodeResult.setText(barcode.displayValue);
                    barcodeIngredients.setText(ingredients);

                }
                else {
                    barcodeResult.setText("No barcode found!");
                }
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private class JsonTask extends AsyncTask<String, String, String> {

        public ProgressDialog pd = new ProgressDialog(ResultActivity.this);;

        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                String str = "";
                String data2 = "";

                while (null != (str= br.readLine())) {
                    data2 +=str;
                }

                Gson g = new Gson();

                Sample.RootObject value = g.fromJson(data2, Sample.RootObject.class);

                String barcode2 = value.products[0].barcode_number;

                String name = value.products[0].product_name;

                ingredients = value.products[0].ingredients;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()) {
                pd.dismiss();
            }
            barcodeIngredients.setText(ingredients.toString());
            txtJson = result;
        }
    }
}
