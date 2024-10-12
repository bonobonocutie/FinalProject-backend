package com.pet.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.Attribute;
import com.amazonaws.services.rekognition.model.CreateCollectionRequest;
import com.amazonaws.services.rekognition.model.CreateCollectionResult;
import com.amazonaws.services.rekognition.model.DeleteCollectionRequest;
import com.amazonaws.services.rekognition.model.DetectFacesRequest;
import com.amazonaws.services.rekognition.model.DetectFacesResult;
import com.amazonaws.services.rekognition.model.Face;
import com.amazonaws.services.rekognition.model.FaceMatch;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.IndexFacesRequest;
import com.amazonaws.services.rekognition.model.ListFacesRequest;
import com.amazonaws.services.rekognition.model.ListFacesResult;
import com.amazonaws.services.rekognition.model.QualityFilter;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.rekognition.model.SearchFacesByImageRequest;
import com.amazonaws.services.rekognition.model.SearchFacesByImageResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service
public class FaceService {
	private AmazonRekognition rekognitionClient;
	private AmazonS3 s3Client;
	
	public FaceService(@Value("${cloud.aws.credentials.accessKey}") String accessKey, 
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
		
	}
	
	public DetectFacesResult detectFaces(byte[] imageBytes) {
	    Image image = new Image().withBytes(ByteBuffer.wrap(imageBytes));

	    DetectFacesRequest request = new DetectFacesRequest()
	            .withImage(image)
	            .withAttributes(Attribute.ALL); // 필요한 속성을 선택

	    return rekognitionClient.detectFaces(request);
	}
	
	
	public String compareFace(byte[] imageBytes, String collectionId) {
//		String collectionId = "user-face";
		String result  = null;
		
		Image image = new Image().withBytes(ByteBuffer.wrap(imageBytes));
		
        SearchFacesByImageRequest searchFacesByImageRequest = new SearchFacesByImageRequest()
                .withCollectionId(collectionId)
                .withImage(image)
                .withFaceMatchThreshold(80F);

//      s3 사진 가져와서 비교하는 방법
//      String bucket = "facefacelist";
//      String compare = "compare.jpg";
        
//		Image image = new Image()
//                .withS3Object(new S3Object().withBucket(bucket).withName(compare));
		
//      SearchFacesByImageRequest searchFacesByImageRequest = new SearchFacesByImageRequest()
//                .withCollectionId("user-face")
//                .withImage(new Image().withS3Object(new S3Object().withBucket(bucket).withName(compare)))
//                .withFaceMatchThreshold(80F); // Optional: Specify a confidence threshold

        SearchFacesByImageResult searchFacesByImageResult = rekognitionClient.searchFacesByImage(searchFacesByImageRequest);
        
        if (searchFacesByImageResult.getFaceMatches().isEmpty()) {
            System.out.println("얼굴이 컬렉션에 없습니다.");
            result = "실패";
            
        } else {
//            searchFacesByImageResult.getFaceMatches().forEach(faceMatch ->{
//                    System.out.println("Face ID: " + faceMatch.getFace().getFaceId() + ", 유사도: " + faceMatch.getSimilarity());
//                    System.out.println(faceMatch.getFace().getExternalImageId());
//                    
//                    result = faceMatch.getFace().getExternalImageId();
//            });
        	FaceMatch faceMatch = searchFacesByImageResult.getFaceMatches().iterator().next();
        	result = faceMatch.getFace().getExternalImageId();
            
        }
       return result;
	}
	
	public void collectionFaceAdd(byte[] imageBytes, String userBN, String collectionId, String bucket) {
		
		InputStream inputStream = new ByteArrayInputStream(imageBytes);
		
		s3Client.putObject(new PutObjectRequest(bucket, userBN, inputStream, null));
		
		Image image = new Image()
                .withS3Object(new S3Object()
                .withBucket(bucket)
                .withName(userBN));
		
		IndexFacesRequest indexFacesRequest = new IndexFacesRequest()
                .withImage(image)
                .withQualityFilter(QualityFilter.AUTO)
                .withMaxFaces(1)
                .withCollectionId(collectionId)
                .withExternalImageId(userBN)
                .withDetectionAttributes("DEFAULT");

        rekognitionClient.indexFaces(indexFacesRequest);
        
        System.out.println("버킷 저장 성공");
	}
	
	public void collectionAdd() {
		
		String collectionId = "user-face";
//		String collectionId = "blacklist-face";
		
		CreateCollectionRequest createCollectionRequest = new CreateCollectionRequest()
                .withCollectionId(collectionId);
		
		CreateCollectionResult createCollectionResult = rekognitionClient.createCollection(createCollectionRequest);
		
		System.out.println("Collection ARN: " + createCollectionResult.getCollectionArn());
    
	}
	
	public void collectionDelete(){
		String collectionId = "user-face";
//		String collectionId = "blacklist-face";
		
		DeleteCollectionRequest request = new DeleteCollectionRequest()
		         .withCollectionId(collectionId);
		
		rekognitionClient.deleteCollection(request);        
		  
	}
	
	public List<Face> collectionList() {
		
//		ListFacesRequest listFacesRequest = new ListFacesRequest()
//                .withCollectionId("blacklist-face");
		ListFacesRequest listFacesRequest = new ListFacesRequest()
				.withCollectionId("user-face");

        // List faces in the collection
        ListFacesResult listFacesResult = rekognitionClient.listFaces(listFacesRequest);
        
        // Get the list of faces
        List<Face> faces = listFacesResult.getFaces();
      
        if (listFacesResult.getFaces().isEmpty()) {
            System.out.println("No faces found in the collection.");
        }else {
//	        for (Face face : faces) {
//	            System.out.println("Face ID: " + face.getFaceId());
//	            System.out.println("Face Bounding Box: " + face.getBoundingBox());
//	            System.out.println("Face Confidence: " + face.getConfidence());
//	            System.out.println("-------");
//	        }
        }
        return faces;
	}
}
