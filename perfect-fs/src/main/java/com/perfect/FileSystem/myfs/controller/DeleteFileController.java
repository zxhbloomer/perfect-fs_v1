package com.perfect.FileSystem.myfs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.perfect.filesystem.myfs.properties.PerfectFsProperties;
import com.perfect.filesystem.myfs.service.TransferService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zxh
 */
@RestController
@Slf4j
@Api("删除相关")
@RequestMapping("/api/v1/delete")
public class DeleteFileController {

    @Autowired
    private PerfectFsProperties perfectFsProperties;
    @Autowired
    private TransferService transferService;

    /**
     * 删除指定文件
     * @param
     */
    @ApiOperation(value = "删除指定文件", notes = "删除指定文件")
    @GetMapping(path = "/{fileuuid}")
    public void deleteFile(
        @ApiParam(name = "fileuuid", value = "uuid", required = true) @PathVariable("fileuuid") String fileUuid) {
        if (!perfectFsProperties.isRealDelete()) {
            return;
        }
        transferService.deleteFile(fileUuid);
    }
}
