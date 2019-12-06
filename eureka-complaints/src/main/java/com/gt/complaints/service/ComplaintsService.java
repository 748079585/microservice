package com.gt.complaints.service;

import com.gt.complaints.entity.Complaint;

import java.util.List;

public interface ComplaintsService {

    /**
     * 保存投诉信息
     * @param complaints
     * @param picPaths
     */
    void save(Complaint complaints, List<String> picPaths);

    /**
     * 查询所有投诉信息
     * @return
     */
    List<Complaint> findAll();

    /**
     * 查询某用户的投诉信息
     * @param userId  用户ID
     * @return
     */
    List<Complaint> findByUserId(long userId);

    /**
     * 删除投诉信息
     * @param id  投诉信息ID
     */
    void delete(long id);
}
