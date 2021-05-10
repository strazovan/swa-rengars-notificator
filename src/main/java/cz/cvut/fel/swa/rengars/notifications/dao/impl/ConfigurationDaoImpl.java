package cz.cvut.fel.swa.rengars.notifications.dao.impl;

import cz.cvut.fel.swa.rengars.notifications.api.model.NotificationsConfiguration;
import cz.cvut.fel.swa.rengars.notifications.dao.ConfigurationDao;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ConfigurationDaoImpl implements ConfigurationDao {

    private MongoOperations operations;


    @Override
    public List<NotificationsConfiguration> findAll() {
        return this.operations.findAll(NotificationsConfiguration.class);
    }

    @Override
    public List<NotificationsConfiguration> findByType(String type) {
        final var query = new Query();
        query.addCriteria(Criteria.where("type").is(type));
        return this.operations.find(query, NotificationsConfiguration.class);
    }

    @Autowired
    public void setOperations(MongoOperations operations) {
        this.operations = operations;
    }
}
