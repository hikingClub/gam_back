package com.gam.hikingclub.util;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class VerificationStore {
    private static final Map<String, VerificationInfo> verificationCodes = new ConcurrentHashMap<>();
    private static final Set<String> verifiedEmails = new ConcurrentSkipListSet<>();

    public static void addCode(String email, String code, long expiryTime) {
        verificationCodes.put(email, new VerificationInfo(code, System.currentTimeMillis() + expiryTime));
    }

    public static VerificationInfo getCode(String email) {
        VerificationInfo info = verificationCodes.get(email);
        if (info != null && info.getExpiryTime() > System.currentTimeMillis()) {
            return info;
        }
        return null;
    }

    public static void removeCode(String email) {
        verificationCodes.remove(email);
    }

    public static void addVerifiedEmail(String email) {
        verifiedEmails.add(email);
    }

    public static boolean isEmailVerified(String email) {
        return verifiedEmails.contains(email);
    }

    public static class VerificationInfo {
        private final String code;
        private final long expiryTime;

        public VerificationInfo(String code, long expiryTime) {
            this.code = code;
            this.expiryTime = expiryTime;
        }

        public String getCode() {
            return code;
        }

        public long getExpiryTime() {
            return expiryTime;
        }
    }
}
