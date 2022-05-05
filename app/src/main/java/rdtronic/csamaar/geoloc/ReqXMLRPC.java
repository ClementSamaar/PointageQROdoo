package rdtronic.csamaar.geoloc;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

import android.util.Log;

import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class ReqXMLRPC {
    final String SERVER_URL = "127.0.0.1";
    final String DB_NAME = "";
    final String USERNAME = "admin";
    final String PASSWORD = "admin";

    private XMLRPCClient client;
    {
        try {
            client = new XMLRPCClient(new URL("http://localhost:8069/"));
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            Log.v("Error : ", e.getMessage());
        }
    }




    /*private final int authenticate() throws MalformedURLException {
        final XmlRpcClientConfigImpl common_config = new XmlRpcClientConfigImpl();
        common_config.setServerURL(
        new URL(SERVER_URL);

        int uid = (int)client.execute(
        common_config, "authenticate", asList(
        db, username, password, emptyMap()));
    }*/
}
