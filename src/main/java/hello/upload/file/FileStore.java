package hello.upload.file;


import hello.upload.domain.UploadFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileStore {

    @Value("${file.dir}")
    private String fileDir;

    public String getFullPath(String filename) {
        //filename을 받아서 fullpath를 만들어 반환하자
        return fileDir+filename;
    }

    //여러개 업로드 하는 경우
    public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<UploadFile> storeFileResult = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                //multipartFile이 비어있지 않으면,

                UploadFile uploadFile = storeFile(multipartFile);//밑에 하나 업로드 하는 메소드

                storeFileResult.add(uploadFile);
            }
        }
        return storeFileResult;
    }

    //하나를 업로드 하는 경우
    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {
        //스프링이 제공하는 MultipartFile 을 받아서 UploadFile로 바꿔주는 작업.

        if (multipartFile.isEmpty()) {
            return null;
        }
        String originalFilename = multipartFile.getOriginalFilename();

        //오리지날 파일 이름을 가지고 image.png
        //그럼 서버에 저장하는 파일명은 어떤식으로 할꺼냐 uuid로 하되 확장명은 살려주자.
        String uuid = UUID.randomUUID().toString();
        //qwer-wetv-adiv-1234-qw32.png

        //확장명 .png 또는 .txt 같은 것들을 꺼내보자
        String ext = extracted(originalFilename);

        String storeFileName = uuid + "." + ext;        //서버 내부에서 관리하는 파일명은 유일한 이름을 생성하는 UUID를 사용한다.

        multipartFile.transferTo(new File(getFullPath(storeFileName)));      //PathName으로 파일 인스턴스를 만들어주는 File

        return new UploadFile(originalFilename, storeFileName);
    }



    private static String extracted(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");        //뒤에서 부터 해당 . 을 찾아서 index를 반환.
        return originalFilename.substring(pos + 1);        // 한칸 +1 해줘서 png만 짤라 가져옴

    }


}
