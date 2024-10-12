package com.pet.service;

import java.io.IOException;
import java.util.List;

import com.google.zxing.WriterException;

public interface QrService {
	String generateQRCodeImage(String text, int width, int height, String s3Key)
			throws WriterException, IOException;

	void generateQRCodesForProducts(List<Integer> pdIdxList);
}
