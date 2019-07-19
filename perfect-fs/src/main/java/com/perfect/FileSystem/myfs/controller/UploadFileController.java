package com.perfect.filesystem.myfs.controller;

import com.perfect.filesystem.myfs.entity.FileEntryEntity;
import com.perfect.filesystem.myfs.service.TransferService;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;


/**
 * @author zxh
 */
@RestController
@Slf4j
@Api("上传文件")
@RequestMapping(value = "/api/[^/]+/uploadFile.json")
public class UploadFileController extends HttpServlet {
    @Autowired
    private TransferService fileService;

    private static final long serialVersionUID = 1L;
    private static final String SUCCESS =
        "{\"act\":\"uploadFile\",\"version\":\"v1.0\",\"flag\":\"SUCCESS\",\"msg\":\"上传文件成功\",\"data\":[${data}]}";
    private static final String FAILURE =
        "{\"act\":\"uploadFile\",\"version\":\"v1.0\",\"flag\":\"FAILURE\",\"msg\":\"上传文件失败\",\"data\":[]}";
    private static final String FAILURE_NOFILE =
        "{\"act\":\"uploadFile\",\"version\":\"v1.0\",\"flag\":\"FAILURE\",\"msg\":\"上传文件失败:无法获取文件信息\",\"data\":[]}";

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        Part part = request.getPart("file");
        if (part == null) {
            response.getWriter().write(
                "{\"act\":\"uploadFile\",\"version\":\"v1.0\",\"flag\":\"FAILURE\",\"msg\":\"上传文件失败:无法获取文件信息\",\"data\":[]}");
            return;
        }

        InputStream fileUploaded = null;
        String result = "";
        try {
            String originalName = parseFileName(part);

            fileUploaded = part.getInputStream();
            FileEntryEntity fileEntry = fileService.uploadFile(originalName, fileUploaded);

            result = getResult(fileEntry);
        } catch (Exception e) {
            e.printStackTrace();

            result =
                "{\"act\":\"uploadFile\",\"version\":\"v1.0\",\"flag\":\"FAILURE\",\"msg\":\"上传文件失败\",\"data\":[]}";
        } finally {
            try {
                if (fileUploaded != null) {
                    fileUploaded.close();
                }
            } catch (Exception ignore) {
            }

            response.getWriter().write(result);
        }
    }

    private String parseFileName(Part part) {
        String originalName = "";

        for (String headerName : part.getHeaderNames()) {
            if ("Content-Disposition".equalsIgnoreCase(headerName)) {
                String contentdisposition = part.getHeader(headerName);
                originalName = parseFileName(contentdisposition);

                break;
            }
        }

        return originalName;
    }

    private String parseFileName(String contentdisposition) {
        String fileName = null;
        String fileNameExtractorRegex = "filename=\".+\"";
        Pattern pattern = Pattern.compile(fileNameExtractorRegex);
        Matcher matcher = pattern.matcher(contentdisposition);
        if (matcher.find()) {
            fileName = matcher.group();
            fileName = fileName.substring(10, fileName.length() - 1);
        }

        return fileName;
    }

    private String getResult(FileEntryEntity fileEntry) {
        StringBuilder data = new StringBuilder(512);
        data.append("{\"fileName\":\"")
            .append(StringEscapeUtils.escapeJavaScript(fileEntry.getFileName()))
            .append("\",\"fileUuid\":\"").append(fileEntry.getFileUuid()).append("\",\"fileSize\":\"")
            .append(fileEntry.getFileSize()).append("\"}");

        String json =
            "{\"act\":\"uploadFile\",\"version\":\"v1.0\",\"flag\":\"SUCCESS\",\"msg\":\"上传文件成功\",\"data\":[${data}]}"
                .replace("${data}", data.toString());

        return json;
    }
}
