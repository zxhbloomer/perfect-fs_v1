package com.perfect.filesystem.myfs.controller;

import com.perfect.filesystem.myfs.entity.FileEntryEntity;
import com.perfect.filesystem.myfs.pojo.UploadFileActionOutputPojo;
import com.perfect.filesystem.myfs.service.TransferService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author zxh
 */
@Api("上传文件接口")
@Controller
public class UploadFileOfLinkController {
    @Autowired
    private TransferService transferService;

    @ApiOperation(value = "文件上传", notes = "上传文件：外部链接")
    @GetMapping(path = "/")
    public UploadFileActionOutputPojo uploadFileOfLink(
        @ApiParam(name = "fileName", value = "文件名", required = true) @RequestParam("fileName") String fileName,
        @ApiParam(name = "url", value = "文件连接地址", required = true) @RequestParam("fileName") String url) {
        FileEntryEntity entity = this.transferService.uploadFile(fileName, url);

        UploadFileActionOutputPojo output = new UploadFileActionOutputPojo();
        output.setFileUuid(entity.getFileUuid());
        output.setFileName(entity.getFileName());
        output.setFileSize(entity.getFileSize());
        return output;
    }
}
