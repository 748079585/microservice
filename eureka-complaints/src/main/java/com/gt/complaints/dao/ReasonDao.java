package com.gt.complaints.dao;

import com.gt.complaints.entity.Reason;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReasonDao extends JpaRepository<Reason,Long> {
    /**
     * 通过enable查询，主要查询enable = 1 的
     * @param enable
     * @return
     */
    List<Reason> findByEnable(int enable);
}
