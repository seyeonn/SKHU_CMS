package com.sofCap.dao;

import java.util.List;

import com.sofCap.dto.AccountDto;
import com.sofCap.model.SemDate;

public interface AccountDao {
	List<AccountDto> findAll();
	List<AccountDto> findByClubId(int club_id);
	List<AccountDto> findBySem(SemDate sem_name);
}