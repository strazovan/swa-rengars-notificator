package cz.cvut.fel.swa.rengars.notifications.dao;

import cz.cvut.fel.swa.rengars.notifications.api.model.NotificationsConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigurationDaoTest extends BaseDaoTestRunner {

    private enum TestConfigurations {
        CONSOLE(new NotificationsConfiguration(null, "CONSOLE", "CONSOLE_NOTIFICATOR", Collections.singletonMap("key", "value"), "template"));

        private final NotificationsConfiguration configuration;

        TestConfigurations(NotificationsConfiguration configuration) {
            this.configuration = configuration;
        }

        public NotificationsConfiguration getConfiguration() {
            return configuration;
        }
    }

    @Autowired
    ConfigurationDao configurationDao;

    @BeforeAll
    public static void beforeAll(@Autowired MongoTemplate mongoTemplate) {
        for (TestConfigurations value : TestConfigurations.values()) {
            mongoTemplate.insert(value.getConfiguration());
        }
    }

    @Test
    public void find_all_returns_all_configurations() {
        final List<NotificationsConfiguration> allConfigurations = this.configurationDao.findAll();
        assertEquals(TestConfigurations.values().length, allConfigurations.size());
    }

    @Test
    public void find_by_type_returns_correct_type() {
        final List<NotificationsConfiguration> found = this.configurationDao.findByType(TestConfigurations.CONSOLE.getConfiguration().getType());
        assertEquals(1, found.size());
        assertEquals(TestConfigurations.CONSOLE.getConfiguration().getType(), found.get(0).getType());
    }
}
