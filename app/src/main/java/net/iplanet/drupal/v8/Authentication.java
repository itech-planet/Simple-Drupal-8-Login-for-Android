package net.iplanet.drupal.v8;

import net.iplanet.utils.Ciphering;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.apache.http.protocol.HTTP;

public class Authentication {

    public JSONObject doAttemptLogin(String mUser, String mPassword) throws Exception{
        JSONObject json = new JSONObject();
        json.put(Tokens.KEY_NAME, mUser);
        json.put(Tokens.KEY_PASS, mPassword);
        Authentication Auth = new  Authentication();
        HttpResponse response = Auth.doLogin(json);
        String jsonResponse = EntityUtils.toString(response.getEntity());
        return new JSONObject(jsonResponse);
    }

    public String getAuthorization(String mUser, String mPassword)throws Exception{
        Ciphering Cph = new Ciphering();
        return "Basic " + Cph.getBase64(mUser + mPassword);
    }

    public HttpResponse doLogin(JSONObject json) throws Exception {
        String endpoint = Tokens.serv_end_Pnt_LOGIN;
        return login_DrupalR825(endpoint, json);
    }

    public HttpResponse doLogout(String mCsrf_token, String mLogoutToken, String mAuth) throws Exception {
        String endpoint = Tokens.serv_end_Pnt_LOGOUT;
        return logout_DrupalR825(endpoint, mCsrf_token, mLogoutToken, mAuth);
    }

    private HttpResponse login_DrupalR825(String endpoint, JSONObject json) throws Exception {

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(endpoint);
        StringEntity se = new StringEntity(json.toString());
        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        httppost.setEntity(se);
        HttpResponse response = httpclient.execute(httppost);
        return response;
    }

    private HttpResponse logout_DrupalR825(String endpoint, String mCsrf_token , String mLogoutToken, String mAuth) throws Exception  {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(endpoint);
        HttpResponse response = httpclient.execute(httpget);
        return response;
    }
}
