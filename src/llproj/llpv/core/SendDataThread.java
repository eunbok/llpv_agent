package llproj.llpv.core;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import llproj.llpv.db.Database;
import llproj.llpv.vo.DataVO;

public class SendDataThread implements Runnable {
  private static final Logger log = Logger.getLogger(SendDataThread.class);
  Database db;
  boolean is_send_data = false;
  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  public SendDataThread(Database db) {
    this.db = db;
  }

  public void run() {
    try {
      Calendar calendar = Calendar.getInstance();
      calendar.add(Calendar.MINUTE, CmnVal.send_data_cycle_min);
      while (true) {
        if ("Y".equals(db.getConfig("is_send_data"))) {
          is_send_data = true;
        } else {
          is_send_data = false;
        }

        if (is_send_data) {
          if (calendar.getTime().getTime() <= new Date().getTime()) {
            ArrayList<DataVO> data_arr = new ArrayList<DataVO>();
            JSONArray jsonArr = new JSONArray();

            String endDate = sdf.format(calendar.getTime());
            calendar.add(Calendar.MINUTE, -CmnVal.send_data_cycle_min);
            String startDate = sdf.format(calendar.getTime());
            log.debug("[Send data] " + startDate + " ~ " + endDate);
            db.getSendData(data_arr, startDate, endDate);
            // 현시간 - 주기 ~ 현시간 데이터 조회
            for (int i = 0; i < data_arr.size(); i++) {
              JSONObject temp = new JSONObject();

              DataVO dv = (DataVO) data_arr.get(i);
              temp.put("run_file", dv.getRun_file());
              temp.put("run_title", dv.getRun_title());
              temp.put("run_sec", dv.getRun_sec());
              temp.put("_datetime", dv.get_datetime());
              temp.put("stored_time", dv.getStored_time());
              jsonArr.put(temp);
            }
            log.debug("send length : " + jsonArr.length() + " (length : "
                + jsonArr.toString().length() + ")");

            int stateCode = postData(CmnVal.url, jsonArr.toString());
            log.debug("stateCode:" + stateCode);
            calendar.add(Calendar.MINUTE, CmnVal.send_data_cycle_min * 2);
          }
        }

        Thread.sleep(1000);
      }
    } catch (InterruptedException e) {
      log.error(e.getMessage());
      e.printStackTrace();
    }
  }

  // https://digitalbourgeois.tistory.com/58
  private int postData(String requestURL, String message) {
    int stateCode;
    try {
      SSLContext sslContext = SSLContext.getInstance("SSL");

      // set up a TrustManager that trusts everything
      sslContext.init(null, new TrustManager[] {new X509TrustManager() {
        public X509Certificate[] getAcceptedIssuers() {
          // System.out.println("getAcceptedIssuers =============");
          return null;
        }

        public void checkClientTrusted(X509Certificate[] certs, String authType) {
          // System.out.println("checkClientTrusted =============");
        }

        public void checkServerTrusted(X509Certificate[] certs, String authType) {
          // System.out.println("checkServerTrusted =============");
        }
      }}, new SecureRandom());

      SSLSocketFactory sf = new SSLSocketFactory(sslContext);
      Scheme httpsScheme = new Scheme("https", 443, sf);
      SchemeRegistry schemeRegistry = new SchemeRegistry();
      schemeRegistry.register(httpsScheme);

      // apache HttpClient version >4.2 should use BasicClientConnectionManager
      ClientConnectionManager cm = new SingleClientConnManager(schemeRegistry);
      HttpClient client = new DefaultHttpClient(cm);

      // HttpClient client = HttpClientBuilder.create().build();

      HttpPost postRequest = new HttpPost(requestURL);
      // postRequest.setHeader("Accept", "application/json");
      // postRequest.setHeader("Connection", "keep-alive");
      postRequest.setHeader("Content-Type", "application/json");

      // int timeOut = 10;
      // RequestConfig requestConfig = RequestConfig.custom()
      // .setSocketTimeout(timeOut*1000)
      // .setConnectTimeout(timeOut*1000)
      // .setConnectionRequestTimeout(timeOut*1000)
      // .build();
      // postRequest.setConfig(requestConfig);

      // postRequest.setEntity(new StringEntity(message));
      postRequest.setEntity(new StringEntity(message, "UTF-8"));



      HttpResponse response = client.execute(postRequest);

      stateCode = response.getStatusLine().getStatusCode();
      if (stateCode == 200) {
        ResponseHandler<String> handler = new BasicResponseHandler();
        String body = handler.handleResponse(response);
        log.debug(body);
      } else {
        log.debug("response is error : " + stateCode);
      }
      return stateCode;
    } catch (Exception e) {
      System.err.println(e.toString());
      return 500;
    }
  }

}
