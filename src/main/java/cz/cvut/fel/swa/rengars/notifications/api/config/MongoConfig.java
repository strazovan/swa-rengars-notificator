package cz.cvut.fel.swa.rengars.notifications.api.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

import java.util.Collection;
import java.util.Collections;

@Configuration
@ComponentScan("cz.cvut.fel.swa.rengars.notifications.dao")
public class MongoConfig extends AbstractMongoClientConfiguration {


    @Value("${configuration.mongo.host}")
    private String mongoHost;
    @Value("${configuration.mongo.port}")
    private int mongoPort;
    @Value("${configuration.mongo.database}")
    private String mongoDatabase;

    @Override
    protected String getDatabaseName() {
        return "notifications";
    }

    @Override
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString("mongodb://" + this.mongoHost + ":" + this.mongoPort + "/" + this.mongoDatabase);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();

        return MongoClients.create(mongoClientSettings);
    }

    @Override
    public Collection getMappingBasePackages() {
        return Collections.singleton("cz.cvut.fel.swa.rengars.notifications.api.model");
    }

}
