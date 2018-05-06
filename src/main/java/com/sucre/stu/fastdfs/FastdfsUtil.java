package com.sucre.stu.fastdfs;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;

public class FastdfsUtil {

	// private static final String CONF_FILENAME =
	// Thread.currentThread().getContextClassLoader().getResource("").getPath()
	// + "fdfs_client.conf";
	private static final String CONF_FILENAME = "src/main/resources/fdfs/fdfs_client.conf";
	private static StorageClient1 storageClient1 = null;

	private static Logger logger = Logger.getLogger(FastdfsUtil.class);

	/**
	 * ֻ����һ��.
	 */
	static {
		try {
			logger.info("=== CONF_FILENAME:" + CONF_FILENAME);
			ClientGlobal.init(CONF_FILENAME);
			TrackerClient trackerClient = new TrackerClient(
					ClientGlobal.g_tracker_group);
			TrackerServer trackerServer = trackerClient.getConnection();
			if (trackerServer == null) {
				logger.error("getConnection return null");
			}
			StorageServer storageServer = trackerClient
					.getStoreStorage(trackerServer);
			if (storageServer == null) {
				logger.error("getStoreStorage return null");
			}
			storageClient1 = new StorageClient1(trackerServer, storageServer);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * 
	 * @param file
	 *            �ļ�
	 * @param fileName
	 *            �ļ���
	 * @return ����Null��Ϊʧ��
	 */
	public static String uploadFile(File file, String fileName) {
		FileInputStream fis = null;
		try {
			NameValuePair[] meta_list = null; // new NameValuePair[0];
			fis = new FileInputStream(file);
			byte[] file_buff = null;
			if (fis != null) {
				int len = fis.available();
				file_buff = new byte[len];
				fis.read(file_buff);
			}

			String fileid = storageClient1.upload_file1(file_buff,
					getFileExt(fileName), meta_list);
			return fileid;
		} catch (Exception ex) {
			logger.error(ex);
			return null;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}
	}

	/**
	 * ����������Զ���ļ�����ɾ��һ���ļ�
	 * 
	 * @param groupName
	 *            ���� "group1" �����ָ����ֵ��Ĭ��Ϊgroup1
	 * @param fileName
	 *            ����"M00/00/00/wKgxgk5HbLvfP86RAAAAChd9X1Y736.jpg"
	 * @return 0Ϊ�ɹ�����0Ϊʧ�ܣ�����Ϊ�������
	 */
	public static int deleteFile(String groupName, String fileName) {
		try {
			int result = storageClient1.delete_file(
					groupName == null ? "group1" : groupName, fileName);
			return result;
		} catch (Exception ex) {
			logger.error(ex);
			return 0;
		}
	}

	/**
	 * ����fileId��ɾ��һ���ļ������������õľ��������ķ�ʽ���ϴ��ļ�ʱֱ�ӽ�fileId�����������ݿ��У�
	 * 
	 * @param fileId
	 *            file_idԴ���еĽ���file_id the file id(including group name and
	 *            filename);����
	 *            group1/M00/00/00/ooYBAFM6MpmAHM91AAAEgdpiRC0012.xml
	 * @return 0Ϊ�ɹ�����0Ϊʧ�ܣ�����Ϊ�������
	 */
	public static int deleteFile(String fileId) {
		try {
			int result = storageClient1.delete_file1(fileId);
			return result;
		} catch (Exception ex) {
			logger.error(ex);
			return 0;
		}
	}

	/**
	 * �޸�һ���Ѿ����ڵ��ļ�
	 * 
	 * @param oldFileId
	 *            ԭ�����ļ���fileId, file_idԴ���еĽ���file_id the file id(including group
	 *            name and filename);����
	 *            group1/M00/00/00/ooYBAFM6MpmAHM91AAAEgdpiRC0012.xml
	 * @param file
	 *            ���ļ�
	 * @param filePath
	 *            ���ļ�·��
	 * @return ���ؿ���Ϊʧ��
	 */
	public static String modifyFile(String oldFileId, File file, String filePath) {
		String fileid = null;
		try {
			// ���ϴ�
			fileid = uploadFile(file, filePath);
			if (fileid == null) {
				return null;
			}
			// ��ɾ��
			int delResult = deleteFile(oldFileId);
			if (delResult != 0) {
				return null;
			}
		} catch (Exception ex) {
			logger.error(ex);
			return null;
		}
		return fileid;
	}

	/**
	 * �ļ�����
	 * 
	 * @param fileId
	 * @return ����һ����
	 */
	public static InputStream downloadFile(String fileId) {
		try {
			byte[] bytes = storageClient1.download_file1(fileId);
			InputStream inputStream = new ByteArrayInputStream(bytes);
			return inputStream;
		} catch (Exception ex) {
			logger.error(ex);
			return null;
		}
	}

	/**
	 * ��ȡ�ļ���׺���������㣩.
	 * 
	 * @return �磺"jpg" or "".
	 */
	private static String getFileExt(String fileName) {
		if (StringUtils.isBlank(fileName) || !fileName.contains(".")) {
			return "";
		} else {
			return fileName.substring(fileName.lastIndexOf(".") + 1); // �������ĵ�
		}
	}
}
