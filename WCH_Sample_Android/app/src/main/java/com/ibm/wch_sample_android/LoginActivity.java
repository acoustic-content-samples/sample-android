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
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

public class LoginActivity extends AppCompatActivity {

    // replace baseTenantAPIURL with the link to your tenant API
    private final String baseTenantAPIURL = "<replace this with your WCH tenantAPIURL>";
    private final String wchLoginURL = baseTenantAPIURL + "/login/v1/basicauth";

    //declare UI elements
    EditText etUserName;
    EditText etPassword;
    Button buttonLogin;
    TextView tvErrorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initialize UI elements
        etUserName = (EditText) findViewById(R.id.etUserName);
        etPassword = (EditText) findViewById(R.id.etPassword);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        tvErrorText = (TextView) findViewById(R.id.tvErrorText);

        etUserName.requestFocus();//set initial focus to username field

        // Use a lenient CookieManager
        final CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        //Attempts login with given credentials
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //On success
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Initialize new intent
                        Intent intentLogin = new Intent(LoginActivity.this, HomeActivity.class);

                        try {
                            JSONArray responseJSONArray = new JSONArray(response);//get response as a JSONArray
                            if (responseJSONArray != null) {
                                JSONObject responseJSON = responseJSONArray.getJSONObject(0);//set JSONObject as first item in the JSONArray
                                if (responseJSON != null) {
                                    //add variables to the Intent
                                    intentLogin.putExtra("baseURL", responseJSON.getString("baseUrl"));
                                    intentLogin.putExtra("tenantID", responseJSON.getString("tenantId"));

                                    //start WCH
                                    LoginActivity.this.startActivity(intentLogin);
                                } else {
                                    tvErrorText.setText("JSONException: failed to create JSONObject from response: " + response);
                                }
                            } else {
                                tvErrorText.setText("JSONException: failed to create JSONArray from response: " + response);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            tvErrorText.setText(e.getMessage() + "\nJSONException: failed to create JSONObject from response");
                        }
                    }
                };
                //WCH authorization failed
                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        tvErrorText.setText(error.toString());
                    }
                };

                //check if credentials are entered
                if (etUserName.getText().toString().trim().length() == 0) {
                    tvErrorText.setText("No username supplied!");
                } else if (etPassword.getText().toString().trim().length() == 0) {
                    tvErrorText.setText("No password supplied!");
                } else {
                    // Instantiate the RequestQueue.
                    RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                    //Create new login request and add it to the Queue
                    WCHRequestLogin requestLogin = new WCHRequestLogin(etUserName.getText().toString().trim(), etPassword.getText().toString().trim(), wchLoginURL, responseListener, errorListener);
                    queue.add(requestLogin);
                }
            }
        });
    }
}
