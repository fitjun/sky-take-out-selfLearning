package com.sky.utils;

import com.sky.entity.Address;
import com.sky.entity.Location;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.springframework.web.util.UriUtils;
import cn.hutool.json.JSONUtil;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashMap;
import java.util.Map;
@Slf4j
public class DistanceUtil {
    private static final double EARTH_RADIUS = 6371.0; // 地球半径，单位：公里
    /**
     * 计算两点之间的距离（单位：公里）
     * @param lat1 点1的纬度
     * @param lon1 点1的经度
     * @param lat2 点2的纬度
     * @param lon2 点2的经度
     * @return 两点之间的距离
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // 将经纬度转换为弧度
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Haversine公式
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c;
        System.out.println("商铺与地址距离："+distance);
        return distance;
    }
    /**
     * 判断两点距离是否在5公里以内
     * @return true表示距离≤5公里，false表示距离>5公里
     */
    public boolean isWithin5Km(String ak,String address1,String address2) throws Exception {

        String URL = "https://api.map.baidu.com/geocoding/v3?";

        Map params1 = new LinkedHashMap<String, String>();
        params1.put("address", address1);
        params1.put("output", "json");
        params1.put("ak", ak);
        params1.put("callback", "showLocation");
        Location l1 = requestGetAK(URL, params1);
        Map params2 = new LinkedHashMap<String, String>();
        params2.put("address", address2);
        params2.put("output", "json");
        params2.put("ak", ak);
        params2.put("callback", "showLocation");
        Location l2 = requestGetAK(URL, params2);
        double distance = calculateDistance(l1.getLat(),l1.getLng(),l2.getLat(),l2.getLng());
        return distance <= 5.0;
    }
    public Location requestGetAK(String strUrl, Map<String, String> param) throws Exception {

        if (strUrl == null || strUrl.length() <= 0 || param == null || param.size() <= 0) {
            return null;
        }
        StringBuffer queryString = new StringBuffer();
        queryString.append(strUrl);
        for (Map.Entry<?, ?> pair : param.entrySet()) {
            queryString.append(pair.getKey() + "=");
            //    第一种方式使用的 jdk 自带的转码方式  第二种方式使用的 spring 的转码方法 两种均可
            //    queryString.append(URLEncoder.encode((String) pair.getValue(), "UTF-8").replace("+", "%20") + "&");
            queryString.append(UriUtils.encode((String) pair.getValue(), "UTF-8") + "&");
        }

        if (queryString.length() > 0) {
            queryString.deleteCharAt(queryString.length() - 1);
        }

        java.net.URL url = new URL(queryString.toString());
        System.out.println(queryString.toString());
        URLConnection httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.connect();

        InputStreamReader isr = new InputStreamReader(httpConnection.getInputStream());
        BufferedReader reader = new BufferedReader(isr);
        StringBuffer buffer = new StringBuffer();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        reader.close();
        isr.close();
        System.out.println(buffer);
        String json = buffer.toString();
        String[] split = json.split("\\(");
        String[] split1 = split[1].split("\\)");
        System.out.println(split1[0]);
        Address bean = JSONUtil.toBean(split1[0], Address.class);
        return bean.getResult().getLocation();
    }
}
