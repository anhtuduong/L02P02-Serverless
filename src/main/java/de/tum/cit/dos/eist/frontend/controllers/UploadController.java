package de.tum.cit.dos.eist.frontend.controllers;

import java.io.File;

import de.tum.cit.dos.eist.frontend.infrastructure.ImageUploader;

public class UploadController {
    private static UploadController instance;

    private UploadController() {
    }

    public static UploadController getInstance() {
        if (instance == null) {
            instance = new UploadController();
        }
        return instance;
    }

    public void uploadImage(File file) throws Exception {
        ImageUploader imageUploader = new ImageUploader();
        imageUploader.uploadImage(file, "unblurred_images/student.jpg");
    }
}
