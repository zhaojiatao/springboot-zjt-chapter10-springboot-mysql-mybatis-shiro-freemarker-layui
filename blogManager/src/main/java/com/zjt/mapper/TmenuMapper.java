package com.zjt.mapper;

import com.zjt.entity.Tmenu;
import com.zjt.util.MyMapper;

import java.util.HashMap;
import java.util.List;

public interface TmenuMapper extends MyMapper<Tmenu> {

    List<Tmenu> selectMenusByRoleId(Integer roleid);

    List<Tmenu> selectByParentIdAndRoleId(HashMap<String,Object> paraMap);

}