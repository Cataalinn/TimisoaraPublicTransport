package ro.mihai.tpt.analytics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;

import ro.mihai.util.Formatting;

public class AnalyticsService implements IAnalyticsService {
	private String url;
	private String agent;
	
	public AnalyticsService(String https_url) {
		this(https_url, "TEST");
	}
	
	public AnalyticsService(String https_url, String agent) {
		this.url = https_url;
		this.agent = agent;
	}
	
	private String getContent(String localAddress, String data) {
		return localAddress+"\r\n"+data+"\r\n";
	}
	
	public String postData(String service, String data) throws IOException {
		URL u = new URL(url+"/"+service);
		URLConnection con = u.openConnection();
		con.setDoOutput(true);
		con.setReadTimeout(10000);
		con.setRequestProperty("User-Agent", this.agent);
		con.setRequestProperty("Referer", this.agent);
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		con.setRequestProperty("Accept", "*/*");
		con.setAllowUserInteraction(false);
        
		PrintStream ps = new PrintStream(con.getOutputStream());
		String local = getLocalAddress();
		String content = getContent(local, data);
		ps.print(content);
		ps.flush();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String result = br.readLine();
		br.close();

		return result;
	}
	
	public String generateDeviceId() throws IOException {
		return postData("generate_device_id", "");
	}
	
	public void postTimesBundle(String deviceId, String data) throws IOException {
		postData("post_times_bundle", deviceId+"\r\n"+data);
	}
	
    public static String getLocalAddress() {
    	ArrayList<String> all_addrs = new ArrayList<String>();
    	int maxInterfAddrs = 4;
    	int maxInterfName = 8;
    	int maxAddrLen = 40;
    	int maxAddrs = 5 * maxInterfAddrs;
        try {
        	Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        	while(interfaces.hasMoreElements() && all_addrs.size() < maxAddrs) {
        		NetworkInterface intf = interfaces.nextElement();
        		Enumeration<InetAddress> addrs = intf.getInetAddresses();
        		int addrCount = 0;
        		while(addrs.hasMoreElements() && addrCount <= maxInterfAddrs) {
        			InetAddress addr = addrs.nextElement();
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        int delim = sAddr.indexOf('%'); // ip6 port
                        if (delim >= 0)
                        	sAddr = sAddr.substring(0, delim);
                        String iname = intf.getName();
                        if (iname.length() > maxInterfName)
                        	iname = iname.substring(0, maxInterfName);
                        if (sAddr.length() > maxAddrLen)
                        	sAddr = sAddr.substring(0, maxAddrLen);
                        all_addrs.add(iname+"-"+sAddr);
                        addrCount += 1;
                    }
                }
            }
        } catch (IOException ex) {
        	
        }
        String result = "v02;"+Formatting.join(";", all_addrs);
        if (result.length() > 1024)
        	result = result.substring(0, 1024);
        return result;
    }
}
