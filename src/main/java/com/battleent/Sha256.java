package com.battleent;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

/**
 * encrypt input data based on SHA-256
 */
public class Sha256 {
    public static String hash(String input) {
        // get SHA-256 from MessageDigest
        try {
            MessageDigest mDigest = MessageDigest.getInstance("SHA-256");

            //convert the byte to hex format method 1
            byte hash[] = mDigest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder(); // hexidecimal
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (UnsupportedEncodingException e) {
            System.err.println("UnsupportedEncodingException : " + e.getMessage());
            return null;
        } catch (java.security.NoSuchAlgorithmException e) {
            System.err.println("NoSuchAlgorithmException : " + e.getMessage());
            return null;
        } catch (Exception e) {
            throw new RuntimeException("No such algorithm SHA-256!");
        }
    }
}