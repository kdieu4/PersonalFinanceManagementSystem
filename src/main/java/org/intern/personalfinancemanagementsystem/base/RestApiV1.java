package org.intern.personalfinancemanagementsystem.base;

import org.intern.personalfinancemanagementsystem.constant.ApiPath;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RestController(ApiPath.API_V1)
@RequestMapping
public @interface RestApiV1 {
}
