package com.gt.complaints.service.impl;

import com.gt.complaints.dao.ReasonDao;
import com.gt.complaints.entity.Reason;
import com.gt.complaints.service.ReasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author luke
 */
@Service
public class ReasonServiceImpl implements ReasonService {

    @Autowired
    private ReasonDao reasonDao;

    @Override
    public void save(Reason reason) {
        reasonDao.save(reason);
    }

    @Override
    public void update(Reason reason) {
        reasonDao.save(reason);
    }

    @Override
    public List<Reason> findAll() {
        return reasonDao.findByEnable(1);
    }

    @Override
    public void deleteReason(long id) {
        Optional<Reason> optional = reasonDao.findById(id);
        if(optional.isPresent()){
            Reason reason = optional.get();
            reason.setEnable(0);
            reasonDao.save(reason);
        }
    }
}
