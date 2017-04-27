/*******************************************************************************
 * Copyright IBM Corp. 2017
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.ibm.wch_sample_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity {

    //declare UI elements
    Button buttonGetTaxonomies;
    Button buttonGetCategories;
    Button buttonGetContentItems;
    EditText etCategory;
    EditText etTaxonomy;
    TextView tvBaseUrl;
    TextView tvCategories;
    TextView tvTenantId;
    TextView tvResponse;
    TextView tvSearchResults;

    private String baseTenantAPIURL, tenantId, taxonomiesUrl, searchUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //initialize UI elements
        buttonGetTaxonomies = (Button) findViewById(R.id.buttonGetTaxonomies);
        buttonGetCategories = (Button) findViewById(R.id.buttonGetCategories);
        buttonGetContentItems = (Button) findViewById(R.id.buttonGetContentItems);
        etCategory = (EditText) findViewById(R.id.etCategory);
        etTaxonomy = (EditText) findViewById(R.id.etTaxonomy);
        tvBaseUrl = (TextView) findViewById(R.id.tvBaseUrl);
        tvCategories = (TextView) findViewById(R.id.tvCategories);
        tvTenantId = (TextView) findViewById(R.id.tvTenantId);
        tvResponse = (TextView) findViewById(R.id.tvResponse);
        tvSearchResults = (TextView) findViewById(R.id.tvSearchResults);

        //get passed variables from LoginActivity
        Intent intent = getIntent();
        baseTenantAPIURL = intent.getStringExtra("baseURL");
        tenantId = intent.getStringExtra("tenantID");

        //URL variables
        taxonomiesUrl = baseTenantAPIURL + "/authoring/v1/categories";
        searchUrl = baseTenantAPIURL + "/authoring/v1/search?";

        //show what the baseUrl and tenantId are.
        tvBaseUrl.setText(tvBaseUrl.getText().toString() + intent.getStringExtra("baseURL"));
        tvTenantId.setText(tvTenantId.getText().toString() + intent.getStringExtra("tenantID"));

        //Instantiate the RequestQueue.
        final RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);

        //Retrieves all existing Taxonomy names
        buttonGetTaxonomies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        tvResponse.setText(response);
                        try {
                            JSONObject responseJSON = new JSONObject(response);//create JSONObject from respsone
                            JSONArray items = new JSONArray(responseJSON.get("items").toString());//get array of taxonomies
                            String taxonomyNames = "";//declare
                            for (int currentItem = 0; currentItem < items.length(); currentItem++) {//iterate through items
                                JSONObject taxonomy = items.getJSONObject(currentItem);//create the object
                                taxonomyNames += "\n" + taxonomy.get("name").toString();//add to string
                            }

                            tvResponse.setText("Taxonomies:" + taxonomyNames);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            tvResponse.setText(e.toString());
                        }
                    }
                };

                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        tvResponse.setText(error.toString());
                    }
                };

                //Create new request and add it to the Queue
                WCHRequest requestCategories = new WCHRequest(taxonomiesUrl, responseListener, errorListener);
                queue.add(requestCategories);
            }
        });

        //Retrieve all content items of a certain category by category name
        buttonGetContentItems.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                final String taxonomyName = etTaxonomy.getText().toString().trim();
                final String categoryName = etCategory.getText().toString().trim();

                if (taxonomyName.length() == 0) {
                    tvSearchResults.setText("Please supply a taxonomy name to get contnet items from");
                }
                else {
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject responseJSON = new JSONObject(response);//create JSONObject from respsone
                                if (responseJSON.has("documents")){
                                    JSONArray documents = new JSONArray(responseJSON.get("documents").toString());
                                    String documentNames = "";
                                    documentNames += responseJSON.get("numFound").toString() + "\n\n";
                                    for (int currentItem = 0; currentItem < documents.length(); currentItem++) {//iterate through items
                                        JSONObject currentDoc = documents.getJSONObject(currentItem);//create the object
                                        JSONObject docDetails = new JSONObject(currentDoc.get("document").toString());
                                        JSONObject docElements = new JSONObject((docDetails.get("elements").toString()));
                                        JSONObject docCategory = docElements.getJSONObject("category");
                                        JSONArray categories = new JSONArray(docCategory.get("categories").toString());
                                        String categoryStrings = "";
                                        for (int currentCategory = 0; currentCategory < categories.length(); currentCategory++){
                                            if (currentCategory == 0) {
                                                categoryStrings += categories.get(currentCategory).toString();
                                            } else {
                                                categoryStrings += ", " + categories.get(currentCategory).toString();
                                            }
                                        }
                                        documentNames += "Name: " + docDetails.get("name").toString() + "\nType: " + docDetails.get("type") + "\nCategories: " + categoryStrings + "\nLast Modified: " + docDetails.get("lastModified") + "\n\n";//add to string
                                        //tvResponse.setText(details.toString());
                                    }
                                    tvSearchResults.setText("Content items: " + documentNames);
                                }
                                else if (responseJSON.has("numFound")) {
                                    tvSearchResults.setText("Content items: " + responseJSON.get("numFound"));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                tvSearchResults.setText(e.toString());
                            }
                        }
                    };

                    Response.ErrorListener errorListener = new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            tvSearchResults.setText(error.toString());
                        }
                    };
                    String url;
                    if (categoryName.length() != 0) {
                        url = baseTenantAPIURL + "/authoring/v1/search?q=*:*&wt=json&fq=type%3A%22Article%22&fq=classification:(content)&fl=id,document&fq=categories:(" + taxonomyName + "/" + categoryName + ")&sort=lastModified%20desc";
                    }
                    else {
                        url = baseTenantAPIURL + "/authoring/v1/search?q=*:*&wt=json&fq=type%3A%22Article%22&fq=classification:(content)&fl=id,document&fq=categories:(" + taxonomyName + ")&sort=lastModified%20desc";
                    }

                    //Create new request and add it to the Queue
                    WCHRequest requestCategories = new WCHRequest(url, responseListener, errorListener);
                    queue.add(requestCategories);
                }
            }
        });

        //returns all the categorie names of a certain taxonomy by the taxonomy name
        buttonGetCategories.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String taxonomyName = etTaxonomy.getText().toString().trim();

                if (taxonomyName.length() == 0) {
                    tvCategories.setText("Please supply a taxonomy name to get categories from");
                }
                else {
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject responseJSON = new JSONObject(response);//create JSONObject from respsone
                                JSONArray items = new JSONArray(responseJSON.get("items").toString());//get array of taxonomies
                                String taxonomyID = "";//declare
                                for (int currentItem = 0; currentItem < items.length(); currentItem++) {//iterate through items
                                    JSONObject taxonomy = items.getJSONObject(currentItem);//create the object
                                    if (taxonomy.get("name").toString().equals(taxonomyName)){
                                        taxonomyID = taxonomy.get("id").toString();
                                    }
                                }

                                if (!taxonomyID.equals("")){
                                    final String categoriesURL = baseTenantAPIURL + "/authoring/v1/categories/" + taxonomyID + "/children";
                                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            tvCategories.setText(response);
                                            try {
                                                String categoryNames = "";
                                                JSONObject responseJSON = new JSONObject(response);//create JSONObject from respsone
                                                JSONArray items = new JSONArray(responseJSON.get("items").toString());//get array of taxonomies

                                                //TODO: for loop through array and get names of all categories and add them to the results
                                                for (int currentItem = 0; currentItem < items.length(); currentItem++) {
                                                    JSONObject currentJSONItem = items.getJSONObject(currentItem);
                                                    categoryNames += "\n" + currentJSONItem.get("name");
                                                }
                                                tvCategories.setText("Categories:" + categoryNames);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                tvCategories.setText(e.toString());
                                            }
                                        }
                                    };

                                    Response.ErrorListener errorListener = new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            tvCategories.setText(error.toString());
                                        }
                                    };

                                    WCHRequest requestCategoriesById = new WCHRequest(categoriesURL, responseListener, errorListener);
                                    queue.add(requestCategoriesById);
                                }
                                else {
                                    tvCategories.setText("Could not find a taxonomy with the specified taxonomy name.");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                tvCategories.setText(e.toString());
                            }

                        }
                    };

                    Response.ErrorListener errorListener = new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            tvCategories.setText(error.toString());
                        }
                    };

                    WCHRequest requestCategories = new WCHRequest(taxonomiesUrl, responseListener, errorListener);
                    queue.add(requestCategories);
                }
            }
        });

    }
}
