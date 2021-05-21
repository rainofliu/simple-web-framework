package org.geekbang.framework.helper;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.geekbang.framework.bean.FileParam;
import org.geekbang.framework.bean.FormParam;
import org.geekbang.framework.bean.Param;
import org.geekbang.framework.utils.CollectionUtil;
import org.geekbang.framework.utils.FileUtil;
import org.geekbang.framework.utils.StreamUtil;
import org.geekbang.framework.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 文件上传 助手类
 *
 * @author ajin
 */
public final class UploadHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadHelper.class);

    /**
     * Apache Commons Upload 提供的 Servlet文件上传对象
     */
    private static ServletFileUpload servletFileUpload;

    /**
     * 初始化
     */
    public static void init(ServletContext servletContext) {
        File repository = (File)servletContext.getAttribute("javax.servlet.context.tempDir");

        servletFileUpload = new ServletFileUpload(
            new DiskFileItemFactory(DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD, repository));

        // 设置上传文件的最大值
        int uploadLimit = ConfigHelper.getAppUploadLimit();
        if (uploadLimit != 0) {
            servletFileUpload.setFileSizeMax(uploadLimit * 1024 * 1024);
        }

    }

    /**
     * 判断请求是否为multipart类型
     */
    public static boolean isMultipart(HttpServletRequest request) {
        return ServletFileUpload.isMultipartContent(request);
    }

    public static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * 创建请求参数对象
     */
    public static Param createParam(HttpServletRequest request) throws IOException {
        List<FormParam> formParamList = new ArrayList<>();
        List<FileParam> fileParamList = new ArrayList<>();

        try {
            Map<String, List<FileItem>> fileItemListMap = servletFileUpload.parseParameterMap(request);

            if (CollectionUtil.isNotEmpty(fileItemListMap)) {
                for (Map.Entry<String, List<FileItem>> fileItemListEntry : fileItemListMap.entrySet()) {

                    String         fieldName    = fileItemListEntry.getKey();
                    List<FileItem> fileItemList = fileItemListEntry.getValue();

                    if (CollectionUtil.isNotEmpty(fileItemList)) {
                        for (FileItem fileItem : fileItemList) {
                            // 如果文件通过表单上传
                            if (fileItem.isFormField()) {
                                String fieldValue = fileItem.getString(DEFAULT_ENCODING);

                                // 添加表单参数
                                formParamList.add(new FormParam(fieldName, fieldValue));
                            } else {
                                // 获取文件名称
                                String fileName = FileUtil.getRealFileName(
                                    new String(fileItem.getName().getBytes(), DEFAULT_ENCODING));
                                if (StringUtil.isNotEmpty(fileName)) {
                                    long        fileSize    = fileItem.getSize();
                                    String      contentType = fileItem.getContentType();
                                    InputStream ins         = fileItem.getInputStream();
                                    // 添加 文件参数
                                    fileParamList.add(new FileParam(fieldName, fileName, fileSize, contentType, ins));
                                }
                            }
                        }
                    }
                }
            }
        } catch (FileUploadException e) {
            LOGGER.error(" create param failure", e);
            throw new RuntimeException(e);
        }

        return new Param(formParamList, fileParamList);
    }

    /**
     * 上传文件
     */
    public static void uploadFile(String basePath, FileParam fileParam) {

        try {
            if (fileParam != null) {
                String filePath = basePath + fileParam.getFileName();
                FileUtil.createFile(filePath);

                InputStream inputStream = new BufferedInputStream(fileParam.getInputStream());

                OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(filePath));

                StreamUtil.copyStream(inputStream, outputStream);
            }
        } catch (Exception e) {
            LOGGER.error(" upload file failure", e);
            throw new RuntimeException(e);
        }
    }

    public static void uploadFile(String basePath, List<FileParam> fileParamList) {
        try {
            if (CollectionUtil.isNotEmpty(fileParamList)) {
                for (FileParam fileParam : fileParamList) {
                    uploadFile(basePath, fileParam);
                }
            }
        } catch (Exception e) {
            LOGGER.error(" upload file failure", e);
            throw new RuntimeException(e);
        }
    }
}
