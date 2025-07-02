package com.sky.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Slf4j
public class Myjwt {
    private static final String ALGORITHM = "HmacSHA256";
    private static final int MIN_KEY_LENGTH = 32;
    public static String createToken(Map<String,Object> claims,String secretKey,Long ttl){
        if (secretKey.getBytes(StandardCharsets.UTF_8).length<MIN_KEY_LENGTH){
            log.error("密钥需大于32位");
            throw new SecurityException("密钥需大于32位");
        }
        Map<String,Object> header = new HashMap<>();
        header.put("alg",ALGORITHM);
        header.put("typ","jwt");
        Map<String,Object> payload = new HashMap<>(claims);
        payload.put("exp", Instant.now().plusSeconds(ttl).getEpochSecond());
        //先json序列化再base64转换
        String base64Header = Base64UrlEncode(jsonSerialize(header));
        String base64payload = Base64UrlEncode(jsonSerialize(payload));
        String unsignedData = base64Header+"."+base64payload;
        String signed = signData(unsignedData,secretKey);
        return unsignedData+"."+signed;
    }

    public static Map<String,Object> parseToken(String token,String secretKey){
        String[] parts = token.split("\\.");
        if(parts.length<3){
            log.error("token格式错误");
            throw new SecurityException("token格式错误");
        }
        String header = parts[0];
        String payload = parts[1];
        String signed = parts[2];
        String compared = signData(header+"."+ payload,secretKey);
        if(!safeCompare(compared, signed)){
            log.error("签名验证失败");
            throw new SecurityException("签名验证失败");
        }

        String tokenjson = new String(
                Base64.getUrlDecoder().decode(payload),
                StandardCharsets.UTF_8);
        Map<String,Object> data = parseJson(tokenjson);
        long curTime = Instant.now().getEpochSecond();
        if (data.containsKey("exp") && curTime>((Number)data.get("exp")).longValue()){
            log.error("令牌已过期");
            throw new SecurityException("令牌已过期");
        }
        return data;
    }

    private static Map<String, Object> parseJson(String tokenjson) {
        Map<String,Object> map = new HashMap<>();
        tokenjson = tokenjson.substring(1,tokenjson.length()-1).trim();
        //存储每一对键值对
        List<String> pairs = new ArrayList<>();
        //嵌套的逗号如何辨别是属于值中的嵌套键值对的
        int braceLevel = 0;
        int start = 0;
        for (int i =0 ; i<tokenjson.length() ; i++){
            char c = tokenjson.charAt(i);
            if(c=='{')braceLevel++;
            if(c=='}')braceLevel--;

            if (c==',' && braceLevel == 0){
                pairs.add(tokenjson.substring(start,i).trim());
                start=i+1;
            }
        }
        //以上是根据逗号来进行插入、最后一对键值对没有逗号，但是start已经记录了最后一对的开始下标，可以直接用
        pairs.add(tokenjson.substring(start).trim());
        log.info("获取的json数据：{}",pairs);
        for (String pair : pairs) {
            //找第一个：的下标切割，而不是简单的split分割，这样会将嵌套的也割开了
            int index = pair.indexOf(":");
            String key = pair.substring(0,index);
            String value = pair.substring(index+1);
            if(key.startsWith("\"")&&key.endsWith("\"")){
                key = key.substring(1,key.length()-1);
            }
            Object parseValue;
            if (value.startsWith("\"")){
                parseValue = value.substring(1,value.length()-1).replace("\\\"","\"");
            }else if(value.equals("null")){
                parseValue = null;
            }else if (value.equals("true")){
                parseValue = true;
            }else if (value.equals("false")){
                parseValue = false;
            }else {
                try {
                    //指数、小数点等都用double
                    if (value.contains(".") || value.contains("e") || value.contains("E")){
                        parseValue = Double.parseDouble(value);
                    }else {
                        parseValue = Long.parseLong(value);
                    }
                }catch (NumberFormatException e){
                    parseValue = value;
                }
            }
            map.put(key,parseValue);
        }
        log.info("反序列化后的json数据：{}",map);
        return map;
    }

    private static boolean safeCompare(String compared, String signed) {
        byte [] b1 = compared.getBytes(StandardCharsets.UTF_8);
        byte [] b2 = signed.getBytes(StandardCharsets.UTF_8);
        int result = b1.length ^ b2.length;
        for (int i=0 ; i<Math.min(b1.length,b2.length) ; i++){
            result = result | b1[i] ^ b2[i];
        }
        return result==0;
    }

    private static String signData(String unsignedData, String secretKey) {
        try {
            Mac sh256 = Mac.getInstance(ALGORITHM);
            //传入密钥和加密算法
            SecretKeySpec keySpec = new SecretKeySpec(
                    secretKey.getBytes(StandardCharsets.UTF_8),
                    ALGORITHM
            );
            sh256.init(keySpec);
            byte[] bytes = sh256.doFinal(unsignedData.getBytes(StandardCharsets.UTF_8));
            //base64加密直接可以得到字符串，在返回
            return Base64UrlEncode(bytes);
        }catch (Exception e){
            log.error("签名生成失败");
            throw new SecurityException("签名生成失败");
        }
    }

    public static String Base64UrlEncode(String data){
        return Base64UrlEncode(data.getBytes(StandardCharsets.UTF_8));
    }
    public static String Base64UrlEncode(byte[] data){
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }

    public static String jsonSerialize(Map<String,Object> data){
        StringBuilder sb = new StringBuilder("{");
        for (Map.Entry<String,Object> entry : data.entrySet()){
            sb.append("\"").append(entry.getKey()).append("\":");
            Object value = entry.getValue();
            //可能的情况：
            // 1.value为空,直接插入null，不需要双引号等东西
            if (value==null){
                sb.append("null");
            }
            // 2.为字符串
            else if (entry.getValue() instanceof String){
                sb.append("\"").append(value).append("\"");
                // 3.为数字或布尔类型
            }else if (value instanceof Number || value instanceof Boolean){
                sb.append(value);
            }else {
                //value内部双引号，替换成\" \需要转义  "也需要转义  所以是\\ + \"
                sb.append("\"").append(value.toString().replace("\"","\\\"")).append("\"");
            }
            sb.append(",");
        }
        //非空才有逗号，所以要判断是否为空
        if (!data.isEmpty()){
            //最后一位是length-1，因为下标从0开始，length从1开始
            sb.deleteCharAt(sb.length()-1);
        }
        sb.append("}");
        return sb.toString();
    }

}
