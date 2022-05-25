package com.xzkj.reggie.controller;

import com.xzkj.reggie.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        // 原始文件名 abc.jpg
        String originalFilename = file.getOriginalFilename();
        // 后缀名 .jpg
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        // UUID.jpg
        String filename = UUID.randomUUID().toString() + suffix;

        // 目录不存在就创建
        File dir = new File(basePath);
        if(!dir.exists()){
            dir.mkdirs();
        }

        try {
            file.transferTo(new File(basePath + filename));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.success(filename);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try {
            // 通过输入流读取文件内容
            FileInputStream fis = new FileInputStream(new File(basePath + name));

            // 通过输出流将文件写回浏览器
            ServletOutputStream sos = response.getOutputStream();

            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while((len = fis.read(bytes)) > 0){
                sos.write(bytes, 0, len);
                sos.flush();
            }

            // 关闭资源
            fis.close();
            sos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
