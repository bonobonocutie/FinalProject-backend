package com.pet.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

@Service
public class MotionService {

    private AmazonRekognition rekognitionClient;
    private AmazonS3 s3Client;
    private String bucketName;
    private String googleApiKey;
    
    public MotionService(@Value("${google.api.key}") String googleApiKey,
			 @Value("${cloud.aws.credentials.accessKey}") String accessKey,
			 @Value("${cloud.aws.credentials.secretKey}") String secretKey) {
    	
    	BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
    	
    	rekognitionClient = AmazonRekognitionClientBuilder.standard()
    			.withCredentials(new AWSStaticCredentialsProvider(awsCreds))
    	        .withRegion(Regions.AP_NORTHEAST_2)
    	        .build();
    	
    	s3Client = AmazonS3ClientBuilder.standard()
    	        .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
    	        .withRegion(Regions.AP_NORTHEAST_2)
    	        .build();
    	
    	bucketName = "petshopmotion";
    	
    	this.googleApiKey = googleApiKey;
    	
    }

    public String Capturing(String streamUrl) {
    	String motion = null;
        try {
        	// FFmpeg 다운로드
        	// FFmpegDown();
        	
            String filePath = captureFrame(streamUrl);
            if (filePath != null) {
            	// S3 업로드
                // uploadToS3(filePath);
            	
            	// 파이썬 다운로드
            	// pythonDown();
            	
                // 이미지 분석
                // 1. aws 분석
                // analyzeImage(filePath);
                
                // 2. gemini 분석
                motion = gemini(filePath);
                
                // 파일 삭제
                File file = new File(filePath);
                if (file.delete()) {
                    System.out.println("파일 삭제 성공: " + filePath);
                } else {
                    System.err.println("파일 삭제 실패: " + filePath);
                }
                
            }
            // 1초 대기
            Thread.sleep(1000);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        
        return motion;
    }

    private String captureFrame(String streamUrl) throws IOException {
        String fileName = "captured-image-" + System.currentTimeMillis() + ".jpg"; // 현재 시간을 포함한 파일 이름
        String ffmpegPath = "src/main/resources/ffmpeg.exe";
        // FFmpeg 실행 명령어 구성
        String command = String.format(
                "\"%s\" -i \"%s\" -vframes 1 \"%s\"",
                ffmpegPath,
                streamUrl,
                fileName
            );
        
        // FFmpeg 프로세스 실행
        Process process = Runtime.getRuntime().exec(command);
        
        // FFmpeg의 오류 출력 스트림을 읽기 위한 BufferedReader
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.err.println(line); // 오류 메시지 출력
            }
        }

        // 프로세스 종료 코드 확인
        try {
            int exitCode = process.waitFor();
            if (exitCode == 0) {
            	System.out.println("캡처 성공");
                return fileName; // 캡처 성공 시 파일 이름 반환
            } else {
                System.err.println("프레임 캡처 실패: " + exitCode);
                return null;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("프레임 캡처 중 오류 발생", e);
        }
    }

    private void uploadToS3(String filePath) {
        File file = new File(filePath);
        s3Client.putObject(bucketName, file.getName(), file);
    }

    private void analyzeImage(String imageName) {
        DetectLabelsRequest request = new DetectLabelsRequest()
                .withImage(new Image().withS3Object(new S3Object().withBucket(bucketName).withName(imageName)))
                .withMaxLabels(10)
                .withMinConfidence(75F);

        DetectLabelsResult result = rekognitionClient.detectLabels(request);
        
        // 분석된 정보 출력
//        for (Label label : result.getLabels()) {
//            System.out.printf("Label: %s, Confidence: %.2f%%\n", label.getName(), label.getConfidence());
//        }
        
        List<String> labels = result.getLabels().stream()
                .map(label -> label.getName())
                .collect(Collectors.toList());
        // 감지된 레이블 처리 로직 추가 가능
        System.out.println(labels);
    }
    
    private String gemini(String filePath) {
    	String result = null;
    	try {
    		
    		
    		// ProcessBuilder로 실행 파일 실행
            ProcessBuilder processBuilder = new ProcessBuilder("src/main/resources/dist/script", filePath, googleApiKey);
            processBuilder.redirectErrorStream(true); // 오류 스트림도 표준 출력으로 리다이렉트

            // 프로세스 시작
            Process process = processBuilder.start();

            // 결과 출력
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
            	output.append(line);
            }

            // 프로세스 종료 대기
            int exitCode = process.waitFor();
            System.out.println("Process exited with code: " + exitCode);
    		
            result = output.toString();
    		
    		
    		
    		
    		
//            ProcessBuilder processBuilder = new ProcessBuilder("src/main/resources/Python312/python.exe", "src/main/resources/script.py", 
//            		filePath, googleApiKey);
//            Process process = processBuilder.start();
//            
//            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
//            StringBuilder errorOutput = new StringBuilder();
//            String line;
//            while ((line = errorReader.readLine()) != null) {
//                errorOutput.append(line);
//            }
//            System.out.println("Error output: " + errorOutput.toString());
//
//
//            // 결과 읽기
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            StringBuilder output = new StringBuilder();  // 출력을 저장할 StringBuilder
//            
//            while ((line = reader.readLine()) != null) {
////                System.out.println(line);
//                output.append(line);  // 각 라인을 StringBuilder에 추가
//            }
//
//            int exitCode = process.waitFor();
//            System.out.println("Exited with code: " + exitCode);
//            
//            // 파이썬에서 출력된 결과를 변수에 담기
//            result = output.toString();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
    	return result;
    }
    
    public void FFmpegDown() throws IOException {
    	bucketName = "petshopdownloadfile";
    	String localFile = "src/main/resources/ffmpeg.exe";
    	
    	 // 로컬 파일이 이미 존재하는지 확인
        File localDir = new File(localFile);
        if (localDir.exists()) {
        	System.out.println("파일이 이미 존재합니다");
            return;
        }
        
        // 파일 다운로드
        com.amazonaws.services.s3.model.S3Object s3Object = s3Client.getObject(bucketName, "ffmpeg.exe");
        InputStream inputStream = s3Object.getObjectContent();
        FileOutputStream outputStream = new FileOutputStream(localFile);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        outputStream.close();
        inputStream.close();
        
        System.out.println("파일 다운로드 완료: " + localFile);
    }
    
    public void pythonDown() throws IOException {
    	bucketName = "petshopdownloadfile";
    	String localDirectory = "src/main/resources/Python312";
    	
    	// 로컬 디렉토리가 이미 존재하는지 확인
        File localDir = new File(localDirectory);
        if (localDir.exists()) {
            System.out.println("디렉토리가 이미 존재합니다");
            return;
        }
        
        // S3에서 폴더 내의 모든 파일 다운로드
        System.out.println("python 다운 시작");
        downloadFolder(bucketName, "Python312", localDirectory);
         
    }
    
    public void downloadFolder(String bucketName, String folderName, String localDirectory) {
        // S3에서 객체 목록 가져오기
        ListObjectsRequest request = new ListObjectsRequest()
                .withBucketName(bucketName)
                .withPrefix(folderName);
        
        // S3 객체 목록 순회
        ObjectListing objectListing;
        do {
            objectListing = s3Client.listObjects(request);
            for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                String key = objectSummary.getKey();
                
                // 상대 경로 생성
                String fileName = key.substring(folderName.length()).replaceFirst("^/", ""); // 슬래시 제거
                
                // 로컬 파일 경로 생성
                File localFile = new File(localDirectory, fileName);

                // 로컬 디렉토리 생성
                if (!localFile.getParentFile().exists()) {
                    localFile.getParentFile().mkdirs(); // 필요한 경우 부모 디렉토리 생성
                }

                // 파일 다운로드
                try (InputStream inputStream = s3Client.getObject(bucketName, key).getObjectContent();
                     FileOutputStream outputStream = new FileOutputStream(localFile)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }
                    System.out.println("다운로드 완료: " + localFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            request.setMarker(objectListing.getNextMarker());
        } while (objectListing.isTruncated()); // 다음 페이지가 있으면 계속 반복
    }

    
}
