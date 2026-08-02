package com.epiis.app.generic;

import java.sql.Timestamp;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class EntityGeneric {
    @Column(name = "\"createdAt\"")
    private Timestamp createdAt;

    @Column(name = "\"updatedAt\"")
    private Timestamp updatedAt;
}