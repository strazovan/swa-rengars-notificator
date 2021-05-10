package cz.cvut.fel.swa.rengars.notifications.api.components.queue;

import cz.cvut.fel.swa.rengars.notifications.api.components.notificators.Notificator;
import cz.cvut.fel.swa.rengars.notifications.api.components.queue.model.NotificationEntry;
import cz.cvut.fel.swa.rengars.notifications.api.components.queue.service.NotificationService;
import cz.cvut.fel.swa.rengars.notifications.api.model.NotificationStatus;
import cz.cvut.fel.swa.rengars.notifications.api.model.NotificationsConfiguration;
import cz.cvut.fel.swa.rengars.notifications.dao.ConfigurationDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;

@Component
public class NotificationQueueImpl implements NotificationQueue, SmartLifecycle, Runnable, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(NotificationQueueImpl.class);

    private final BlockingQueue<NotificationEntry> messages;
    private final ExecutorService scheduler;
    private final NotificationService service;
    private Map<String, Notificator> notificators = new HashMap<>();
    private ApplicationContext context;
    private volatile boolean running = false;
    private final ConfigurationDao configurationDao;

    public NotificationQueueImpl(NotificationService service, ConfigurationDao configurationDao) {
        this.service = service;
        this.configurationDao = configurationDao;
        this.messages = new PriorityBlockingQueue<>(11, Comparator.comparing(NotificationEntry::getScheduledAt));
        this.scheduler = Executors.newSingleThreadExecutor();
    }

    @Override
    public void start() {
        logger.info(this.getClass().getName() + " notificator starting...");
        this.context.getBeansOfType(Notificator.class)
                .values()
                .forEach(notificator -> this.notificators.put(notificator.getName(), notificator));
        this.scheduler.submit(this);
        this.running = true;
        logger.info(this.getClass().getName() + " notificator has started...");
    }

    @Override
    public void stop() {
        logger.info(this.getClass().getName() + " notificator stopping...");
        this.scheduler.shutdownNow();
        try {
            this.scheduler.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            logger.error("Interrupted while waiting for termination of the scheduler.", ex);
        }
        this.running = false;
        logger.info(this.getClass().getName() + " notificator has stopped...");
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                var entry = this.messages.take();
                final var notification = this.getById(entry.getId()); // get the current notification state
                if (notification.getStatus() == NotificationStatus.CANCELLED)
                    continue;
                if (notification.getScheduledAt().after(new Date())) {
                    this.messages.add(notification);
                    TimeUnit.SECONDS.sleep(10);
                    continue;
                }
                try {
                    final List<NotificationsConfiguration> config = this.configurationDao.findByType(notification.getType());
                    config.forEach(configuration -> this.notificators.get(configuration.getNotificatorName()).processEntry(configuration, notification));
                    notification.setStatus(NotificationStatus.SENT);
                    notification.setSentAt(new Date());
                } catch (Exception ex) {
                    logger.error("Failed to process an notification.", ex);
                    notification.setStatus(NotificationStatus.FAILED);
                }
                this.service.persist(notification);
            }
        } catch (InterruptedException ex) {
            logger.error("The waiting for entry has been interrupted.", ex);
        }
    }


    @Override
    public NotificationEntry queue(NotificationEntry entry) {
        final NotificationEntry persistedEntry = this.service.persist(entry);
        entry.setStatus(NotificationStatus.QUEUED);
        this.messages.add(persistedEntry);
        return persistedEntry;
    }

    @Override
    public List<NotificationEntry> getCurrentQueueState() {
        return Collections.unmodifiableList(this.messages.stream().toList());
    }

    @Override
    public NotificationEntry getById(String id) {
        return this.service.getById(id);
    }

    @Override
    public NotificationEntry cancel(String id) {
        return this.service.cancel(id);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
