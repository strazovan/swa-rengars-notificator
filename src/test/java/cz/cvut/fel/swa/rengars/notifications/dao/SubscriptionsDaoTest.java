package cz.cvut.fel.swa.rengars.notifications.dao;

import cz.cvut.fel.swa.rengars.notifications.api.model.SubscriptionDocument;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class SubscriptionsDaoTest extends BaseDaoTestRunner {


    private enum TestSubscriptions {
        CONSOLE(new SubscriptionDocument("CONSOLE", "CONSOLE_TEST", null, "test", "Console")),
        WITH_ID(new SubscriptionDocument("WITH_ID", "TEST_EVENT", 1L, "test", "Console"));

        private final SubscriptionDocument subscriptionDocument;

        TestSubscriptions(SubscriptionDocument subscriptionDocument) {
            this.subscriptionDocument = subscriptionDocument;
        }

        public SubscriptionDocument getSubscriptionDocument() {
            return subscriptionDocument;
        }
    }

    @Autowired
    SubscriptionsDao subscriptionsDao;

    @BeforeAll
    public static void beforeAll(@Autowired MongoTemplate mongoTemplate) {
        for (TestSubscriptions value : TestSubscriptions.values()) {
            mongoTemplate.insert(value.getSubscriptionDocument());
        }
    }

    @Test
    public void create_subscription() {
        final var document = new SubscriptionDocument(null, "TEST_EVENT", null, "test", "CreationTest");
        final SubscriptionDocument createdDocument = this.subscriptionsDao.create(document);
        assertNotNull(createdDocument);
        final SubscriptionDocument found = this.subscriptionsDao.findById(createdDocument.getId());
        assertNotNull(found);
        assertEquals(createdDocument.getId(), found.getId());
    }

    @Test
    public void find_for_object_test() {
        final List<SubscriptionDocument> forTypeAndObject = this.subscriptionsDao.findForTypeAndObject(TestSubscriptions.WITH_ID.getSubscriptionDocument().getType(),
                TestSubscriptions.WITH_ID.getSubscriptionDocument().getObjectId());

        assertEquals(2L, forTypeAndObject.size());
    }

    @Test
    public void find_for_type_without_object() {
        final List<SubscriptionDocument> forTypeAndObject = this.subscriptionsDao.findForTypeAndObject(TestSubscriptions.CONSOLE.getSubscriptionDocument().getType(),
                null);

        assertEquals(1L, forTypeAndObject.size());
        assertEquals(TestSubscriptions.CONSOLE.getSubscriptionDocument().getId(), forTypeAndObject.get(0).getId());
    }

}
