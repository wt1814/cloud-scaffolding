package com.wuw.ucenter.server.dao;

import com.wuw.ucenter.api.model.DO.UserAccountDO;

public interface UserAccountMapper {

    int deleteByPrimaryKey(String id);

    int insert(UserAccountDO record);

    int insertSelective(UserAccountDO record);

    UserAccountDO selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(UserAccountDO record);

    int updateByPrimaryKey(UserAccountDO record);

}