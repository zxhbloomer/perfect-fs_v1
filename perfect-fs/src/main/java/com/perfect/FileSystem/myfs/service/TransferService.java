package com.perfect.filesystem.myfs.service;

import com.perfect.filesystem.myfs.entity.FileEntryEntity;
import org.springframework.stereotype.Service;

import java.io.InputStream;


public abstract interface TransferService
{
  public abstract FileEntryEntity uploadFile(String paramString, InputStream paramInputStream);

  public abstract FileEntryEntity uploadFile(String paramString1, String paramString2);

  public abstract String downloadFile(String paramString);

  public abstract void deleteFile(String paramString);

  public abstract FileEntryEntity inquireFileEntryEntity(String paramString);
}
