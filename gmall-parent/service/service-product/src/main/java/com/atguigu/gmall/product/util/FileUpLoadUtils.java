package com.atguigu.gmall.product.util;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * @ClassName FileUpLoadUtils
 * @Description fastdfs图片上传工具类
 * @Author yzchao
 * @Date 2022/10/25 15:08
 * @Version V1.0
 */
public class FileUpLoadUtils {

    //静态代码块中 fastdfs配置文件 只初始化一次
    static {

        try {
            //读取配置文件的信息
            ClassPathResource classPathResource = new ClassPathResource("abc.conf");
            //进行fastDFS的初始化
            ClientGlobal.init(classPathResource.getFilename());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String fileUpload(@RequestParam("file") MultipartFile file) {
        try {
            //初始化tracker的连接
            TrackerClient trackerClient = new TrackerClient();
            //通过客户端获取服务端的实例
            TrackerServer trackerServer = trackerClient.getConnection();
            //通过tacker获取storage的信息
            StorageClient storageClient = new StorageClient(trackerServer, null);
            //获取文件的名字: abc.jpg
            String originalFilename = file.getOriginalFilename();
            //通过storage进行文件上传
            /**
             * 1.文件的字节码: 从文件取
             * 2.文件的拓展名: 通过文件名取
             * 3.文件的附加参数:
             */
            String[] uploadFile = storageClient.upload_file(file.getBytes(), StringUtils.getFilenameExtension(originalFilename), null);
            return uploadFile[0] + "/" + uploadFile[1];
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
