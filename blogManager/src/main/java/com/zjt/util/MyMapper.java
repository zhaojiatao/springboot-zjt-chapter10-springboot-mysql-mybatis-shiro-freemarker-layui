package com.zjt.util;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * 继承自己的MyMapper
 *
 * @author
 * @since 2017-06-26 21:53
 */
public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T> {
    //FIXME 特别注意，该接口不能被扫描到，否则会出错
    //FIXME 最后在启动类中通过MapperScan注解指定扫描的mapper路径：
}
