package com.gt.complaints.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * 用户投诉表
 * @author luke
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * 用户ID
     */
    private long userId;

    /**
     * 投诉原因
     */
    private String reason;

    /**
     * 建议
     */
    private String advice;

//    @JsonIgnore
    /**
     * 图片一对多
     * fetch=FetchType.EAGER :立刻加载
     * 关闭懒加载,在数据库中取值的时候，如果使用懒加载，就会只取出一层节点的数据，然后关闭session
     * 这样再去取下一层级的数据的时候就会报出错误：session is closed
     * 如果是EAGER，那么表示取出这条数据时，它关联的数据也同时取出放入内存中
     * 如果是LAZY，那么取出这条数据时，它关联的数据并不取出来，在同一个session中，什么时候要用，就什么时候取(再次访问数据库)
     */
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "p_id")
    private List<Picture> pictures = new ArrayList<>();;

}
