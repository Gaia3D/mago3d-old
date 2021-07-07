package gaia3d.controller;

import gaia3d.domain.FileType;
import gaia3d.domain.uploaddata.UploadDataFile;
import gaia3d.domain.uploaddata.UploadDataType;
import gaia3d.support.LogMessageSupport;
import gaia3d.utils.DateUtils;
import gaia3d.utils.FileUtils;
import gaia3d.utils.FormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
class UploadDataRestControllerTest {
	
	// 파일 copy 시 버퍼 사이즈
	public static final int BUFFER_SIZE = 8192;
	public static List<String> uploadTypeList = new ArrayList<>(
		Arrays.asList(
			"zip",
			"citygml",
			"gml",
			"jpg",
			"gif",
			"png"
		)
	);
	
	public static List<String> converterTypeList= new ArrayList<>(
		Arrays.asList(
			"citygml",
			"gml"
		)
	);

	@Test
	//@Disabled
	void unzipTest() throws Exception {
		unzip();
	}

	private Map<String, Object> unzip() {
		Map<String, Object> result = new HashMap<>();
		// 전체 파일 사이즈
		long totalFileSize = 0L;
		String today = DateUtils.getToday(FormatUtils.YEAR_MONTH_DAY_TIME14);
		String userId = "admin";

		File uploadedFile = new File("c:\\data\\3d-data\\unzip-test.zip");
		
		// input directory 생성
		String targetDirectory = "D:\\test\\" + System.nanoTime() + File.separator;
		FileUtils.makeDirectory(targetDirectory);
		
		String dataType = "citygml";

		// input directory 생성
		targetDirectory = targetDirectory + userId + "_" + System.nanoTime() + File.separator;
		FileUtils.makeDirectory(targetDirectory);
		
		// converter 변환 대상 파일 수
		int converterTargetCount = 0;
		Map<String, String> fileNameMatchingMap = new HashMap<>();
		List<UploadDataFile> uploadDataFileList = new ArrayList<>();
		
		int count = 0;

		try ( ZipFile zipFile = new ZipFile(uploadedFile)) {
			String directoryPath = targetDirectory;
			String subDirectoryPath = "";
			String directoryName = null;
			int depth = 1;
			Enumeration<? extends ZipArchiveEntry> entries = zipFile.getEntries();

			while( entries.hasMoreElements() ) {
				UploadDataFile uploadDataFile = new UploadDataFile();

				ZipArchiveEntry entry = entries.nextElement();
				String unzipfileName = targetDirectory + entry.getName();
				boolean converterTarget = false;

				if( entry.isDirectory() ) {
					// 디렉토리인 경우
					uploadDataFile.setFileType(FileType.DIRECTORY.name());
					if(directoryName == null) {
						uploadDataFile.setFileName(entry.getName());
						uploadDataFile.setFileRealName(entry.getName());
						directoryName = entry.getName();
						directoryPath = directoryPath + directoryName;
						//subDirectoryPath = directoryName;
					} else {
						String fileName;
						if(entry.getName().indexOf(directoryName) >=0) {
							fileName = entry.getName().substring(entry.getName().indexOf(directoryName) + directoryName.length());
						} else {
							// 이전이 디렉토리, 현재도 디렉토리인데 서로 다른 디렉토리
							if(directoryPath.indexOf(directoryName) >=0) {
								directoryPath = directoryPath.replace(directoryName, "");
								directoryName = null;
							}
							fileName = entry.getName();
						}
						uploadDataFile.setFileName(fileName);
						uploadDataFile.setFileRealName(fileName);
						directoryName = fileName;
						directoryPath = directoryPath + fileName;
						subDirectoryPath = fileName;
					}

					File file = new File(unzipfileName);
					file.mkdirs();
					uploadDataFile.setFilePath(directoryPath);
					uploadDataFile.setFileSubPath(subDirectoryPath);
					uploadDataFile.setDepth(depth);
					depth++;
				} else {
					// 파일인 경우
					String fileName;
					String extension = null;
					String[] divideFileName;
					String saveFileName;

					// TODO zip 파일도 확장자 validation 체크를 해야 함
					if(directoryName == null) {
						fileName = entry.getName();
						divideFileName = fileName.split("\\.");
						saveFileName = fileName;
						if(divideFileName.length != 0) {
							extension = divideFileName[divideFileName.length - 1];
							if(uploadTypeList.contains(extension.toLowerCase())) {

								String searchfileNameKey = fileName.substring(0, fileName.length() - extension.length() - 1);
								String sameFileName = fileNameMatchingMap.get(searchfileNameKey);
								if(converterTypeList.contains(extension.toLowerCase())) {
									if(!dataType.equalsIgnoreCase(extension)) {
										// 데이터 타입과 업로딩 파일 확장자가 같지 않고
										if(	UploadDataType.CITYGML == UploadDataType.findBy(dataType)
												&& UploadDataType.GML.getValue().equalsIgnoreCase(extension)){
											// 데이터 타입은 citygml 인데 확장자는 gml 인 경우 통과
										} else if(UploadDataType.INDOORGML == UploadDataType.findBy(dataType)
												&& UploadDataType.GML.getValue().equalsIgnoreCase(extension)) {
											// 데이터 타입은 indoorgml 인데 확장자는 gml 인 경우 통과
										} else {
											// 전부 예외
											log.info("@@@@@@@@@@@@ datatype = {}, extension = {}", dataType, extension);
											result.put("errorCode", "file.ext.invalid");
											return result;
										}
									}

									if(UploadDataType.CITYGML == UploadDataType.findBy(dataType) && UploadDataType.INDOORGML == UploadDataType.findBy(extension)) {
										// 전부 예외
										log.info("@@@@@@@@@@@@ 데이터 타입이 다른 경우. datatype = {}, extension = {}", dataType, extension);
										result.put("errorCode", "file.ext.invalid");
										return result;
									}

									if (UploadDataType.CITYGML.getValue().equalsIgnoreCase(dataType) && UploadDataType.GML.getValue().equalsIgnoreCase(extension)) {
										extension = UploadDataType.CITYGML.getValue();
									} else if (UploadDataType.INDOORGML.getValue().equalsIgnoreCase(dataType) && UploadDataType.GML.getValue().equalsIgnoreCase(extension)) {
										extension = UploadDataType.INDOORGML.getValue();
									}

									// 변환 대상 파일만 이름을 변경하고 나머지 파일은 그대로 이름 유지
									saveFileName = userId + "_" + today + "_" + System.nanoTime() + "." + extension;
									converterTarget = true;
									converterTargetCount++;
								}
							}
						}
					} else {
						if(entry.getName().indexOf(directoryName) >= 0) {
							// 디렉토리내 파일의 경우
							fileName = entry.getName().substring(entry.getName().indexOf(directoryName) + directoryName.length());
						} else {
							fileName = entry.getName();
							if(directoryPath.indexOf(directoryName) >= 0) {
								directoryPath = directoryPath.replace(directoryName, "");
								directoryName = null;
							}
						}
						divideFileName = fileName.split("\\.");
						saveFileName = fileName;
						if(divideFileName.length != 0) {
							extension = divideFileName[divideFileName.length - 1];
							if(uploadTypeList.contains(extension.toLowerCase())) {

								String searchfileNameKey = fileName.substring(0, fileName.length() - extension.length() - 1);
								String sameFileName = fileNameMatchingMap.get(searchfileNameKey);
								if(converterTypeList.contains(extension.toLowerCase())) {
									if(!dataType.equalsIgnoreCase(extension)) {
										// 데이터 타입과 업로딩 파일 확장자가 같지 않고
										if(	UploadDataType.CITYGML == UploadDataType.findBy(dataType)
												&& UploadDataType.GML.getValue().equalsIgnoreCase(extension)){
											// 데이터 타입은 citygml 인데 확장자는 gml 인 경우 통과
										} else if(UploadDataType.INDOORGML == UploadDataType.findBy(dataType)
												&& UploadDataType.GML.getValue().equalsIgnoreCase(extension)) {
											// 데이터 타입은 indoorgml 인데 확장자는 gml 인 경우 통과
										} else {
											// 전부 예외
											log.info("@@@@@@@@@@@@ datatype = {}, extension = {}", dataType, extension);
											result.put("errorCode", "file.ext.invalid");
											return result;
										}
									}

									if(UploadDataType.CITYGML == UploadDataType.findBy(dataType) && UploadDataType.INDOORGML == UploadDataType.findBy(extension)) {
										// 전부 예외
										log.info("@@@@@@@@@@@@ 데이터 타입이 다른 경우. datatype = {}, extension = {}", dataType, extension);
										result.put("errorCode", "file.ext.invalid");
										return result;
									}

									if (UploadDataType.CITYGML.getValue().equalsIgnoreCase(dataType) && UploadDataType.GML.getValue().equalsIgnoreCase(extension)) {
										extension = UploadDataType.CITYGML.getValue();
									} else if (UploadDataType.INDOORGML.getValue().equalsIgnoreCase(dataType) && UploadDataType.GML.getValue().equalsIgnoreCase(extension)) {
										extension = UploadDataType.INDOORGML.getValue();
									}

									// 변환 대상 파일만 이름을 변경하고 나머지 파일은 그대로 이름 유지
									saveFileName = userId + "_" + today + "_" + System.nanoTime() + "." + extension;
									converterTarget = true;
									converterTargetCount++;
								}
							} else {
								// 예외 처리
								log.info("@@ file.ext.invalid. extList = {}, extension = {}", uploadTypeList, fileName);
								result.put("errorCode", "file.ext.invalid");
								return result;
							}
						}
					}
					uploadDataFile = fileCopyInUnzip(uploadDataFile, zipFile, entry, directoryPath, saveFileName, extension, fileName, subDirectoryPath, depth);
					totalFileSize += Long.valueOf(uploadDataFile.getFileSize());
				}

				uploadDataFile.setConverterTarget(converterTarget);
				uploadDataFile.setFileSize(String.valueOf(entry.getSize()));
				uploadDataFileList.add(uploadDataFile);
			}
		} catch(RuntimeException ex) {
			log.info("@@@@@@@@@@@@ RuntimeException. message = {}", ex.getClass().getName());
		} catch(IOException ex) {
			log.info("@@@@@@@@@@@@ IOException. message = {}", ex.getClass().getName());
		}

		result.put("converterTargetCount", converterTargetCount);
		result.put("uploadDataFileList", uploadDataFileList);
		result.put("totalFileSize", totalFileSize);
		return result;
	}

	/*
	 * unzip 로직 안에서 파일 복사
	 */
	private UploadDataFile fileCopyInUnzip(UploadDataFile uploadDataFile, ZipFile zipFile, ZipArchiveEntry entry, String directoryPath, String saveFileName,
										   String extension, String fileName, String subDirectoryPath, int depth) {
		long size = 0L;
		try ( 	InputStream inputStream = zipFile.getInputStream(entry);
				 FileOutputStream outputStream = new FileOutputStream(directoryPath + saveFileName); ) {

			int bytesRead = 0;
			byte[] buffer = new byte[BUFFER_SIZE];
			while ((bytesRead = inputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
				size += bytesRead;
				outputStream.write(buffer, 0, bytesRead);
			}

			uploadDataFile.setFileType(FileType.FILE.name());
			uploadDataFile.setFileExt(extension);
			uploadDataFile.setFileName(fileName);
			uploadDataFile.setFileRealName(saveFileName);
			uploadDataFile.setFilePath(directoryPath);
			uploadDataFile.setFileSubPath(subDirectoryPath);
			uploadDataFile.setDepth(depth);
			uploadDataFile.setFileSize(String.valueOf(size));

		} catch(IOException e) {
			LogMessageSupport.printMessage(e);
			log.info("@@@@@@@@@@@@ io exception. message = {}", e.getClass().getName());
			uploadDataFile.setErrorMessage(e.getMessage());
		} catch(Exception e) {
			LogMessageSupport.printMessage(e);
			log.info("@@@@@@@@@@@@ exception. message = {}", e.getClass().getName());
			uploadDataFile.setErrorMessage(e.getMessage());
		}

		return uploadDataFile;
	}

}
