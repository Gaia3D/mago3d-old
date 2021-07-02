package gaia3d.weather.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * TODO N중화 처리를 위해 FTP 로 다른 PM 으로 전송해 줘야 하는데....
 * 
 * 파일 처리 관련 Util
 * @author jeongdae
 *
 */
@Slf4j
public class FileUtils {

	/*
	 * TODO 대용량 엑셀 처리 관련 참조
	 * http://poi.apache.org/spreadsheet/how-to.html#sxssf
	 * 
	 */
	
	// 디렉토리 생성 방법 
	public static final int SUBDIRECTORY_YEAR = 1;
	public static final int SUBDIRECTORY_YEAR_MONTH = 2;
	public static final int SUBDIRECTORY_YEAR_MONTH_DAY = 3;
	
	// 사용자 일괄 등록
	public static final String USER_FILE_UPLOAD = "USER_FILE_UPLOAD";
	// Data 일괄 등록
	public static final String DATA_FILE_UPLOAD = "DATA_FILE_UPLOAD";
	// Smart Tiling Data 일괄 등록
	public static final String DATA_SMART_TILING_FILE_UPLOAD = "DATA_SMART_TILING_FILE_UPLOAD";
	// Issue 등록
	public static final String ISSUE_FILE_UPLOAD = "ISSUE_FILE_UPLOAD";
	// Data Attribute
	public static final String DATA_ATTRIBUTE_UPLOAD = "DATA_ATTRIBUTE_UPLOAD";
	// Data Object Attribute
	public static final String DATA_OBJECT_ATTRIBUTE_UPLOAD = "DATA_OBJECT_ATTRIBUTE_UPLOAD";
	
	// 사용자 일괄 등록의 경우 허용 문서 타입
	public static final String[] USER_FILE_TYPE = {"xlsx", "xls"};
	// 데이터 일괄 등록 문서 타입
	public static final String[] DATA_FILE_TYPE = {"xlsx", "xls", "json", "txt"};
	// Smart Tiling 데이터 일괄 등록 문서 타입
	public static final String[] DATA_SMART_TILING_FILE_TYPE = {"json", "txt"};
	// issue 등록의 경우 허용 문서 타입
	public static final String[] ISSUE_FILE_TYPE = {"png", "jpg", "jpeg", "gif", "tiff", "xlsx", "xls", "docx", "doc", "pptx", "ppt"};
	// data attribute 허용 문서 타입
	public static final String[] DATA_ATTRIBUTE_FILE_TYPE = {"json", "txt"};
	// data object attribute 허용 문서 타입
	public static final String[] DATA_OBJECT_ATTRIBUTE_FILE_TYPE = {"json", "txt"};
	// json 파일
	public static final String EXTENSION_JSON = "json";
	// txt 파일
	public static final String EXTENSION_TXT = "txt";
	// 엑셀 처리 기본 프로그램
	public static final String EXCEL_EXTENSION_XLS = "xls";
	// JEXCEL이 2007버전(xlsx) 을 읽지 못하기 때문에 POI를 병행해서 사용
	public static final String EXCEL_EXTENSION_XLSX = "xlsx";
	
	// 업로더 가능한 파일 사이즈(2G)
	public static final long FILE_UPLOAD_SIZE = 2_000_000_000L;
	// 파일 copy 시 버퍼 사이즈
	public static final int BUFFER_SIZE = 8192;


	public static boolean makeDirectory(String targetDirectory) {
		File directory = new File(targetDirectory);
		if(directory.exists()) {
			return true;
		} else {
			return directory.mkdir();
		}
	}
	
	/**
	 * 경로를 기준으로 디렉토리를 생성. window, linux 에서 File.separator 가 문제를 일으킴
	 * @param servicePath
	 * @param dataGroupPath
	 * @return
	 */
	public static boolean makeDirectoryByPath(String servicePath, String dataGroupPath) {
		String[] directors = dataGroupPath.split("/");
		String fullName = servicePath;
		
		boolean result = true;
		for(String directoryName : directors) {
			fullName = fullName + directoryName + File.separator;
			File directory = new File(fullName);
			if(directory.exists()) {
				result = true;
			} else {
				result = directory.mkdir();
				if(!result) return result;
			}
		}
		return result;
	}

	public static String makeDirectory(String userId, UploadDirectoryType uploadDirectoryType, String targetDirectory, String date) {
		//String today = DateUtils.getToday(FormatUtils.YEAR_MONTH_DAY_TIME14);
		String today = date;
		String year = today.substring(0,4);
		String month = today.substring(4,6);
		String day = today.substring(6,8);
		String sourceDirectory = targetDirectory;

		File rootDirectory = new File(sourceDirectory);
		if(!rootDirectory.exists()) {
			rootDirectory.mkdir();
		}

		// 사용자 디렉토리
		if(UploadDirectoryType.USERID_YEAR == uploadDirectoryType
				|| UploadDirectoryType.USERID_YEAR_MONTH == uploadDirectoryType
				|| UploadDirectoryType.USERID_YEAR_MONTH_DAY == uploadDirectoryType) {
			sourceDirectory = sourceDirectory + userId + File.separator;
			File userDirectory = new File(sourceDirectory);
			if(!userDirectory.exists()) {
				userDirectory.mkdir();
			}
		}

		// 년
		if(UploadDirectoryType.USERID_YEAR == uploadDirectoryType
				|| UploadDirectoryType.USERID_YEAR_MONTH == uploadDirectoryType
				|| UploadDirectoryType.USERID_YEAR_MONTH_DAY == uploadDirectoryType
				|| UploadDirectoryType.YEAR  == uploadDirectoryType
				|| UploadDirectoryType.YEAR_MONTH == uploadDirectoryType
				|| UploadDirectoryType.YEAR_MONTH_DAY == uploadDirectoryType
				|| UploadDirectoryType.YEAR_USERID == uploadDirectoryType
				|| UploadDirectoryType.YEAR_MONTH_USERID == uploadDirectoryType
				|| UploadDirectoryType.YEAR_MONTH_DAY_USERID == uploadDirectoryType) {
			sourceDirectory = sourceDirectory + year + File.separator;
			File yearDirectory = new File(sourceDirectory);
			if(!yearDirectory.exists()) {
				yearDirectory.mkdir();
			}
		}

		// 월
		if(UploadDirectoryType.USERID_YEAR_MONTH == uploadDirectoryType
				|| UploadDirectoryType.USERID_YEAR_MONTH_DAY == uploadDirectoryType
				|| UploadDirectoryType.YEAR_MONTH == uploadDirectoryType
				|| UploadDirectoryType.YEAR_MONTH_DAY == uploadDirectoryType
				|| UploadDirectoryType.YEAR_MONTH_USERID == uploadDirectoryType
				|| UploadDirectoryType.YEAR_MONTH_DAY_USERID == uploadDirectoryType) {
			sourceDirectory = sourceDirectory + month + File.separator;
			File monthDirectory = new File(sourceDirectory);
			if(!monthDirectory.exists()) {
				monthDirectory.mkdir();
			}
		}

		// 일
		if(UploadDirectoryType.USERID_YEAR_MONTH_DAY == uploadDirectoryType
				|| UploadDirectoryType.YEAR_MONTH_DAY == uploadDirectoryType
				|| UploadDirectoryType.YEAR_MONTH_DAY_USERID == uploadDirectoryType) {
			sourceDirectory = sourceDirectory + day + File.separator;
			File dayDirectory = new File(sourceDirectory);
			if(!dayDirectory.exists()) {
				dayDirectory.mkdir();
			}
		}

		// 사용자 디렉토리
		if(UploadDirectoryType.YEAR_USERID == uploadDirectoryType
				|| UploadDirectoryType.YEAR_MONTH_USERID == uploadDirectoryType
				|| UploadDirectoryType.YEAR_MONTH_DAY_USERID == uploadDirectoryType) {
			sourceDirectory = sourceDirectory + userId + File.separator;
			File userDirectory = new File(sourceDirectory);
			if(!userDirectory.exists()) {
				userDirectory.mkdir();
			}
		}

		return sourceDirectory;
	}
	
	public static String makeDirectory(String userId, UploadDirectoryType uploadDirectoryType, String targetDirectory) {
		String today = DateUtils.getToday(FormatUtils.YEAR_MONTH_DAY_TIME14);
		return makeDirectory(userId, uploadDirectoryType, targetDirectory, today);
	}
	
	public static String getFilePath(String dataGroupPath) {
		String[] names = dataGroupPath.split("/");

		// TODO SpringBuilder
		String filePath = "";
		for(String name : names) {
			filePath = filePath + name + File.separator;
		}
		return filePath;
	}
	
	public static void deleteFileReculsive(String path) {
		File folder = new File(path);
		try {
		    while(folder.exists()) {
		    if(!folder.isDirectory()) {
		    	folder.delete();
		    	break;
		    }
			File[] folder_list = folder.listFiles(); //파일리스트 얻어오기
			for (int j = 0; j < folder_list.length; j++) {
				folder_list[j].delete(); //파일 삭제 
			}
					
			if(folder_list.length == 0 && folder.isDirectory()){ 
				folder.delete(); //대상폴더 삭제
			}
	            }
		 } catch (Exception e) {
			e.getStackTrace();
		}
	}
}