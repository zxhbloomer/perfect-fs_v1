package com.perfect.filesystem.myfs.controller;

import com.perfect.filesystem.myfs.properties.PerfectFsProperties;
import com.perfect.filesystem.myfs.service.TransferService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zxh
 */
@RestController
@Slf4j
@Api("下载相关")
@RequestMapping({"/api/[^/]+/downloadFile.file"})
public class DownloadFileAction extends HttpServlet {

    PerfectFsProperties perfectFsProperties;

    private static final long serialVersionUID = 1L;
    @Autowired
    private TransferService fs;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    public void processRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.setCharacterEncoding("utf-8");

            String fileUid = request.getParameter("fileUuid");
            String size = request.getParameter("size");
            String crop = request.getParameter("crop");

            String uri = fs.downloadFile(fileUid);
            if (uri == null) {
                response.setStatus(404);
            } else if (uri.startsWith("http")) {
                response.setHeader("Location", uri);
                response.setStatus(302);
            } else {
                if (StringUtils.isNotBlank(crop)) {
                    gm(crop, PATTERN_CROP, uri, new ImageAction() {
                        @Override
                        public void assembleCmd(IMOperation operation, Matcher matcher) {
                            int width = Integer.parseInt(matcher.group(1));
                            int height = Integer.parseInt(matcher.group(2));
                            int left = Integer.parseInt(matcher.group(3));
                            int top = Integer.parseInt(matcher.group(4));
                            operation.addImage();
                            operation.crop(Integer.valueOf(width), Integer.valueOf(height), Integer.valueOf(left),
                                Integer.valueOf(top));
                            operation.addImage();
                        }
                    }, response);

                    return;
                }
                if (StringUtils.isNotBlank(size)) {
                    response.setCharacterEncoding("utf-8");
                    gm(size, PATTERN_SIZE, uri, new ImageAction() {
                        @Override
                        public void assembleCmd(IMOperation operation, Matcher matcher) {
                            int width = Integer.parseInt(matcher.group(1));
                            int height = Integer.parseInt(matcher.group(1));
                            operation.addImage();
                            operation.thumbnail(Integer.valueOf(width), Integer.valueOf(height));
                            operation.background("white");
                            operation.gravity("center");
                            operation.extent(Integer.valueOf(width), Integer.valueOf(height));
                            operation.addImage();
                        }
                    }, response);

                    return;
                }

                int originalNameIndex = uri.lastIndexOf("/");
                String originalName = uri.substring(originalNameIndex + 1);
                String requestUrl = request.getRequestURI();
                String requestRoot = requestUrl.replaceAll("/api/[^/]+/downloadFile.file", "");
                String uriEncoded = requestRoot + "/data/" + uri.substring(0, originalNameIndex + 1)
                    + URLEncoder.encode(originalName, "UTF-8").replaceAll("[+]", "%20");

                response.setHeader("Location", uriEncoded);
                response.setStatus(302);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IM4JavaException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final Pattern PATTERN_SIZE = Pattern.compile("([0-9]+)x([0-9]+)");
    private static final Pattern PATTERN_CROP = Pattern.compile("([0-9]+)x([0-9]+)-([0-9]+)x([0-9]+)");
    private static final int EOF = -1;

    private void gm(String suffix, Pattern pattern, String uri, ImageAction action, HttpServletResponse response)
        throws InterruptedException, IOException, IM4JavaException {
        // String rawFile = ConfigPropertites.getProperties("upload", "data.path", "E://fs_data/") + uri;
        String rawFile = perfectFsProperties.getUploadDataPath() + uri;
        String targetFile = rawFile.replace(".", "-" + suffix + ".");

        Matcher matcher = pattern.matcher(suffix);
        if (matcher.find()) {
            File image = new File(targetFile);
            if (!image.exists()) {
                IMOperation operation = new IMOperation();
                action.assembleCmd(operation, matcher);
                ConvertCmd cmd = new ConvertCmd(true);
                if (System.getProperty("os.name").toLowerCase().indexOf("win") != -1) {
                    String searchPath = perfectFsProperties.getUploadDataPath() + uri;
//                        ConfigPropertites.getProperties("upload", "gm.path", "E://GraphicsMagick-1.3.21-Q8");
                    cmd.setSearchPath(searchPath);
                }
                cmd.run(operation, new Object[] {rawFile, targetFile});
            }
            copy(image, response.getOutputStream());

            response.setHeader("Content-Length", String.valueOf(image.length()));
        } else {
            response.setStatus(404);
        }
    }

    private long copy(File file, OutputStream output) throws IOException {
        long count = 0L;
        byte[] buffer = new byte['က'];
        int n = 0;
        FileInputStream input = null;
        try {
            input = new FileInputStream(file);
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
                count += n;
            }
        } finally {
            if (input != null) {
                input.close();
            }
        }
        return count;
    }

    public static abstract interface ImageAction {
        public abstract void assembleCmd(IMOperation paramIMOperation, Matcher paramMatcher);
    }
}