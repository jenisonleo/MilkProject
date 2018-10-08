package com.milkvender.procurement.controller;

import com.google.gson.JsonObject;
import com.milkvender.procurement.entitty.UserEntity;
import com.milkvender.procurement.service.UserService;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public ResponseEntity<String> ping(){
        return new ResponseEntity<String>("pong", HttpStatus.OK);
    }


    @RequestMapping(value = "/requestlogin", method = RequestMethod.GET)
    public ResponseEntity<String> requestLogin(@RequestParam(value = "phoneno")Long phno){
        //// todo check if phonenumber is of size 10
        Random random=new Random();
        int otp = random.nextInt(9999);
        boolean isSuccess = userService.sendOtp(phno, otp);
        if(isSuccess){
            JsonObject jsonObject=new JsonObject();
            JsonObject keyobject=new JsonObject();
            keyobject.addProperty("otp",otp);
            jsonObject.add("success",keyobject);
            UserEntity entity=new UserEntity();
            entity.setUserID(phno);
            entity.setDetails(keyobject.toString());
            userService.addNewUser(entity);
            return new ResponseEntity<String>(jsonObject.toString(), HttpStatus.OK);
        }else {
            return new ResponseEntity<String>("some error has occured while sending otp", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/verifyotp", method = RequestMethod.POST)
    public ResponseEntity<String> verifyotp(@RequestParam(value = "phonrno")Long phno,@RequestParam(value = "otp")Integer otp){

        boolean isuserpres = userService.isuserPresent(phno);
        if(isuserpres){
            boolean iscorrect = userService.verifyOtp(phno, otp);
            if(iscorrect){
                return new ResponseEntity<String>("otp verified", HttpStatus.OK);
            }else {
                return new ResponseEntity<String>("invalid otp", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else {
            return new ResponseEntity<String>("user not registered", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
