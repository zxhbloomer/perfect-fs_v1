package com.perfect.filesystem.myfs.bean.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class InquireFileOutputPojo
{
    // 文件Fileuuid
    private String fileUuid;
    // 文件名
    private String fileName;
    // 文件大小
    private Long fileSize;
    // 保存时间
    private Date createTime;
}
