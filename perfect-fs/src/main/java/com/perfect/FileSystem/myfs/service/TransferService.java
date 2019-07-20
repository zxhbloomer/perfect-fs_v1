package com.perfect.filesystem.myfs.service;

import com.perfect.filesystem.myfs.entity.FileEntryEntity;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author zhangxh
 */
public abstract interface TransferService
{
  public abstract FileEntryEntity uploadFile(MultipartFile multipartFile);

  public abstract String downloadFile(String paramString);

  public abstract void deleteFile(String paramString);

  public abstract FileEntryEntity inquireFileEntryEntity(String paramString);
}
