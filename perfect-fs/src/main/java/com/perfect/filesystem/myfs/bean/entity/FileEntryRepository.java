package com.perfect.filesystem.myfs.bean.entity;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


/**
 * @author zxh
 */

public interface FileEntryRepository extends CrudRepository<FileEntryEntity, Integer> {

    @Query(value = "select from FS_FILE_ENTRY where FILE_UUID=?1 ", nativeQuery = true)
    @Modifying
    public FileEntryEntity selectByUuid(String uuid);
}
