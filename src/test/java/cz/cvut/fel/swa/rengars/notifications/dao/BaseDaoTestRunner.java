package cz.cvut.fel.swa.rengars.notifications.dao;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@ComponentScan(basePackages = {"cz.cvut.fel.swa.rengars.notifications.dao"})
@DataMongoTest
public abstract class BaseDaoTestRunner {
}
