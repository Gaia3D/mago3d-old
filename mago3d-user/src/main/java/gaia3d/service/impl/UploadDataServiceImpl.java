package gaia3d.service.impl;

import gaia3d.domain.FileType;
import gaia3d.domain.membership.MembershipUsage;
import gaia3d.domain.uploaddata.UploadData;
import gaia3d.domain.uploaddata.UploadDataFile;
import gaia3d.domain.uploaddata.UploadDataType;
import gaia3d.persistence.UploadDataMapper;
import gaia3d.service.MembershipService;
import gaia3d.service.UploadDataService;
import gaia3d.support.LogMessageSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author Cheon JeongDae
 *
 */
@Slf4j
@Service
public class UploadDataServiceImpl implements UploadDataService {

	@Autowired
	private MembershipService membershipService;

	@Autowired
	private UploadDataMapper uploadDataMapper;
	
	/**
	 * 업로딩 데이터 총 건수
	 * @param uploadData
	 * @return
	 */
	@Transactional(readOnly=true)
	public Long getUploadDataTotalCount(UploadData uploadData) {
		return uploadDataMapper.getUploadDataTotalCount(uploadData);
	}
	
	/**
	 * 업로딩 데이터 목록
	 * @param uploadData
	 * @return
	 */
	@Transactional(readOnly=true)
	public List<UploadData> getListUploadData(UploadData uploadData) {
		return uploadDataMapper.getListUploadData(uploadData);
	}
	
	/**
	 * 업로딩 정보
	 * @param uploadData
	 * @return
	 */
	@Transactional(readOnly=true)
	public UploadData getUploadData(UploadData uploadData) {
		return uploadDataMapper.getUploadData(uploadData);
	}
	
//	/**
//	 * 업로딩 데이터 파일 총 건수
//	 * @param uploadDataFile
//	 * @return
//	 */
//	@Transactional(readOnly=true)
//	public Long getUploadDataFileTotalCount(UploadDataFile uploadDataFile) {
//		return uploadDataMapper.getUploadDataFileTotalCount(uploadDataFile);
//	}
//
//	/**
//	 * 업로딩 데이터 파일 총 용량
//	 * @param uploadDataFile
//	 * @return
//	 */
//	@Transactional(readOnly=true)
//	public Long getUploadDataFileTotalSize(UploadDataFile uploadDataFile) {
//		return uploadDataMapper.getUploadDataFileTotalSize(uploadDataFile);
//	}
	
	/**
	 * 업로딩 데이터 파일 목록
	 * @param uploadData
	 * @return
	 */
	@Transactional(readOnly=true)
	public List<UploadDataFile> getListUploadDataFile(UploadData uploadData) {
		return uploadDataMapper.getListUploadDataFile(uploadData);
	}

	/**
	 * 업로딩 데이터 파일
	 * @param uploadDataFile
	 * @return	업로딩 데이터 파일
	 */
	@Transactional(readOnly=true)
	public UploadDataFile getUploadDataFile(UploadDataFile uploadDataFile) {
		return uploadDataMapper.getUploadDataFile(uploadDataFile);
	}
	
	/**
	 * 사용자 파일 정보 업로딩
	 * @param uploadData
	 * @param uploadDataFileList
	 * @param totalFileSize
	 * @return
	 */
	@Transactional
	public int insertUploadData(UploadData uploadData, List<UploadDataFile> uploadDataFileList, Long totalFileSize) {
		int result = uploadDataMapper.insertUploadData(uploadData);
		
		Long uploadDataId = uploadData.getUploadDataId();
		String userId = uploadData.getUserId();
		for(UploadDataFile uploadDataFile : uploadDataFileList) {
			uploadDataFile.setUploadDataId(uploadDataId);
			uploadDataFile.setUserId(userId);
			uploadDataMapper.insertUploadDataFile(uploadDataFile);
			result++;
		}

		MembershipUsage membershipUsage = membershipService.getMembershipUsageByUserId(userId);
		membershipUsage.setUseUploadFileSize(Double.valueOf(totalFileSize));
		membershipService.updateUsage(membershipUsage);

		return result;
	}
	
	/**
	 * 사용자 파일 정보 수정
	 * @param uploadData
	 * @return
	 */
	@Transactional
	public int updateUploadData(UploadData uploadData) {
		return uploadDataMapper.updateUploadData(uploadData);
	}
	
	/**
	 * 업로딩 데이터 삭제
	 * @param uploadData
	 * @return
	 */
	@Transactional
	public int deleteUploadData(UploadData uploadData) {
			
		List<UploadDataFile> uploadDataFileList = uploadDataMapper.getListUploadDataFile(uploadData);
		uploadDataMapper.deleteUploadDataFile(uploadData);
		for(UploadDataFile deleteUploadDataFile : uploadDataFileList) {
			String fileName = null;
			if(FileType.DIRECTORY == FileType.valueOf(deleteUploadDataFile.getFileType())) {
				fileName = deleteUploadDataFile.getFilePath();
			} else {
				fileName = deleteUploadDataFile.getFilePath() + deleteUploadDataFile.getFileRealName();
			}
			
			// TODO 처리 불필요
			fileName = fileName.replaceAll("&", "");
			File file = new File(fileName);
			if(file.exists()) {
				file.delete();
			}
		}
			
		return uploadDataMapper.deleteUploadData(uploadData);
	}

	@Transactional
	public int updateUploadDataAndFile(UploadData uploadData) {
		int result = 0;
		try {
			result = uploadDataMapper.updateUploadData(uploadData);
			if (result > 0) {
				List<UploadDataFile> uploadDataFileList = uploadDataMapper.getListUploadDataFile(uploadData);
				for (UploadDataFile uploadDataFile : uploadDataFileList) {
	
					String fileName = uploadDataFile.getFileName();
					String uploadExt = uploadDataFile.getFileExt();
	
					String[] fileNameValues = fileName.split("\\.");
					String extension = fileNameValues[fileNameValues.length - 1];
					
					// 원본이 gml 파일이고 데이터 타입을 citygml 혹은 indoorgml로 처음 등록과 다르게 변경하는 경우 
					if (UploadDataType.GML.getValue().equalsIgnoreCase(extension) && !uploadData.getDataType().equalsIgnoreCase(uploadExt)) {
						String originalFileName = uploadDataFile.getFileRealName();
						String updateFileName = originalFileName.replace(uploadExt, uploadData.getDataType());
						
						uploadDataFile.setFileExt(uploadData.getDataType());
						uploadDataFile.setFileRealName(updateFileName);
						
						// DB를 갱신
						result += uploadDataMapper.updateUploadDataFile(uploadDataFile);
						File uploadFile = Paths.get(uploadDataFile.getFilePath(), originalFileName).toFile();
						
						if (uploadFile.exists()) {
							// 파일 확장자를 변경.
							uploadFile.renameTo(Paths.get(uploadDataFile.getFilePath(), updateFileName).toFile());
						}
					}
				}
			}
		} catch(DataAccessException e) {
			LogMessageSupport.printMessage(e, "@@ DataAccessException. message = {}", e.getMessage());
		} catch(RuntimeException e) {
			LogMessageSupport.printMessage(e, "@@ RuntimeException. message = {}", e.getMessage());
		} catch(Exception e) {
			LogMessageSupport.printMessage(e, "@@ Exception. message = {}", e.getMessage());
		}
		return result;
	}
	
}