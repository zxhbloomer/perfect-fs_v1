package com.perfect.filesystem.myfs.service.impl;

import com.perfect.filesystem.myfs.bean.entity.FileEntryEntity;
import com.perfect.filesystem.myfs.bean.entity.FileEntryRepository;
import com.perfect.filesystem.myfs.properties.PerfectFsProperties;
import com.perfect.filesystem.myfs.service.TransferService;
import com.perfect.filesystem.myfs.util.CommonUtil;
import com.perfect.filesystem.myfs.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.regex.Pattern;

/**
 * @author zhangxh
 */
@Service
public class TransferServiceImpl implements TransferService {

    @Autowired
    private FileEntryRepository fileEntryRepository;
    @Autowired
    PerfectFsProperties perfectFsProperties;

    private static final int BUFFER_SIZE_10K = 10240;
    private static final Pattern P_CHARSET = Pattern.compile("(?i)(<[^>]+charset=)([^\\s\"'>]+)");

    private static final String UTF8 = "utf-8";

    /**
     * 通用上传
     * @param multipartFile
     * @return
     */
    @Override
    public FileEntryEntity uploadFile(MultipartFile multipartFile) {
        String originalName = CommonUtil.getFilename(multipartFile);
        FileEntryEntity fileEntryEntity = new FileEntryEntity(originalName);
        try {
            File file = new File(fileEntryEntity.getServerAbsolutePath(perfectFsProperties.getUploadDataPath()));
            File parentDir = file.getParentFile();
            if ((parentDir != null) && (!parentDir.exists())) {
                parentDir.mkdirs();
            }
            multipartFile.transferTo(file);
            fileEntryEntity.setFileSize(Long.valueOf(file.length()));
            insertFileEntryEntity(fileEntryEntity);

//            resetEncode(fileEntryEntity);

        } catch (Exception ignore) {
            throw new RuntimeException(ignore);
        }
        return fileEntryEntity;
    }

    @Transactional(rollbackFor = Exception.class)
    public void insertFileEntryEntity(FileEntryEntity fileEntryEntity) {
        fileEntryEntity.setCreateTime(DateTimeUtil.now());
        fileEntryRepository.save(fileEntryEntity);
    }

    /**
     *
     * @param FileEntryEntity
     */
//    private void resetEncode(FileEntryEntity FileEntryEntity) {
//        if (!FileEntryEntity.isHtmlFile()) {
//            return;
//        }
//        try {
//            File resumeFile = new File(FileEntryEntity.getServerAbsolutePath());
//            byte[] buffer = new byte['?'];
//            FileInputStream fis = new FileInputStream(resumeFile);
//            ByteArrayOutputStream bao = new ByteArrayOutputStream();
//
//            int len = 0;
//            while ((len = fis.read(buffer)) != -1) {
//                bao.write(buffer, 0, len);
//            }
//            bao.flush();
//            byte[] htmlBytes = bao.toByteArray();
//
//            fis.close();
//            bao.close();
//
//            String html = new String(htmlBytes, "utf-8");
//
//            Matcher m = P_CHARSET.matcher(html);
//            if (m.find()) {
//                String charset = m.group(2);
//                if (!"utf-8".equalsIgnoreCase(charset)) {
//                    html = new String(htmlBytes, charset);
//                    html = html.replaceFirst(charset, "utf-8");
//
//                    OutputStreamWriter writer =
//                        new OutputStreamWriter(new FileOutputStream(resumeFile, false), "utf-8");
//                    writer.write(html);
//                    writer.flush();
//                    writer.close();
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 下载
     * @param fileUid
     * @return
     */
    @Override
    public String downloadFile(String fileUid) {
        FileEntryEntity FileEntryEntity = inquireFileEntryEntity(fileUid);

        String uri = FileEntryEntity == null ? null : FileEntryEntity.getUri();
        return uri;
    }

    /**
     * 删除
     * @param fileUid
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteFile(String fileUid) {
        FileEntryEntity fileEntryEntity = inquireFileEntryEntity(fileUid);
        File f = new File(fileEntryEntity.getServerAbsolutePath(perfectFsProperties.getUploadDataPath()));
        f.delete();
//        PersistUtil.delete(FileEntryEntity);
        fileEntryRepository.delete(fileEntryEntity);
    }

    /**
     * 查询
     * @param fileUuid
     * @return
     */
    @Override
    public FileEntryEntity inquireFileEntryEntity(String fileUuid) {
        FileEntryEntity fileEntryEntity = fileEntryRepository.selectByUuid(fileUuid);
        return fileEntryEntity;
    }
}
