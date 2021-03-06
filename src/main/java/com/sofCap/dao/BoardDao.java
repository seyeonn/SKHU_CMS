package com.sofCap.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.sofCap.dto.BoardDto;
import com.sofCap.model.Pagination;
import com.sofCap.model.SemDate;

public interface BoardDao {
	BoardDto findById(int id);
	BoardDto findOneClub(int id);
//	List<BoardDto> findBySem_m(SemDate sem_name);
	List<BoardDto> findBySem_m(SemDate sem_name, Pagination pagination);
	List<BoardDto> findBySem_a(SemDate sem_name, int club_id, Pagination pagination);
	List<BoardDto> findByClubId_p(int club_id, Pagination pagination);
	List<BoardDto> findByClubId_r(int club_id,Pagination pagination);
	List<BoardDto> findByClubId_n(int club_id, Pagination pagination);
	List<BoardDto> findAll_p();
	List<BoardDto> findAll_r();
	List<BoardDto> findAll_n(Pagination pagination);
	List<BoardDto> findAll_m();
	BoardDto findOne(@Param("id") int id);
	List<BoardDto> listFive_p();
	List<BoardDto> listFive_r();
	void delete(int id);
	void insert(BoardDto board);
	void update(BoardDto board);
}