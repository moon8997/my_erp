package com.myproject.caseNara.mapper;

import com.myproject.caseNara.model.Account;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper {
    int insertAccount(Account account);
    Account findById(String id);
    int countById(String id);
}
