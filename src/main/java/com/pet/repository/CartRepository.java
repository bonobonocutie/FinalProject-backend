package com.pet.repository;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pet.entity.Cart;
import com.pet.entity.Product;

public interface CartRepository extends JpaRepository<Cart, Integer> {

	Optional<Cart> findByProduct_PdIdx(Integer pdIdx);

	@Query("select c from Cart c join fetch c.product where c.sessionId = :sessionId")
	List<Cart> findBySessionId(@Param("sessionId") String sessionId);

	Cart findBySessionIdAndProduct(String sessionId, Product product);

	void deleteBySessionId(String sessionId);


//	@Query("select c from Cart c join fetch c.product where c.cartId = :cartId")
//	List<Cart> findByCartId(@Param("cartId") String cartId);
}
