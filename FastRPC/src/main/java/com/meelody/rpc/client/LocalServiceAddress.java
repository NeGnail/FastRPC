package main.java.com.meelody.rpc.client;

import javafx.fxml.LoadException;
import main.java.com.meelody.rpc.info.Execution;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LocalServiceAddress implements LoadBalance{

    private static HashMap<String,List<InetAddressWeight>> serviceAddress;


    @Override
    public InetSocketAddress findServiceAddress(Execution execution) throws IOException {
        List<InetAddressWeight> inetAddressWeights=serviceAddress.get(execution.getService());
        if(inetAddressWeights.size()==1){
            return inetAddressWeights.get(0).socketAddress;
        }else if(inetAddressWeights.size()==0){
            throw new LoadException("no address");
        }
        int random= (int) (Math.random()*100);
        InetSocketAddress flagAddress = null;
        int length=inetAddressWeights.size();
        for(int i=0;i<length;i++){
            int weight=inetAddressWeights.get(i).weight;
            if(weight>random){
                flagAddress=inetAddressWeights.get(i).socketAddress;
                break;
            }
        }
        if(flagAddress==null){
            flagAddress= inetAddressWeights.get(inetAddressWeights.size()-1).socketAddress;
        }

       return flagAddress;
    }

    public LocalServiceAddress() throws IOException {
        loadInfo();
    }

    public void loadInfo() throws IOException {
        serviceAddress=new HashMap<String,List<InetAddressWeight>>();
        BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream("serviceAddress")));
        String line=null;
        while((line=reader.readLine())!=null){
            putInetAddressWeight(line);
        }
    }

    private void putInetAddressWeight(String line) {
        String service=null;
        List<InetAddressWeight> list=new ArrayList<>();

        String[] info=line.split("\\{");
        service=info[0];

        String info1=info[1].substring(0,info[1].length()-1);
        String[] inetAddressWeights=info1.split(",");
        for(String inetAddressWeightString:inetAddressWeights){
            String[] iw=inetAddressWeightString.split("\\*");

            String[] addressPort=iw[0].split(":");
            InetSocketAddress inetSocketAddress=new InetSocketAddress(addressPort[0],Integer.valueOf(addressPort[1]));
            int weight=Integer.valueOf(iw[1].substring(0,iw[1].length()-1));

            InetAddressWeight inetAddressWeight=new InetAddressWeight(inetSocketAddress,weight);
            list.add(inetAddressWeight);
        }
        serviceAddress.put(service,list);
    }

    private class InetAddressWeight{
        private InetSocketAddress socketAddress;
        private int weight;

        public InetAddressWeight(InetSocketAddress socketAddress, int weight) {
            this.socketAddress = socketAddress;
            this.weight = weight;
        }
    }
}
