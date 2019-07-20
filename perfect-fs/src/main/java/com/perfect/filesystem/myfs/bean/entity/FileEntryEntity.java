package com.perfect.filesystem.myfs.bean.entity;

import com.perfect.filesystem.myfs.util.UuidUtil;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zxh
 */
@Entity
@Table(name = "s_file_repository")
public class FileEntryEntity implements Serializable {
    private static final long serialVersionUID = 1794670886372121692L;

    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "file_uuid")
    private String fileUuid;
    @Column(name = "file_name")
    private String fileName;
    @Column(name = "file_size")
    private Long fileSize;
    @Column(name = "relative_path")
    private String relativePath;
    @Column(name = "create_dt")
    private Date createTime;

    public FileEntryEntity() {}

    public FileEntryEntity(String fileName) {
        this.fileName = fileName;

        generateFileUid();
        generateServerPath();
    }

    private void generateFileUid() {
        this.fileUuid = UuidUtil.randomUUID();
    }

    /**
     * 获取保存路径
     */
    private void generateServerPath() {
        if (this.fileUuid == null) {
            return;
        }
        int modulus = getFileIdModulus(this.fileUuid);
        String today = getToday();
        String thisYear = today.substring(0, today.length() - 4);

        StringBuffer resumePath = new StringBuffer();
        resumePath.append(thisYear).append(File.separator).append(today).append(File.separator).append(modulus)
            .append(File.separator).append(this.fileUuid).append(File.separator).append(this.fileName);

        this.relativePath = resumePath.toString();
    }

    private int getFileIdModulus(String fileId) {
        int hashCode = fileId.hashCode();
        int modulus = Math.abs(hashCode % 1000);

        return modulus;
    }

    private String getToday() {
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
        Date today = new Date();
        String result = DATE_FORMAT.format(today);

        return result;
    }

    public String getFileUuid() {
        return this.fileUuid;
    }

    public void setFileUuid(String fileUuid) {
        this.fileUuid = fileUuid;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return this.fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getRelativePath() {
        return this.relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getServerAbsolutePath(String filePath) {
        // String absoluteHostPath = ConfigPropertites.getProperties("upload", "data.path", "d:/tmp/fs/") +
        // File.separator + this.relativePath;
        String absoluteHostPath = filePath + File.separator + this.relativePath;
        // ConfigPropertites.getProperties("upload", "data.path", "d:/tmp/fs/") + File.separator + this.relativePath;

        return absoluteHostPath;
    }

    public String getUri() {
        String uri = getRelativePath().replaceAll("\\\\", "/");
        return uri;
    }

    public boolean isHtmlFile() {
        String ext = getFileNameExtension();
        boolean isHtmlFile =
            ("html".equalsIgnoreCase(ext)) || ("htm".equalsIgnoreCase(ext)) || ("mht".equalsIgnoreCase(ext));

        return isHtmlFile;
    }

    public String getFileNameExtension() {
        if (this.fileName == null) {
            return "";
        }
        int lastDotIndex = this.fileName.lastIndexOf(".");
        if (lastDotIndex <= 0) {
            return "";
        }
        return this.fileName.substring(lastDotIndex + 1);
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();

        result.append(getClass().getName()).append(": fileUid=\"").append(this.fileUuid).append("\"")
            .append(", fileName=\"").append(this.fileName).append("\"").append(", fileSize=").append(this.fileSize)
            .append(", serverPath=\"").append(this.relativePath).append("\"").append(", createTime=")
            .append(this.createTime);

        return result.toString();
    }
}
