package com.pet.repository;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pet.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	@Query("select u from User u " +
			"WHERE userEmail = :userEmail AND userPw = :userPw")
	public User authenticate(@Param("userEmail") String userEmail, @Param("userPw") String userPw);

	public User findByuserEmail(String userEmail);

	public User findByuserIdx(Integer userIdx);
	
	public User findByUserBN(String userBN);

//	public User save(User user);
}
