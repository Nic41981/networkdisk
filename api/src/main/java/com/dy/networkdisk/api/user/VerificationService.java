package com.dy.networkdisk.api.user;

import java.awt.image.BufferedImage;

public interface VerificationService {
    void storageAnswer(String token,String answer);
    boolean checkVerification(String token,String code);
}
