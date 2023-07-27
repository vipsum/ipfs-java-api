package com.example.ipfsdemo2;


import java.io.IOException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@ControllerAdvice
@RestController
@AllArgsConstructor
@RequestMapping("/files")
public class IPFSController {

   private final FileServiceImpl ipfsService;


   @PostMapping("/upload")
   public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
      return ipfsService.saveFile(file);
   }

   @GetMapping( "/{hash}")
   public ResponseEntity<byte[]> getFile(@PathVariable("hash") String hash) {
      HttpHeaders headers = new HttpHeaders();
      headers.set("Content-type", MediaType.ALL_VALUE);
      byte[] bytes = ipfsService.loadFile(hash);
      return ResponseEntity.status(HttpStatus.OK).headers(headers).body(bytes);

   }

}