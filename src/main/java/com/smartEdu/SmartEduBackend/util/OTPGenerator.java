package com.smartEdu.SmartEduBackend.util;

import java.security.SecureRandom;

public class OTPGenerator {

    private static final SecureRandom secureRandom = new SecureRandom(); // More secure than Random
    private static final int OTP_LENGTH = 6; // Length of the OTP

    public static String generateOTP() {
        // Generate a random 6-digit number
        int otp = secureRandom.nextInt((int) Math.pow(10, OTP_LENGTH)); // Random number between 0 and 999999

        // Format the number to ensure it always has 6 digits (pads with leading zeroes if necessary)
        return String.format("%06d", otp);
    }
}
