/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inout;

import java.net.URL;
import java.net.URLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 *
 * @author Steinar
 */
public class NetGetConnection {
    

public static String backStreng(String refstreng){
    
    if(refstreng == null)
        refstreng = "";
    
    refstreng = refstreng.replace("1ae1","æ");
    refstreng = refstreng.replace("1oe1","ø");
    refstreng = refstreng.replace("1aa1","å");
    refstreng = refstreng.replace("1AE1","Æ");
    refstreng = refstreng.replace("1OE1","Ø");
    refstreng = refstreng.replace("1AA1","Å");

    return refstreng;
}

public static String getUrlAsString(String url)
{
    try
    {
        URL urlObj = new URL(url);
        URLConnection con = urlObj.openConnection();

        con.setDoOutput(true); // we want the response 
        con.setRequestProperty("Cookie", "myCookie=test123");
        con.connect();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        String newLine = System.getProperty("line.separator");
        while ((inputLine = in.readLine()) != null)
        {
            response.append(inputLine + newLine);
        }

        in.close();

        return response.toString();
    }
    catch (Exception e)
    {
        throw new RuntimeException(e);
    }
}
    
}
