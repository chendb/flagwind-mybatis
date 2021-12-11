package com.flagwind.mybatis.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @description: 观注事项
 *
 * @author chendb
 * @date 2020-04-15 15:35:18
 */
@Data
@Entity
@Table(name = "esta_focus_item")
public class FocusItem {



        @Id
        @Column(name = "id")
        private String id;


        @Column(name = "groupId")
        private String focusGroupId;


        @Column(name = "instanceId")
        private String testInstanceId;


        @Column(name = "creator")
        private String testcreator;


        @Column(name = "createTime")
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Timestamp testcreateTime;


        @Column(name = "tenantId")
        private String tenantId;


        @OneToOne(targetEntity = FocusGroup.class,mappedBy = "id")
        @JoinColumn(name = "groupId")
        private FocusGroup group;



}
