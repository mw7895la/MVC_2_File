package hello.upload.controller;

import hello.upload.domain.Item;
import hello.upload.domain.ItemRepository;
import hello.upload.domain.UploadFile;
import hello.upload.file.FileStore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemRepository itemRepository;
    private final FileStore fileStore;

    @GetMapping("/items/new")
    public String newItem(@ModelAttribute ItemForm form) {
        return "item-form";
    }

    @PostMapping("/items/new")
    public String saveItem(@ModelAttribute ItemForm form, RedirectAttributes redirectAttributes) throws IOException {
        /*MultipartFile attachFile = form.getAttachFile();
        UploadFile uploadFile = fileStore.storeFile(attachFile);    을 아래 한줄로 */
        UploadFile attachFile = fileStore.storeFile(form.getAttachFile());


        /*List<MultipartFile> imageFiles = form.getImageFiles();
        List<UploadFile> uploadFiles = fileStore.storeFiles(imageFiles); 을 아래 한줄로 */
        List<UploadFile> storeImageFiles = fileStore.storeFiles(form.getImageFiles());

        //파일은 데이터베이스에 저장하지 않고 보통 스토리지에 저장한다. 데이터베이스에는 파일자체보다는, 경로를 저장한다.

        //데이터베이스에 저장
        Item item = new Item();
        item.setItemName(form.getItemName());
        item.setAttachFile(attachFile);
        item.setImageFiles(storeImageFiles);
        itemRepository.save(item);

        redirectAttributes.addAttribute("itemId", item.getId());
        return "redirect:/items/{itemId}";
    }

    //보여주는 기능
    @GetMapping("/items/{id}")
    public String items(@PathVariable Long id, Model model) {

        Item item = itemRepository.findById(id);
        model.addAttribute("item", item);
        return "item-view";
    }

    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {

        //실제 모든 파일이 어떻게 조합되냐면  "file:/Users/../b6a9be70-3bcb-402e-9c2d-6d528bd14289.PNG  이렇게 된다.
        //new UrlResource()  -  Create a new UrlResource based on a URL path.
        return new UrlResource("file:" + fileStore.getFullPath(filename));
        //item-view.html에서 src로 부터 받은 UUID filename을 받고 전체경로로 만들어서
        //위의 경로에 있는 파일에 접근해서 그 파일을 스트림으로 반환하게 된다.
        //그래서 th:src 부분의 경로를 여기로 넘겨와서 접근후 스트림으로 반환하면서 타임리프에서 이미지를 출력한 것

        //Resource를 리턴할 때 HttpMessageConverter를 구현한  ResourceHttpMessageConverter가 해당 리소스의 바이트 정보를 응답 바디에 담아줍니다.
        //@ResponseBody 가 있으니 HttpMessageConverter가 컨트롤러에서 어댑터로 다시 돌아갈 때 동작을 할 것.
    }

    //첨부파일명 클릭시 상품Id로 들어옴
    //item을 접근할 수 있는 사용자만 이 파일을 다운로드 받을 수 있따.
    @GetMapping("/attach/{itemId}")
    public ResponseEntity<Resource> downloadAttach(@PathVariable Long itemId) throws MalformedURLException {
        Item item = itemRepository.findById(itemId);
        String storeFileName = item.getAttachFile().getStoreFileName();
        String uploadFileName = item.getAttachFile().getUploadFileName();   //왜 업로드 파일 이름이 필요하냐면, 사용자가 다운을 받을 때,실제 내가 업로드한 파일명이 나와야 겠지.

        UrlResource resource = new UrlResource("file:" + fileStore.getFullPath(storeFileName));

        log.info("uploadFileName = {}", uploadFileName);

        /*return ResponseEntity.ok()
                .body(resource);*/    //이렇게 하면 파일에 적혀있는 내용만 보인다.업로드가 안된다.

        //한글이 깨질 때 사용해라 UriUtils가 수많은 인코딩 기능을 지원한다.
        //String encodedUploadFileName = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8);

        //첨부파일명을 클릭하여 다운받으면  response header에  Content-Disposition: attachment; filename="t.txt"가 있다. 즉, 해당 resource를 첨부파일이구나 하고 인식해서 다운로드를 받을 수 있던것이다.
        String contentDisposition ="attachment; filename=\""+uploadFileName+"\"";       //쌍 따옴표로 감싼것.
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }
    //스프링 컨트롤러에서 @ResponseBody 일반적인 객체를 반환하면 json 형태로 반환되는 것이 맞습니다. 그런데 @ResponseBody + byte[]또는, Resource를 반환하는 경우 바이트 정보가 반환됩니다.
    //<img> 에서는 이 바이트 정보를 읽어서 이미지로 반환하게 됩니다.
}
