/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.lotame.api;

import com.mycompany.lotame.api.Audience.AudienceBuilder;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Chris
 */
public class LotameAPI {

    private static final String BASE_URL = "https://api.lotame.com/";
    private static final String ENCODING = "UTF-8";
    private String activeToken;
    
    public enum Direction {
        ASCENDING("ASC"),
        DESCENDING("DESC");
        
        private String value;
        
        private Direction(String value) {
            this.value = value;
        }
                
        @Override
        public String toString() {
            return value;
        }
        
        public static Direction getDirection(String value) {
            if (value.equals(ASCENDING.toString())) {
                return ASCENDING;
            } else if (value.equals(DESCENDING.toString())) {
                return DESCENDING;
            } else {
                return null;
            }
        }
    }

    public void setActiveToken(String token) {
        activeToken = token;
    }

    public String getToken(String userId, String password) throws UnsupportedEncodingException, MalformedURLException, IOException, InterruptedException, ExecutionException {
        String params = String.format("email=%s&password=%s", URLEncoder.encode(userId, ENCODING), URLEncoder.encode(password, ENCODING));
        InputStream stream = call("", "POST", "", params);
        return new BufferedReader(new InputStreamReader(stream)).readLine();
    }

    public Collection<Audience> getAudiences(String clientId, int pageCount, String sortAttr, Direction direction) throws UnsupportedEncodingException, MalformedURLException, IOException, InterruptedException, ExecutionException {
        clientId = URLEncoder.encode(clientId, ENCODING);
        String params = String.format("client_id=%s&page_count=%d&sort_attr=%s&sort_order=%s", clientId, pageCount, sortAttr, direction.toString());
        InputStream stream = authorizedCall("audstats/reports/topAudiences", "GET", params, "");
        return parseXMLAudiences(stream);
    }

    private InputStream call(String urlString, String method, String params, String body) throws UnsupportedEncodingException, MalformedURLException, IOException, InterruptedException, ExecutionException {
        return call(urlString, method, params, body, new HashMap<String, String>());
    }

    private InputStream authorizedCall(String urlString, String method, String params, String body) throws MalformedURLException, IOException, ExecutionException, InterruptedException {
        Map<String, String> headers = new HashMap<String, String>();
        // TODO: Check for active token here...
        headers.put("Authorization", activeToken);
        return call(urlString, method, params, body, headers);
    }

    private InputStream call(String urlString, String method, String params, String body, Map<String, String> headers) throws MalformedURLException, IOException, InterruptedException, ExecutionException {
        // Using Future here to simulate async request
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<Response> response = executor.submit(new Request(new URL(BASE_URL + urlString + (params.length() > 1 ? "?" + params : "")), method, body, headers) {
        });
        InputStream stream = response.get().getBody();
        executor.shutdown();
        return stream;
    }

    private Collection<Audience> parseXMLAudiences(InputStream stream) throws IOException {
        // TODO: This is really lame, and it should be it's own service...
        List<Audience> audiences = new ArrayList<Audience>();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document dom = db.parse(stream);

            Element elem = dom.getDocumentElement();
            NodeList nodeList = elem.getElementsByTagName("ns3:stat");
            for (int i = 0; i < nodeList.getLength(); ++i) {
                elem = (Element) nodeList.item(i);
                AudienceBuilder builder = new Audience.AudienceBuilder();
                NodeList nodes = elem.getElementsByTagName("ns3:audienceId");
                if (nodes != null && nodes.getLength() > 0 && nodes.item(0).getFirstChild() != null) {
                    builder.setId(Integer.parseInt(nodes.item(0).getFirstChild().getNodeValue()));
                }
                nodes = elem.getElementsByTagName("ns3:audienceName");
                if (nodes != null && nodes.getLength() > 0 && nodes.item(0).getFirstChild() != null) {
                    builder.setName(nodes.item(0).getFirstChild().getNodeValue());
                }
                nodes = elem.getElementsByTagName("ns3:audienceTargetingCode");
                if (nodes != null && nodes.getLength() > 0 && nodes.item(0).getFirstChild() != null) {
                    builder.setTargetingCode(nodes.item(0).getFirstChild().getNodeValue());
                }
                nodes = elem.getElementsByTagName("ns3:opportunities");
                if (nodes != null && nodes.getLength() > 0 && nodes.item(0).getFirstChild() != null) {
                    builder.setOpportunities(Long.parseLong(nodes.item(0).getFirstChild().getNodeValue()));
                }
                nodes = elem.getElementsByTagName("ns3:pageViews");
                if (nodes != null && nodes.getLength() > 0 && nodes.item(0).getFirstChild() != null) {
                    builder.setPageViews(Long.parseLong(nodes.item(0).getFirstChild().getNodeValue()));
                }
                nodes = elem.getElementsByTagName("ns3:uniques");
                if (nodes != null && nodes.getLength() > 0 && nodes.item(0).getFirstChild() != null) {
                    builder.setUniques(Long.parseLong(nodes.item(0).getFirstChild().getNodeValue()));
                }
                audiences.add(builder.build());
            }
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return audiences;
    }

    private class Request implements Callable<Response> {

        private URL url;
        private String method;
        private String body;
        private Map<String, String> headers;

        public Request(URL url, String method, String body, Map<String, String> headers) {
            this.url = url;
            this.method = method;
            this.body = body;
            this.headers = headers;
        }

        @Override
        public Response call() throws Exception {
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod(method);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("Content-Length", "" + Integer.toString(body.getBytes().length));

            for (String key : headers.keySet()) {
                con.setRequestProperty(key, headers.get(key));
            }


            con.setDoInput(true);

            if (body.length() > 0) {
                con.setDoOutput(true);
                DataOutputStream out = new DataOutputStream(con.getOutputStream());
                out.writeBytes(body);
                out.close();
            }

            if (con.getResponseCode() != 200) {
                throw new IOException("Invalid server response while retrieving token: " + con.getResponseCode());
            } else {
                return new Response(con.getInputStream());
            }
        }
    }

    private class Response {

        private InputStream body;

        public Response(InputStream body) {
            this.body = body;
        }

        public InputStream getBody() {
            return body;
        }
    }
}
