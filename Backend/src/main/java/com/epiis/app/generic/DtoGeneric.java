package com.epiis.app.generic;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class DtoGeneric {
    private Date createdAt;
    private Date updatedAt;
}
