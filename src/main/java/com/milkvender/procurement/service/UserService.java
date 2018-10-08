package com.milkvender.procurement.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.milkvender.procurement.entitty.UserEntity;
import com.milkvender.procurement.repository.UserRepository;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private Environment env;

    @Autowired
    private UserRepository userRepository;

    private OkHttpClient okHttpClient;
    private UserService(){
        okHttpClient=new OkHttpClient();
    }

    public UserEntity addNewUser(UserEntity userEntity){
        return userRepository.save(userEntity);
    }

    public boolean isuserPresent(long userID){
        return userRepository.findById(userID).isPresent();
    }

    public boolean verifyOtp(long phoneno,int otp){
        Optional<UserEntity> userEntity = userRepository.findById(phoneno);
        if(userEntity.isPresent()){
            String details = userEntity.get().getDetails();
            JsonParser jsonParser=new JsonParser();
            JsonObject parse = jsonParser.parse(details).getAsJsonObject();
            int localotp = parse.get("otp").getAsInt();
            if(localotp==otp){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }



    public boolean sendOtp(long phonenumber,int otp){
        StringBuilder url=new StringBuilder("http://api.msg91.com/api/sendhttp.php?country=91&sender=JENISO&route=4&authkey="+env.getProperty("msg91shkey"));
        url.append("&mobiles="+phonenumber);
        url.append("&message=Your OTP is "+otp);
        Request.Builder builder = new Request.Builder().url(url.toString());
        Response execute=null;
        try {
            execute = okHttpClient.newCall(builder.build()).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (execute!=null && execute.code()== 200){
            String responseKey=null;
            try {
                if (execute.body() != null) {
                    responseKey= new String(execute.body().bytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(responseKey!=null && responseKey.length()==24){
                System.out.println("resposne "+responseKey);
                return true;
            }
        }
        return false;
    }
}
