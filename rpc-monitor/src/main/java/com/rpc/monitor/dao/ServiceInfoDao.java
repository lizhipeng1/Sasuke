package com.rpc.monitor.dao;

import com.rpc.monitor.model.ServiceInfo;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;

@MapperScan
public interface ServiceInfoDao {
    int deleteByPrimaryKey(Integer id);

    int insert(ServiceInfo record);

    int batchInsert(@Param("serviceInfoList") List<ServiceInfo> serviceInfoList);

    int insertSelective(ServiceInfo record);

    ServiceInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ServiceInfo record);

    int updateByPrimaryKey(ServiceInfo record);

    List<ServiceInfo> selectByCondition(ServiceInfo record);
}