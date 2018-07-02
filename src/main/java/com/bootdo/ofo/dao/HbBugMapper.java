package com.bootdo.ofo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.bootdo.ofo.domain.HbBug;
import com.bootdo.ofo.domain.HbBugExample;

public interface HbBugMapper {
    int countByExample(HbBugExample example);

    int deleteByExample(HbBugExample example);

    int deleteByPrimaryKey(Integer bugId);

    int insert(HbBug record);

    int insertSelective(HbBug record);

    List<HbBug> selectByExample(HbBugExample example);

    HbBug selectByPrimaryKey(Integer bugId);

    int updateByExampleSelective(@Param("record") HbBug record, @Param("example") HbBugExample example);

    int updateByExample(@Param("record") HbBug record, @Param("example") HbBugExample example);

    int updateByPrimaryKeySelective(HbBug record);

    int updateByPrimaryKey(HbBug record);
}