package com.perfect.FileSystem.Service;


import com.perfect.FileSystem.File.FileListener;
import com.perfect.FileSystem.File.UploadFileExt;
import com.perfect.FileSystem.File.UploadResult;
import org.springframework.stereotype.Service;

@Service
public class FastdfsServcice implements FileListener {

	public void store(String filePath, String finalFilename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public UploadResult Store(UploadFileExt ufe) {
		//TODO 上传回调
		return null;
	}

	@Override
	public void Download(String fileKeyorName) {
		// TODO 下载
		
	}

}
