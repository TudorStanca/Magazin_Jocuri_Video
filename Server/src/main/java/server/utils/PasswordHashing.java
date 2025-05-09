package server.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class PasswordHashing {

    public static byte[] generateSalt(){
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return bytes;
    }

    public static byte[] generateHash(String password, byte[] salt) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.reset();
            digest.update(salt);
            return digest.digest(password.getBytes());
        }
        catch(NoSuchAlgorithmException e){
            throw new RuntimeException(e);
        }
    }

    public static boolean isExpectedPassword(String password, byte[] hash, byte[] salt){
        byte[] array = generateHash(password, salt);
        return Arrays.equals(hash, array);
    }
}
