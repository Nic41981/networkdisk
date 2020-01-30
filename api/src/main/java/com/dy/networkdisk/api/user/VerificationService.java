package com.dy.networkdisk.api.user;

import java.awt.image.BufferedImage;

public interface VerificationService {
    byte[] getVerificationCode(String token);
}
