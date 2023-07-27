package com.example.ipfsdemo2;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
//   String saveFile(String filePath);
   String saveFile(MultipartFile file) throws IOException;

   byte[] loadFile(String hash);
}
