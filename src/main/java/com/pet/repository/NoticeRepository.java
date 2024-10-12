package com.pet.repository;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pet.dto.NoticeDTO;
import com.pet.entity.Notice;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Integer> {

	@Query("select new com.pet.dto.NoticeDTO(n.ntIdx, n.title, n.body, n.createdAt, n.readStatus, n.user.userIdx) " +
			"from Notice n " +
			"where n.user.userIdx = :userIdx ORDER BY  n.createdAt DESC")
	public List<NoticeDTO> findAllNotice(@Param("userIdx") Integer userIdx);
}
