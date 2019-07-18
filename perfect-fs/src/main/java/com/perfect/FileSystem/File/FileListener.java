package com.perfect.FileSystem.File;

public interface FileListener {
	
	UploadResult Store(UploadFileExt ufe);
	void Download(String fileKeyorName);
	
}
