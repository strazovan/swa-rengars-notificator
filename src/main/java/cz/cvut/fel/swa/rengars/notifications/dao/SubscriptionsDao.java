package cz.cvut.fel.swa.rengars.notifications.dao;

import cz.cvut.fel.swa.rengars.notifications.api.model.SubscriptionDocument;

import java.util.List;
import java.util.Optional;

public interface SubscriptionsDao {
    List<SubscriptionDocument> findForTypeAndObject(String type, Long id);

    SubscriptionDocument create(SubscriptionDocument subscription);

    Optional<SubscriptionDocument> delete(String id);
}
