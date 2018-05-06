package com.sucre.stu.fastdfs;

import java.io.File;

public class FastdfsUtilTest {

	public static void main(String[] args) {
		String basePath = "E:/workspace/stu-fastdfs/src/test/resources/file/upload";
		String fileName = "sonarqube.png";
		String filePath = basePath+"/"+fileName;
		File file = new File(filePath);
		System.out.println(FastdfsUtil.uploadFile(file, filePath));
	}
}
