package com.pet.service;

import java.util.List;

import com.pet.dto.CartDTO;

public interface CartService {
	public int cartAdd(String sessionId, String qrCodeText);
	public List<CartDTO> findAllCart(String sessionId);
	public int updateCart(String sessionId, List<CartDTO> updates);
	public void deleteCartItem(String sessionId, Integer pdIdx);
	public void deleteCartBySessionId(String sessionId);
}
