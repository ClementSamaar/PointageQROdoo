package rdtronic.csamaar.geoloc;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

import android.util.Log;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReqXMLRPC {
    final String SERVER_URL = "http://10.10.10.222:8069";
    final String DATABASE_NAME = "odoo_pointage";
    final String USERNAME = "admin";
    final String PASSWORD = "admin";

    static XmlRpcClient client = new XmlRpcClient();
    static XmlRpcClient models = new XmlRpcClient();
    private Object tuple;

    public int authenticate(){
        int uid = 0;
        try {
            final XmlRpcClientConfigImpl common_config = new XmlRpcClientConfigImpl();
            common_config.setServerURL(
                    new URL(String.format("%s/xmlrpc/2/common", SERVER_URL)));
            Map<String, String> versionInfo = (Map<String, String>) client.execute(common_config, "version", emptyList());

            uid = (int)client.execute(
                common_config, "authenticate", asList(
                DATABASE_NAME, USERNAME, PASSWORD, emptyMap()));

            Log.v("ARRAY SIZE", versionInfo.get("server_serie"));

            models.setConfig(new XmlRpcClientConfigImpl(){{
                setServerURL(new URL(String.format("%s/xmlrpc/2/object", SERVER_URL)));
            }});
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            Log.v("AUTHENTICATE : URL ERROR", e.getMessage());
        }
        catch (XmlRpcException e) {
            e.printStackTrace();
            Log.v("AUTHENTICATE : XML RPC ERROR", e.getMessage());
        }
        return uid;
    }

    public List<Object> getValuesFromOdoo (int UID) {
        List<Object> result = null;
        int recordsNb = 0;
        try {
            recordsNb = (Integer)models.execute("execute_kw", asList(
                    DATABASE_NAME, UID, PASSWORD,
                    "hr.attendance", "search_count",
                    Collections.singletonList(Collections.singletonList(
                            asList("employee_id", "=", 1)))
            ));

            result = asList((Object[]) models.execute("execute_kw", asList(
                    DATABASE_NAME, UID, PASSWORD,
                    "hr.attendance", "search_read",
                    Collections.singletonList(Collections.singletonList(
                            asList("employee_id", "=", 1))),
                    new HashMap() {{
                        put("fields", asList("id", "worked_hours"));
                        put("limit", 10);
                    }}
            )));
        }
        catch (XmlRpcException e) {
            e.printStackTrace();
        }
        Log.v("NB OF RECORDS", String.valueOf(recordsNb));

        if (result != null) {
            tuple = result.get(0);
            Log.v("TEST", result.get(0).toString());
        }
        return result;
    }
    /*{
        try {
            test = (HttpURLConnection) new URL(SERVER_URL).openConnection();
            client = new XMLRPCClient(new URL(SERVER_URL));
            client.setLoginData(USERNAME, PASSWORD);
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.v("Error : ", e.getMessage());
        }
    }*/
}
