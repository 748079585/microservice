package com.gt.complaints.service;

import com.gt.complaints.entity.Reason;

import java.util.List;

public interface ReasonService {

    /**
     * 添加reason
     * @param reason
     */
    void save(Reason reason);

    /**
     * 更新reason
     * @param reason
     */
    void update(Reason reason);

    /**
     * 查询所有enable为1 的 reason
     * @return
     */
    List<Reason> findAll();

    /**
     * 设置enable为0
     * @param id
     */
    void deleteReason(long id);
}
