package com.app;

import com.app.config.DBConfig;
import com.app.config.KafkaConfig;
import com.app.config.StorageConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(initializers = {DBConfig.Initializer.class, StorageConfig.Initializer.class,
        KafkaConfig.Initializer.class})
public abstract class AbstractTest {
}
