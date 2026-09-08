package com.ning.ningaicodemother.request.apprequest;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class AppSaveRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String initPrompt;

}
