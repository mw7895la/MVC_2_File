package hello.upload.domain;

import lombok.Data;

@Data
public class UploadFile {
    private String uploadFileName;      //고객이 저장한 파일       //고객이 a.png라는 파일을 업로드
    private String storeFileName;       //실제 우리 디렉토리 안에 저장되어있는 파일명      //51041c62-86e4-4274-801d-614a7d994edb.png 처럼 저장한다.
    //왜 이렇게 구분하냐, 사용자 A와 B가 같이 image.png 같은 파일명을 업로드 하면  같은 파일이름으로하면 파일이 덮어지겠지 그래서 안겹치게 만들어야돼
    //ex) image_1234521421.png image_1234123522.png UUID나 이런걸로 안겹치게 해야돼.
    public UploadFile(String uploadFileName, String storeFileName) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
    }

}
