package com.zjt.mapper;

import com.zjt.entity.Trole;
import com.zjt.util.MyMapper;

import java.util.List;

public interface TroleMapper extends MyMapper<Trole> {

    List<Trole> selectRolesByUserId(Integer userid);

}