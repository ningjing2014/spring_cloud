package com.ln.xproject.job.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.ln.xproject.base.model.BaseModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by ning on 1/10/18.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "job")
public class Job extends BaseModel {

    private static final long serialVersionUID = 6377064610741933508L;

    private String machineStatus;

    private Byte jobType;

    private Date jobStartTime;

    private Byte jobStatus;

    private Long lastJobId;

    private Integer executeTimes;

    private Integer maxExecuteTimes;

    private String lastError;

    private Long primaryKey;

    private String jobParam;

}
