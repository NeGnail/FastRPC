package main.java.com.meelody.rpc.util;

import main.java.com.meelody.rpc.exception.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class Config {
    protected static final Log log = LogFactory.getLog(Config.class);
    private static HashMap<String,String> map=new HashMap<>();

    public static int getRW_COUNT() throws IOException, ConfigurationException {
        if (map.isEmpty()){
            loadConfig();
        }
        return Integer.valueOf(map.get("rw_count"));
    }
    public static String getPROTOCOL() throws IOException, ConfigurationException {
        if (map.isEmpty()){
            loadConfig();
        }
        return map.get("protocol");
    }
    public static double getRW_RADIO() throws IOException, ConfigurationException {
        if (map.isEmpty()){
            loadConfig();
        }
        return Double.valueOf(map.get("rw_radio"));
    }
    public static int getPORT() throws IOException, ConfigurationException {
        if (map.isEmpty()){
            loadConfig();
        }
        return Integer.valueOf(map.get("port"));
    }
    public static int getCONNECT_COUNT() throws IOException, ConfigurationException {
        if (map.isEmpty()){
            loadConfig();
        }
        return Integer.valueOf(map.get("connect_count"));
    }
    public static String getCACHE() throws IOException, ConfigurationException {
        if (map.isEmpty()){
            loadConfig();
        }
        return map.get("cache");
    }
    public static String getLOADBALANCE() throws IOException, ConfigurationException {
        if (map.isEmpty()){
            loadConfig();
        }
        return map.get("loadbalance");
    }
    public static int getHANDLE_COUNT() throws IOException, ConfigurationException {
        if (map.isEmpty()){
            loadConfig();
        }
        return Integer.valueOf(map.get("handle_count"));
    }

    public static String getSERIALIZER() throws IOException, ConfigurationException {
        if (map.isEmpty()){
            loadConfig();
        }
        return map.get("serializer");
    }

    public static void loadConfig() throws IOException, ConfigurationException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("configuration")));
        String line=null;
        while((line=reader.readLine()) != null){
            if(!line.contains(":")){
                throw new ConfigurationException("error configuration file");
            }
            String[] infos=line.split(":");
            if(infos.length!=2){
                throw new ConfigurationException("error configuration file");
            }
            map.put(infos[0],infos[1]);
        }
    }
}
