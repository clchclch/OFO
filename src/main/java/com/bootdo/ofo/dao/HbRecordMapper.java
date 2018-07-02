package com.bootdo.ofo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.bootdo.ofo.domain.HbRecord;
import com.bootdo.ofo.domain.HbRecordExample;

public interface HbRecordMapper {
    int countByExample(HbRecordExample example);

    int deleteByExample(HbRecordExample example);

    int deleteByPrimaryKey(Integer recordId);

    int insert(HbRecord record);

    int insertSelective(HbRecord record);

    List<HbRecord> selectByExample(HbRecordExample example);

    HbRecord selectByPrimaryKey(Integer recordId);

    int updateByExampleSelective(@Param("record") HbRecord record, @Param("example") HbRecordExample example);

    int updateByExample(@Param("record") HbRecord record, @Param("example") HbRecordExample example);

    int updateByPrimaryKeySelective(HbRecord record);

    int updateByPrimaryKey(HbRecord record);
}