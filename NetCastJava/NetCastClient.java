package NetCastJava;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class NetCastClient {

    String url, access_token;
    String protocol;
    private static final int DEFAULT_TIMEOUT = 10000;
    private static final String XML = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
    private static final String KEY = XML + "<auth><type>AuthKeyReq</type></auth>";
    private static final String AUTH = XML + "<auth><type>%s</type><value>%s</value></auth>";
    private static final String COMMAND = XML + "<command><session>%s</session><type>%s</type>%s</command>";
    private final static int DEFAULT_PORT = 8080;


    private Response send_to_tv(String message_type, String message) {

        if (!message_type.equals("command") && this.protocol.equals(LG_Protocols.HDCP))
            message_type = "dtv_wifirc";
        String url = String.format("%s%s", this.url, message_type);
        try {
            URL uri = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
            conn.setRequestProperty("Content-Type", "application/atom+xml");
            conn.setDoOutput(true);
            conn.setConnectTimeout(DEFAULT_TIMEOUT);
            conn.setReadTimeout(DEFAULT_TIMEOUT);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = message.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            InputStreamReader ins;
            boolean error = false;
            if (conn.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                ins = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8);

            } else {
                error = true;
                ins = new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8);

            }
            try (BufferedReader br = new BufferedReader(
                    ins)) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                if (!error) {
                    return new Response(response.toString(), conn.getResponseCode());
                }

                //System.out.println(response);
                return new Response(response.toString(), conn.getResponseCode());
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Response();

    }

    public String get_query_data(String query) {
        String url = String.format("%s%s?target=%s", this.url, "data", query);
        try {
            URL uri = new URL(url);
            URLConnection conn = uri.openConnection();

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                response.append(inputLine.trim());
            in.close();

            return response.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return "";
    }


    String session_id = null;

    private void display_token() {
        this.send_to_tv("auth", KEY);
    }

    private void get_session_id() throws IOException {
        Response response = this.send_to_tv("auth", String.format(AUTH, "AuthReq", this.access_token));
        Pattern pattern = Pattern.compile("<session>(.*?)</session>");
        if (access_token == null) {
            display_token();
            throw new NetCastExceptions.AccessTokenError("Now Provide a Access token");
        }
        if (response.status_code > HttpURLConnection.HTTP_BAD_REQUEST) {
            throw new NetCastExceptions.SessionIdError(response.response);
        }

        Matcher matcher = pattern.matcher(response.response);
        if (matcher.find()) {
            this.session_id = matcher.group(1);
        }
    }

    public void send_command(int command_id) {
        String message = String.format(COMMAND, session_id, LG_Handlers.LG_HANDLE_KEY_INPUT, String.format("<value>%s</value>", command_id));
        send_to_tv("command", message);

    }

    public static String parse(String str, String pattern) {
        Pattern pat = Pattern.compile(pattern);
        Matcher matcher = pat.matcher(str);
        if (matcher.find())
            return matcher.group(1);
        else
            return matcher.group(0);
    }

    public String[] get_volume_info() {
        String resp = this.get_query_data(LG_Queries.VOLUME_INFO);

        return new String[]{
                parse(resp, "<level>(.*?)</level>"),
                parse(resp, "<minLevel>(.*?)</minLevel>"),
                parse(resp, "<maxLevel>(.*?)</maxLevel>")};

    }

    public NetCastClient(String host, String access_token) throws IOException {

        this.access_token = access_token;
        this.protocol = LG_Protocols.ROAP;
        this.url = String.format("http://%s:%s/%s/api/", host, DEFAULT_PORT, protocol);
        get_session_id();


    }

    public NetCastClient(String host) throws IOException {

        this.access_token = null;
        this.protocol = LG_Protocols.ROAP;
        this.url = String.format("http://%s:%s/%s/api/", host, DEFAULT_PORT, protocol);
        get_session_id();

    }

    public NetCastClient(String host, String access_token, String protocol) throws IOException {
        this.url = host;
        this.access_token = access_token;
        this.protocol = protocol;
        this.url = String.format("http://%s:%s/%s/api/", host, DEFAULT_PORT, protocol);
        get_session_id();
    }


}
