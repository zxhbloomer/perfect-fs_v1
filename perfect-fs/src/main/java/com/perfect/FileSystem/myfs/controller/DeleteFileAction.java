package com.perfect.filesystem.myfs.controller;

import com.perfect.filesystem.myfs.pojo.DeleteFileActionInputPojo;
import com.perfect.filesystem.myfs.properties.PerfectFsProperties;
import com.perfect.filesystem.myfs.service.TransferService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.awt.ComponentFactory;

/**
 * @author zxh
 */
@RestController
@Slf4j
@Api("删除相关")
public class DeleteFileAction {

    @Autowired
    private PerfectFsProperties perfectFsProperties;
    @Autowired
    private TransferService transferService;

    /**
     * 删除指定文件
     * @param input
     */
    @ApiOperation(value = "删除指定文件", notes = "删除指定文件")
    @GetMapping(path = "/")
    public void deleteFile(DeleteFileActionInputPojo input) {
        if (!perfectFsProperties.isRealDelete()) {
            return;
        }
        transferService.deleteFile(input.getFileUuid());
    }
}
