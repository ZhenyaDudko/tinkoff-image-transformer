package com.wisestudent;

import com.wisestudent.config.DBConfig;
import com.wisestudent.config.StorageConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(initializers = {DBConfig.Initializer.class, StorageConfig.Initializer.class})
public abstract class AbstractTest {
}
