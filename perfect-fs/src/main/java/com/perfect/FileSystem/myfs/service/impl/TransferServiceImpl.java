package com.perfect.filesystem.myfs.service.impl;

import com.perfect.filesystem.Entity.AppRepository;
import com.perfect.filesystem.myfs.entity.FileEntryEntity;
import com.perfect.filesystem.myfs.entity.FileEntryRepository;
import com.perfect.filesystem.myfs.service.TransferService;
import com.perfect.filesystem.myfs.util.DateTimeUtil;
import com.perfect.filesystem.myfs.util.StringUtil;
import com.perfect.filesystem.myfs.util.UuidUtil;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferServiceImpl implements TransferService {

    @Autowired
    private FileEntryRepository fileEntryRepository;

    private static final int BUFFER_SIZE_10K = 10240;
    private static final Pattern P_CHARSET = Pattern.compile("(?i)(<[^>]+charset=)([^\\s\"'>]+)");

    private static final String UTF8 = "utf-8";

    @Override
    public FileEntryEntity uploadFile(String fileName, InputStream fileUploaded) {
        FileEntryEntity FileEntryEntity = new FileEntryEntity(fileName);
        try {
            File file = new File(FileEntryEntity.getServerAbsolutePath());

            File parentDir = file.getParentFile();
            if ((parentDir != null) && (!parentDir.exists())) {
                parentDir.mkdirs();
            }

            FileOutputStream output = new FileOutputStream(file);
            byte[] buf = new byte['?'];
            int len = 0;
            while ((len = fileUploaded.read(buf)) != -1) {
                output.write(buf, 0, len);
            }
            fileUploaded.close();
            output.close();

            FileEntryEntity.setFileSize(Long.valueOf(file.length()));

            insertFileEntryEntity(FileEntryEntity);

            resetEncode(FileEntryEntity);
        } catch (Exception ignore) {
            throw new RuntimeException(ignore);
        }
        return FileEntryEntity;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public FileEntryEntity uploadFile(String fileName, String url) {
        if (StringUtil.isBlank(fileName)) {
            if (url.contains("/")) {
                int index = url.lastIndexOf('/') + 1;
                fileName = url.substring(index);
            } else if (url.contains("\\")) {
                int index = url.lastIndexOf('\\') + 1;
                fileName = url.substring(index);
            }
        }

        FileEntryEntity entity = new FileEntryEntity();
        entity.setFileUuid(UuidUtil.randomUUID());
        entity.setFileSize(Long.valueOf(0L));
        entity.setFileName(fileName);
        entity.setRelativePath(url);
        entity.setCreateTime(DateTimeUtil.now());
        fileEntryRepository.save(entity);
//        PersistUtil.insert(entity);
        return entity;
    }

    @Transactional(rollbackFor = Exception.class)
    public void insertFileEntryEntity(FileEntryEntity fileEntryEntity) {
        fileEntryEntity.setCreateTime(DateTimeUtil.now());
//        PersistUtil.insert(FileEntryEntity);
        fileEntryRepository.save(fileEntryEntity);
    }

    private void resetEncode(FileEntryEntity FileEntryEntity) {
        if (!FileEntryEntity.isHtmlFile()) {
            return;
        }
        try {
            File resumeFile = new File(FileEntryEntity.getServerAbsolutePath());
            byte[] buffer = new byte['?'];
            FileInputStream fis = new FileInputStream(resumeFile);
            ByteArrayOutputStream bao = new ByteArrayOutputStream();

            int len = 0;
            while ((len = fis.read(buffer)) != -1) {
                bao.write(buffer, 0, len);
            }
            bao.flush();
            byte[] htmlBytes = bao.toByteArray();

            fis.close();
            bao.close();

            String html = new String(htmlBytes, "utf-8");

            Matcher m = P_CHARSET.matcher(html);
            if (m.find()) {
                String charset = m.group(2);
                if (!"utf-8".equalsIgnoreCase(charset)) {
                    html = new String(htmlBytes, charset);
                    html = html.replaceFirst(charset, "utf-8");

                    OutputStreamWriter writer =
                        new OutputStreamWriter(new FileOutputStream(resumeFile, false), "utf-8");
                    writer.write(html);
                    writer.flush();
                    writer.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
        File f = new File(fileEntryEntity.getServerAbsolutePath());
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
