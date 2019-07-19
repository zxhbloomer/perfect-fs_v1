package com.perfect.filesystem.myfs.controller;

import com.perfect.filesystem.myfs.entity.FileEntryEntity;
import com.perfect.filesystem.myfs.pojo.InquireFileOutputPojo;
import com.perfect.filesystem.myfs.service.TransferService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zxh
 */
@RestController
@Slf4j
@Api("文件查询")
public class InquireFileAction {
    @Autowired
    TransferService transferService;

    @ApiOperation(value = "文件查询", notes = "查看指定文件信息")
    @GetMapping(path = "/")
    public InquireFileOutputPojo inquireFile(InquireFileOutputPojo input) {
        FileEntryEntity entity = transferService.inquireFileEntryEntity(input.getFileUuid());
        if (entity == null) {
            return null;
        }
        InquireFileOutputPojo output = new InquireFileOutputPojo();
        output.setCreateTime(entity.getCreateTime());
        output.setFileName(entity.getFileName());
        output.setFileSize(entity.getFileSize());
        output.setFileUuid(entity.getFileUuid());
        return output;
    }
}
