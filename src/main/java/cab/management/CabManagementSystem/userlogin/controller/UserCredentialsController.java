package cab.management.CabManagementSystem.userlogin.controller;

import cab.management.CabManagementSystem.userlogin.model.TokenAuthModel;
import cab.management.CabManagementSystem.userlogin.model.UserCredentialsModel;
import cab.management.CabManagementSystem.userlogin.model.UserProfileModel;
import cab.management.CabManagementSystem.userlogin.model.UserRegistrationModel;
import cab.management.CabManagementSystem.userlogin.repository.CredentialsRepository;
import cab.management.CabManagementSystem.userlogin.repository.ProfileRepository;
import cab.management.CabManagementSystem.userlogin.repository.RegisterRepository;
import cab.management.CabManagementSystem.userlogin.repository.TokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/user")
public class UserCredentialsController {

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final Logger logger = LoggerFactory.getLogger(UserCredentialsController.class);

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);


    @Autowired
    RegisterRepository registerRepository;

    @Autowired
    CredentialsRepository credentialsRepository;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    TokenRepository tokenRepository;


    @RequestMapping(value = "/userlogin", method = RequestMethod.POST)
    public ResponseEntity<Object> authenticateUser(@RequestBody UserCredentialsModel userCredentials) {

        try {

            logger.info("Enter Into The User Authentication Function");

            if (userCredentials.getUsername()== null) {
                return new ResponseEntity<>("{\"response\":\"please provide username\"}", HttpStatus.OK);
            }
            if(userCredentials.getPassword()== null){
                return new ResponseEntity<>("{\"response\":\"please provide password\"}", HttpStatus.OK);
            }
            else if(credentialsRepository.findByUsername(userCredentials.getUsername())!=null){
             UserCredentialsModel user = credentialsRepository.findByUsername(userCredentials.getUsername());
             if(user.getPassword().equals(userCredentials.getPassword())){
                 //DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                 Date date = new Date();
                 //System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43
                 user.setLastLogin(date);

                 if(tokenRepository.findByUserId(user.getId())!=null){
                 TokenAuthModel tokenTableData  = tokenRepository.findByUserId(user.getId());

                     tokenTableData.setTokenId(UserCredentialsController.randomAlphaNumeric(10));

                     Date oldDate = new Date();
                     Date newDate = new Date(oldDate.getTime() + 60 * 60 * 1000);

                     tokenTableData.setValidTill(newDate);

                     tokenRepository.save(tokenTableData);
                     credentialsRepository.save(user);
                     logger.info("User Table Login Successfull After Successfully Signed In");
                     return new ResponseEntity<>(tokenTableData, HttpStatus.OK);
                 }
                 else {
                     TokenAuthModel tokenAuthModel = new TokenAuthModel();
                     tokenAuthModel.setUserId(user.getId());

                     tokenAuthModel.setTokenId(UserCredentialsController.randomAlphaNumeric(10));

                     Date oldDate = new Date();
                     Date newDate = new Date(oldDate.getTime() + 60 * 60 * 1000);

                     tokenAuthModel.setValidTill(newDate);

                     tokenRepository.save(tokenAuthModel);

                     credentialsRepository.save(user);
                     logger.info("Token Auth Table Data Is Successfully Saved");
                     logger.info("User Table Login Successfull After Successfully Signed In");
                     return new ResponseEntity<>(tokenAuthModel, HttpStatus.OK);
                 }

             }
             else{
                 return new ResponseEntity<>("{\"response\":\"Incorrrect Password\"}", HttpStatus.OK);
             }
            }
            return new ResponseEntity<>("{\"response\":\"Username Not Found\"}", HttpStatus.OK);

        } catch (Exception ex) {
            logger.info("Error Occur At User Authentication Function");
            return new ResponseEntity<>("{\"response\":\"Error Occured\"}", HttpStatus.BAD_REQUEST);
        }

    }


    // registration of user
    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody UserRegistrationModel userRegistration) {
        logger.info("Enter Into The Register Function At Repositorty");
        try {

            if(credentialsRepository.findByUsername(userRegistration.getUsername())!=null)
            {
                return new ResponseEntity<>("{\"response\":\"Username Already Exist\"}", HttpStatus.OK);
            }
            else if(profileRepository.findByNstId(userRegistration.getNstId())!=null){
                return new ResponseEntity<>("{\"response\":\"NstId Already Exist\"}", HttpStatus.OK);
            }
            else if(fieldValidation(userRegistration)==true) {
                //registerRepository.save(userRegistration);

                // saving in credentials table
                UserCredentialsModel userCredentials = new UserCredentialsModel();
                userCredentials.setUsername(userRegistration.getUsername());
                userCredentials.setPassword(userRegistration.getPassword());
                credentialsRepository.save(userCredentials);
                logger.info("Data Saved In User Credentials Repository");

                // saving data in profile table
                UserProfileModel userProfile = new UserProfileModel();
                UserCredentialsModel userData = credentialsRepository.findByUsername(userRegistration.getUsername());
                userProfile.setUserId(userData.getId());
                userProfile.setNstId(userRegistration.getNstId());
                userProfile.setEmail(userRegistration.getEmail());
                userProfile.setPhoneNumber(userRegistration.getPhoneNumber());
                profileRepository.save(userProfile);
                logger.info("Data Saved In User Profile Repository");


                registerRepository.save(userRegistration);
                logger.info("Data Saved In User Registration Repository");
                return new ResponseEntity<>("{\"response\":\"You are Successfully Registered\"}", HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>("{\"response\":\"Validation Problem. Please Try Again\"}", HttpStatus.OK);
            }
        }
        catch(Exception ex){
            logger.info("Error Occur At User Registration Controller Function" + ex);
            return new ResponseEntity<>("{\"response\":\"Error Occured During Registration. Please Try Again\"}", HttpStatus.BAD_REQUEST);
        }
    }




    public boolean fieldValidation(UserRegistrationModel userRegistrationModel){
        if(userRegistrationModel.getUsername()==""){
            return false;
        }
        if(userRegistrationModel.getNstId()==""){
            return false;
        }
        else if(userRegistrationModel.getPassword()=="" || userRegistrationModel.getPassword().length()<8 ){
            return false;
        }
        else if(userRegistrationModel.getEmail()=="" || validateEmail(userRegistrationModel.getEmail())==false){
            return false;
        }
        else if(userRegistrationModel.getPhoneNumber()=="" || userRegistrationModel.getPhoneNumber().length()!=10 || !(userRegistrationModel.getPhoneNumber().matches("[0-9]+"))){
            return false;
        }
        else {
            return true;
        }
    }

    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }


    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }


 //        if (otp_data.containsKey(requestBodyOTPSystem.getUserEmail())) {
//                OtpSystem otpSystem = otp_data.get(requestBodyOTPSystem.getUserEmail());
//                if (otpSystem != null) {
//                    if (otpSystem.getExpiryTime() >= System.currentTimeMillis()) {
//                        if (requestBodyOTPSystem.getOtp().equals(otpSystem.getOtp())) {
//                            otp_data.remove(requestBodyOTPSystem.getUserEmail());
//                            logger.info("Otp Is Verified Successfully");
//                            return new ResponseEntity<>("{\"response\":\"OTP is verified successfullly\"}", HttpStatus.OK);
//                        }
//
//                        return new ResponseEntity<>("{\"response\":\"Invalid OTP\"}", HttpStatus.BAD_REQUEST);
//                    }
//                    return new ResponseEntity<>("{\"response\":\"Otp Expired\"}", HttpStatus.BAD_REQUEST);
//                }
//                return new ResponseEntity<>("{\"response\":\"something went wrong\"}", HttpStatus.BAD_REQUEST);
//            }
//            return new ResponseEntity<>("{\"response\":\"email id not found\"}", HttpStatus.NOT_FOUND);

}
