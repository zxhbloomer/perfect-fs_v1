package com.perfect.filesystem.myfs.pojo;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@Data
public class UploadFileActionOutputPojo
{
    /**
     * 文件ID
     */
    private String fileUuid;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 文件大小(B)
     */
    private Long fileSize;

//    public String toString()
//    {
//        StringBuffer result = new StringBuffer().append(getClass().getName()).append(": fileUuid = '").append(this.fileUuid).append("'").append(", fileName = '").append(this.fileName).append("'").append(", fileSize = ").append(this.fileSize).append("'");
//
//        return result.toString();
//    }
}
