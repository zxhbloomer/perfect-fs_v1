package com.perfect.FileSystem;

import com.perfect.FileSystem.Cache.UsesCache;
import com.perfect.FileSystem.File.StoreSource;
import com.perfect.FileSystem.Propert.StorageProperties;
import com.perfect.FileSystem.Service.FastdfsServcice;
import com.perfect.FileSystem.Service.MongoService;
import com.perfect.FileSystem.Service.QiniuService;
import com.perfect.FileSystem.Service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.thymeleaf.dialect.springdata.SpringDataDialect;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(StorageProperties.class)
public class FileSystemApplication {
	
	@Autowired
	private StorageProperties prop;
	
	@Autowired
	QiniuService qiniuService;
	
//	@Autowired
//	AliService aliService;
	
	@Autowired
	FastdfsServcice fastdfsServcice;
	
	@Autowired
	MongoService mongoService;
	
//	@Autowired
//	SeaweedfsService seaweedfsService;
	
	public static void main(String[] args) {
		SpringApplication.run(FileSystemApplication.class, args);
	}
	
	@Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {
            //storageService.deleteAll();
            storageService.init();
            
            initCache();
            
            registerStoreSource();
        };
    }
	
	@Bean
    public SpringDataDialect springDataDialect() {
        return new SpringDataDialect();
    }

	public void registerStoreSource() {
		if (prop.isToqiniu()){
			StoreSource.RegisterListensers(qiniuService);
		}
		
		if (prop.isToalioss()){
//			StoreSource.RegisterListensers(aliService);
		}
		
		if (prop.isTofastdfs()){
			StoreSource.RegisterListensers(fastdfsServcice);
		}
		
		if (prop.isTomongodb()){
			StoreSource.RegisterListensers(mongoService);
		}		
		
		if (prop.isToseaweedfs()){
//			StoreSource.RegisterListensers(seaweedfsService);
		}
	}

	public void initCache() {
		//TODO  cache test
		UsesCache.files = 1000000;
		UsesCache.groups = 1000;
		UsesCache.usedspace = 1000000000;
	}
	
	
}


