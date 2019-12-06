package com.gt.complaints.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 投诉原因表
 * @author luke
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Reason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * 中文原因
     */
    private String reasonZh;

    /**
     * 英文原因
     */
    private String reasonEn;

    /**
     * 是否使用，1：启用，0：未启用，代替delete
     * 默认启用
     */
    private int enable=1;
}
