package com.sky.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Myjwt {

    private static final String ALGORITHM = "HmacSHA256";
    public static String createJwt(Map<String, Object> claims,String secretKey,long expTime){
        //base64将加密算法以及载荷加密，后面再用Hamc256算法将载荷和密钥结合形成最后的密钥，再将三部分组合即是jwtToken

        //信息都用map键值对来装，否则后面解密出来都不知道是干嘛的
        Map<String,Object> headMap = new HashMap<>();
        headMap.put("alg",ALGORITHM);
        headMap.put("typ","Jwt");
        String header = base64Encode(serializeJson(headMap));

        claims.put("exp", Instant.now().plusSeconds(expTime).getEpochSecond());
        String payLoadBase64 = base64Encode(serializeJson(claims));

        //结合加密算法与密钥、载荷组成token最重要的标识部分，这部分对了才是对的token，否则就是被篡改过的,传入的是加密算法与载荷结合的base64编码、还有原始密钥
        //未加密所以叫unsignedToken，不仅方法要学、命名也要学的合理
        String unsignedToken = header+"."+payLoadBase64;
        String signature = signData(unsignedToken,secretKey);
        return unsignedToken+"."+signature;
    }

    public static Map<String,Object> parseToken(String token, String secretKey) {
        //1.检查加密是否正确
        //token不是三部分、直接返回错误
        String[] split = token.split("\\.");
        if (split.length<3){
            throw new IllegalArgumentException("无效token格式");
        }
        //不用解密、加密算法本来就是传入base64编码数据做的
        String header = split[0];
        String payload = split[1];
        String signed = split[2];
        String compared = signData(header+"."+payload,secretKey);
        if (!secureCompare(signed,compared)){
            throw new SecurityException("签名验证失败");
        }
        //2.取出数据
        //用String的构造方法传入二进制数组和编码格式可以生产字符串，只需要解码载荷部分、其他部分不携带信息、都是校验位
        String payloadJson = new String(
                Base64.getUrlDecoder().decode(payload),
                StandardCharsets.UTF_8
        );
        //3.封装数据
        Map<String, Object> data = parseJson(payloadJson);
        //4.检查是否过期，要是exp没有，说明是永久token，直接短路放，通过if的都是过期的
        long curTime = Instant.now().getEpochSecond();
        if (data.containsKey("exp") && curTime>((Number)data.get("exp")).longValue()){
            throw new SecurityException("令牌已过期");
        }
        //5.返回
        return data;
    }

    private static String signData(String unsignedToken, String secretKey) {
        try {
            Mac sha256 = Mac.getInstance(ALGORITHM);
            //传入的是密钥和加密算法
            SecretKeySpec spec = new SecretKeySpec(
                    secretKey.getBytes(StandardCharsets.UTF_8),
                    ALGORITHM
            );
            sha256.init(spec);
            byte[] bytes = sha256.doFinal(unsignedToken.getBytes(StandardCharsets.UTF_8));
            return base64Encode(bytes);
        }catch (Exception e){
            return "签名失败";
        }
    }

    //二进制数组转字符串不好转、但字符串转二进制数组好转
    public static String base64Encode(String data){
        return base64Encode(data.getBytes(StandardCharsets.UTF_8));
    }

    public static String base64Encode(byte [] data){
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }

    public static String serializeJson(Map<String,Object> map){
        StringBuilder sb = new StringBuilder("{");
        Set<Map.Entry<String, Object>> entries = map.entrySet();
        System.out.println(entries);
        for (Map.Entry<String,Object> entry : map.entrySet()) {
            sb.append("\"").append(entry.getKey()).append("\":");
            //原工具中区分了字符串和数字、数字则不加双引号，但不利于统一？按照json格式的标准、数字是不加双引号的，所以必须判断
            if (entry.getValue() instanceof String) {//instanceof 判断数据类型
                sb.append("\"").append(entry.getValue()).append("\"");
            }else {
                sb.append(entry.getValue());
            }
            sb.append(",");
        }
        //最后要删一个逗号，加一个右大括号
        sb.deleteCharAt(sb.length()-1).append("}");
        return sb.toString();
    }

    public static Map<String,Object> parseJson(String json){
        Map<String,Object> map = new HashMap<>();
        String sub = json.substring(1,json.length()-1);
        String[] split = sub.split(",");
        for (int i=0;i<split.length;i++){
            String[] kv = split[i].split(":");
            String key = kv[0].substring(1,kv[0].length()-1);
            //双引号开头是字符串，json格式的规矩
            if (kv[1].startsWith("\"")){
                //字符串去双引号
                map.put(key,kv[1].substring(1,kv[1].length()-1));
            }else {
                //数字直接插入，但要转成数字
                map.put(key,Long.valueOf(kv[1]));
            }
        }
        return map;
    }

    public static boolean secureCompare(String c1, String c2){
        byte [] b1 = c1.getBytes(StandardCharsets.UTF_8);
        byte [] b2 = c2.getBytes(StandardCharsets.UTF_8);
        //若长度不一致，这里就会让result变成1，因为异或是同0异1
        int result = b1.length ^ b2.length;
        for (int i=0;i<Math.min(b1.length,b2.length);i++){
            result = result | b1[i] ^ b2[i];
        }
        return result==0;
    }

}
