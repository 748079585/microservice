package com.gt.complaints.dao;

import com.gt.complaints.entity.Picture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PictureDao extends JpaRepository<Picture,Long> {
}
