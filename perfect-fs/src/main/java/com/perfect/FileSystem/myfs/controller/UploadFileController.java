package com.perfect.filesystem.myfs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.perfect.filesystem.myfs.bean.JSONResult;
import com.perfect.filesystem.myfs.bean.entity.FileEntryEntity;
import com.perfect.filesystem.myfs.bean.pojo.UploadFileResultPojo;
import com.perfect.filesystem.myfs.service.TransferService;
import com.perfect.filesystem.myfs.util.CommonUtil;
import com.perfect.filesystem.myfs.util.ResultUtil;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zxh
 */
@RestController
@Slf4j
@Api("上传文件")
@RequestMapping("/api/v1/upload")
public class UploadFileController {
    private static final long serialVersionUID = 9030382499844843036L;
    @Autowired
    private TransferService fileService;

    /**
     * 通用上传请求
     */
    @PostMapping("/")
    @ResponseBody
    public ResponseEntity<JSONResult<UploadFileResultPojo>> uploadFile(@RequestParam("file") MultipartFile file)
        throws Exception {
        String originalName = CommonUtil.getFilename(file);

        FileEntryEntity fileEntry = fileService.uploadFile(file);

        UploadFileResultPojo uploadFileResultPojo = new UploadFileResultPojo();

        uploadFileResultPojo.setFileName(fileEntry.getFileName());
        uploadFileResultPojo.setFileSize(fileEntry.getFileSize());
        uploadFileResultPojo.setFileUuid(fileEntry.getFileUuid());
        uploadFileResultPojo.setUrl(fileEntry.getUri());

        return ResponseEntity.ok().body(ResultUtil.success(uploadFileResultPojo));

    }

}
