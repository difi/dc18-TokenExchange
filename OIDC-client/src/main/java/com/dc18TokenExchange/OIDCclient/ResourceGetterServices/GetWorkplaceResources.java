package com.dc18TokenExchange.OIDCclient.ResourceGetterServices;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GetWorkplaceResources {

    @Value("${resource.server.username}")
    private String username;

    @Value("${resource.server.password}")
    private String password;

    @Value("${resource.server.url}")
    private String url;


    //Sends request to resource server in order to
    public BufferedImage getWorkplaceImages(int orgNum, String type) throws IOException {
        String auth = makeAuthorization(username, password);
        String orgNumString = String.valueOf(orgNum);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url + "/workplace/" + type);

        post.setHeader(new BasicHeader("Authorization", "Basic "+auth));
        post.setHeader(new BasicHeader("Content-type", "application/json"));

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("orgNum", orgNumString));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        HttpResponse response = client.execute(post);
        BufferedImage bi = ImageIO.read(response.getEntity().getContent());

        client.close();

        return bi;

        //saveImageWithBytes("C:\\temp\\newImage.png", bi);
        //return logoBytes;
    }

    //Returns the specified organization's home page
    public String getHomeUrl(int orgNum) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(url + "/workplace/" + orgNum + "/homepage");

        HttpResponse response = client.execute(get);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        String url = rd.readLine();

        client.close();

        return url;
    }

    //Gets color profile from the specified organization
    public Map<String, String> getTheme(int orgNum) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(url + "/workplace/" + orgNum + "/theme");

        HttpResponse response = client.execute(get);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        String themeMapString = rd.readLine();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> themeMap = objectMapper.readValue(themeMapString, Map.class);

        client.close();

        return themeMap;
    }

    //Creates authorizaion header for resource server authorization
    public String makeAuthorization(String resource_server_username, String resource_server_password){
        String clientAuth = resource_server_username+":"+resource_server_password;
        byte[] clientAuthEncoded = Base64.encodeBase64(clientAuth.getBytes());
        return new String(clientAuthEncoded);
    }

    public byte[] getImageAsBytes(BufferedImage buffer) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(buffer, "png", baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();
        return imageInByte;
    }

    public void saveImageWithBytes(String newPath, byte[] logoBytes){

        try{
            FileOutputStream fos = new FileOutputStream(newPath);
            fos.write(logoBytes);
            fos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}

