package com.dailyselfie.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class SelfieNotFoundException extends RuntimeException {

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 5799787002794264506L;
}
