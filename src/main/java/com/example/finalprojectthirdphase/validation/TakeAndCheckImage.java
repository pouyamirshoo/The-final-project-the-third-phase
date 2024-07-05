package com.example.finalprojectthirdphase.validation;


import com.example.finalprojectthirdphase.exception.WrongImageInputException;
import org.springframework.stereotype.Component;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

@Component
public class TakeAndCheckImage {
    public byte[] expertImage(String path) {
        byte[] image = new byte[0];
        File inputImage = new File(path);
        String mimetype = new MimetypesFileTypeMap().getContentType(inputImage);
        String type = mimetype.split("/")[0];
        if (type.equals("image")) {
            try {
                image = Files.readAllBytes(inputImage.toPath());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            return image;
        } else
            throw new WrongImageInputException("this file is not jpg");
    }

    public void saveExpertImageToHDD(byte[] expertImage, String firstName, String lastName) {
        String path = "F:\\Maktab\\FinalProjectFirstPhase\\src\\main\\java\\images\\save from dataBase\\" + firstName + " " + lastName + ".jpg";
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(expertImage);
        } catch (IOException e) {
            throw new WrongImageInputException("can not save image");
        }
    }
}
