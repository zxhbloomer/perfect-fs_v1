package com.perfect.FileSystem.Utils;

import com.perfect.FileSystem.Propert.StorageProperties;
import org.springframework.beans.factory.annotation.Autowired;

public class DebugUtil {
	
	@Autowired
	StorageProperties prop;

	public DebugUtil() {
		// TODO Auto-generated constructor stub
	}

	public static void debug(String str){
			System.out.println(str);
	}
}
