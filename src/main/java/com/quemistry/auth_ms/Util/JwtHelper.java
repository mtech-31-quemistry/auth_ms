package com.quemistry.auth_ms.Util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;

public class JwtHelper {
    private static final String EMAIL = "email";
    private static final String USERID = "sub";
    private static final String USER_GROUP ="cognito:groups";

    private final Map<String, Object> KeyValue;
    public JwtHelper(String jwtString){
        this.KeyValue = JwtKeyValue(jwtString);
    }

    public Boolean getValid(){
        return this.KeyValue != null;
    }

    public String getEmail(){
        return (String)this.KeyValue.get(JwtHelper.EMAIL);
    }

    public String getUserId(){
        return (String)this.KeyValue.get(JwtHelper.USERID);
    }

    public String[] getUserGroup(){
        var useRoles = (ArrayList<Object>)this.KeyValue.get(JwtHelper.USER_GROUP);
        if(useRoles != null) {
            return useRoles.toArray(new String[useRoles.size()]);
        }
        return new String[0];
    }
    private static Map<String, Object> JwtKeyValue(String jwtString) {
        String[] chunks = (jwtString == null) ? null : jwtString.split("\\.");
        if (chunks == null || chunks.length < 3) {
            return null;
        }
        try {
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]));
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(payload, new TypeReference<>() {});
        } catch (Exception ex) {
            return null;
        }
    }
}
