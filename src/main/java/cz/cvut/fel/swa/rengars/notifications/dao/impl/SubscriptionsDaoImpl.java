package cz.cvut.fel.swa.rengars.notifications.dao.impl;

import cz.cvut.fel.swa.rengars.notifications.api.model.SubscriptionDocument;
import cz.cvut.fel.swa.rengars.notifications.dao.SubscriptionsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SubscriptionsDaoImpl implements SubscriptionsDao {

    private MongoOperations operations;

    @Override
    public List<SubscriptionDocument> findForTypeAndObject(String type, Long objectId) {
        final var query = new Query();
        final var criteria = new Criteria().andOperator(
                Criteria.where("type").is(type),
                new Criteria().orOperator(
                        Criteria.where("objectId").is(null),
                        Criteria.where("objectId").is(objectId)));
        query.addCriteria(criteria);
        return this.operations.find(query, SubscriptionDocument.class);
    }

    @Override
    public SubscriptionDocument create(SubscriptionDocument subscription) {
        return operations.insert(subscription);
    }

    @Override
    public Optional<SubscriptionDocument> delete(String id) {

        final Query query = new Query(Criteria.where("id").is(id));
        final List<SubscriptionDocument> documents = this.operations.find(query, SubscriptionDocument.class);
        if (documents.size() > 1) {
            throw new IllegalStateException("More documents have the same id."); // this should not happen
        }
        if (documents.isEmpty())
            return Optional.empty();
        this.operations.remove(documents.get(0));
        return Optional.of(documents.get(0));

    }

    @Autowired
    public void setOperations(MongoOperations operations) {
        this.operations = operations;
    }
}
