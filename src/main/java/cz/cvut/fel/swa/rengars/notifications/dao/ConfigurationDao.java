package cz.cvut.fel.swa.rengars.notifications.dao;

import cz.cvut.fel.swa.rengars.notifications.api.model.NotificationsConfiguration;

import java.util.List;

public interface ConfigurationDao {
    List<NotificationsConfiguration> findAll();

    List<NotificationsConfiguration> findByType(String type);
}
