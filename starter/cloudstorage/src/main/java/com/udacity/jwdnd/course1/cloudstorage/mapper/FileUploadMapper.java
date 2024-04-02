package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.entity.File;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface FileUploadMapper {

    @Select("SELECT * FROM FILES WHERE userId = #{userId}")
    List<File> getAllFilesByUserId(Integer userId);

    @Insert("INSERT INTO FILES (fileName, contentType, fileSize, fileData, userId) " +
            "VALUES (#{file.fileName}, #{file.contentType}, #{file.fileSize}, #{file.fileData}, #{userId})")
    int insertFile(File file, Integer userId);

    @Select("SELECT * FROM FILES WHERE fileId = #{fileId}")
    File findFileById(Integer fileId);

    @Delete("DELETE FROM FILES WHERE fileId = #{fileId}")
    int deleteFile(Integer fileId);
}
