package hello.upload.domain;
import lombok.Data;

import java.util.List;

@Data
public class Item {
    private Long id;        //DB에 저장할떄 생기는 값
    private String itemName;
    private UploadFile attachFile;
    private List<UploadFile> imageFiles;        //이미지는 여러개 파일들을 업로드 할 수 있어야함.

}
