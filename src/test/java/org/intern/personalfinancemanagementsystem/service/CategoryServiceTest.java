package org.intern.personalfinancemanagementsystem.service;

import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;
import org.intern.personalfinancemanagementsystem.repository.CategoryRepository;
import org.intern.personalfinancemanagementsystem.service.impl.CategoryServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class CategoryServiceTest {
    @Mock
    UserService userService;
    @Mock
    CategoryRepository categoryRepository;
    @InjectMocks
    CategoryServiceImpl categoryService;
}
