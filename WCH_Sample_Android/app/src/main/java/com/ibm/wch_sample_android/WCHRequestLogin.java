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

import android.util.Base64;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Herbst on 3/21/2017.
 */

public class WCHRequestLogin extends StringRequest{

    private Map<String, String> params;
    private Map<String, String> headers;
    private String username;
    private String password;

    //Constructor: pass in username and password, as well as the actions to do on response (error or success)
    public WCHRequestLogin(String usrnme, String psswrd, String wchLoginURL, Response.Listener<String> listener, Response.ErrorListener error){
        super(Method.GET, wchLoginURL, listener, error);
        username = usrnme;
        password = psswrd;

        params = new HashMap<>();
        headers = new HashMap<>();

        //Encrypt credentials and add them to the request header
        String credentials = username + ":" + password;
        String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        headers.put("Authorization", auth);
    }


    @Override
    public Map<String, String> getParams() {
        return params;
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }
}
