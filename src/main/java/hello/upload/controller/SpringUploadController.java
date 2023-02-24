package hello.upload.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

@Slf4j
@Controller
@RequestMapping("/spring")
public class SpringUploadController {
    //실제 파일에다가 업로드한 것을 넣어 보자        //@Value("${file.dir}") - application.properites의  속성을 그대로 가져올 수 있다.
    @Value("${file.dir}")
    private String fileDir;     //fildDir 에 C:/study/file/  값이 들어가게 됨.


    @GetMapping("/upload")
    public String newFile() {
        return "upload-form";
    }

    @PostMapping("/upload")     //itemName , file  ( 둘다 upload-form.html에서 name 지정이 여기랑 똑같이 되어있으니까 따로 정해주지 않았음 )
    public String saveFile(@RequestParam String itemName,
                           @RequestParam MultipartFile file, HttpServletRequest request) throws IOException {
        log.info("request ={}", request);
        log.info("itemNAme ={}", itemName);
        log.info("multipartFile={}", file);

        if (!file.isEmpty()) {      //MultipartFile 에 구현된 메소드
            String fullPath = fileDir + file.getOriginalFilename();
            log.info("파일 저장 fullPath = {}", fullPath);

            //File
            file.transferTo(new File(fullPath));
            //File의 여러 생성자 중 path에 관한 것.
            //Creates a new File instance by converting the given pathname string into an abstract pathname. If the given string is the empty string, then the result is the empty abstract pathname.

        }
        return "redirect:/spring/upload";

    }
}
