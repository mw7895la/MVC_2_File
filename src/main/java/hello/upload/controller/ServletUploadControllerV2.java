package hello.upload.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

@Slf4j
@Controller
@RequestMapping("/servlet/v2")
public class ServletUploadControllerV2 {

    //실제 파일에다가 업로드한 것을 넣어 보자        //@Value("${file.dir}") - application.properites의  속성을 그대로 가져올 수 있다.
    @Value("${file.dir}")
    private String fileDir;     //fildDir 에 C:/study/file/  값이 들어가게 됨.



    @GetMapping("/upload")
    public String newFile() {
        return "upload-form";
    }

    @PostMapping("/upload")
    public String saveFileV2(HttpServletRequest request) throws ServletException, IOException {
        //서블릿으로 하니 HttpServletRequest를 받은 것
        log.info("request = {}", request);

        String itemName = request.getParameter("itemName");
        log.info("itemName = {}", itemName);


        //모든 업로드 파츠들의 컬랙션
        Collection<Part> parts = request.getParts();        //이 부분 파일에 관한것  //여러개의 다른 스타일의 데이터들이다
        log.info("parts={}", parts);

        //폴더에 파일을 실제 보내기 위해 이제 parts를 사용해야 한다.
        for (Part part : parts) {
            log.info("=====PART======");
            log.info("name={}", part.getName());        //part의 이름
            Collection<String> headerNames = part.getHeaderNames(); //parts도 헤더와 바디로 구분이 된다. parts각각의 헤더들의 값을 출력해보자.

            for (String headerName : headerNames) {
                log.info("header {} : {}", headerName, part.getHeader(headerName));
            }

            //편의 메소드
            //content-disposition; filename
            log.info("submittedFilename={}", part.getSubmittedFileName());
            log.info("size ={}", part.getSize());

            //데이터 읽기
            //메시지 바디에 있는 데이터 하나하나 읽기
            InputStream inputStream = part.getInputStream();
            String body = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);//스프링이 제공하는 유틸 사용,  바이너리 데이터를 문자로 문자를 바이너리 데이터로 바꿀땐 Characterset 정의해줘야 함.
            //받아온 바이너리 부분을 String으로 변환
            log.info("body={}", body);//body를 보면  내가 입력한 itemName = string 이 찍히고  PNG는 다 찍힐것.


            //파일에 저장하기
            if (StringUtils.hasText(part.getSubmittedFileName())) {
                String fullPath = fileDir + part.getSubmittedFileName();
                log.info("파일 저장 fullPath={}", fullPath);
                part.write(fullPath);    ////해당경로에 저장
                // A convenience method to write an uploaded part to disk.
                // They just want to write the uploaded part to a file. This method is not guaranteed to succeed if called more than once for the same part
            }
            inputStream.close();    //자원을 반환한다. 그러면 이제 GC가 처리해준다.
        }

        return "upload-form";
    }
}
