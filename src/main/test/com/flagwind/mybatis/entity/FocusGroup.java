package com.flagwind.mybatis.entity;/**
 * @description: 关注分组
 * @author chendb
 * @date 2020-04-15 15:36:56
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * @description: 关注分组
 *
 * @author chendb
 * @date 2020-04-15 15:36:56
 */
@Data
@Entity
@Table(name = "esta_focus_group")
public class FocusGroup {

    @Id
    @Column(name = "id")
    private String id;


    @Column(name = "name")
    private String name;

    @Column(name = "fontColor")
    private String fontColor;


    @Column(name = "backgroupColor")
    private String backgroupColor;



    @Column(name = "creator")
    private String creator;


    @Column(name = "createTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp createTime;


    @Column(name = "tenantId")
    private String tenantId;
}
