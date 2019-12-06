package com.gt.complaints.dao;

import com.gt.complaints.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintsDao extends JpaRepository<Complaint,Long> {

    /**
     * 查询用户的投诉信息
     * @param userId
     * @return
     */
    List<Complaint> findByUserId(long userId);
}
