package com.perfect.filesystem.Utils;

import java.io.File;
import java.io.IOException;

/**
 * 路径 工具类
 *
 * @author zxh
 * @date 2019年 07月20日 19:31:54
 */
public class PathUtil {

    public static void createPathAndFile(String fileUuid, String fileName) throws IOException {
        // 获取路径
        String rtnFile = generateServerPath(fileUuid, fileName);
        getAndCreateSFile(rtnFile,fileName);
    }


    /**
     * 建立文件
     * @param uploadDir
     * @param filename
     * @return
     * @throws IOException
     */
    public static  File getAndCreateSFile(String uploadDir, String filename) throws IOException {
        File desc = new File(uploadDir + File.separator + filename);

        if (!desc.getParentFile().exists()) {
            desc.getParentFile().mkdirs();
        }
        if (!desc.exists()) {
            desc.createNewFile();
        }
        return desc;
    }

    /**
     * 获取保存路径
     */
    public static String generateServerPath(String fileUuid, String fileName) {
        if (fileUuid == null) {
            return null;
        }
        int modulus = getFileIdModulus(fileUuid);
        String today = DateTimeUtil.dateTime();
        String thisYear = DateTimeUtil.getYear();

        StringBuffer resumePath = new StringBuffer();
        resumePath.append(thisYear).append(File.separator).append(today).append(File.separator).append(modulus)
                .append(File.separator).append(fileUuid).append(File.separator).append(fileName);

        return resumePath.toString();
    }

    /**
     *
     * @param fileId
     * @return
     */
    private static int getFileIdModulus(String fileId) {
        int hashCode = fileId.hashCode();
        int modulus = Math.abs(hashCode % 1000);

        return modulus;
    }

}
