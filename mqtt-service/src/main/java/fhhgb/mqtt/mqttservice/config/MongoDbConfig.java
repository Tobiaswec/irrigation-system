package fhhgb.mqtt.mqttservice.config;

import fhhgb.mqtt.mqttservice.converter.OffsetDateTimeReadConverter;
import fhhgb.mqtt.mqttservice.converter.OffsetDateTimeWriteConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.List;

@Configuration
public class MongoDbConfig {

    /**
     * set custom MongoFB type converters
     */
    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(List.of(new OffsetDateTimeWriteConverter(), new OffsetDateTimeReadConverter()));

    }

    /**
     * Set custom converter for MongoDB and remove column class by setting the type mapper to null
     */
    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoDatabaseFactory databaseFactory,
                                                       MongoCustomConversions customConversions, MongoMappingContext mappingContext) {

        DbRefResolver dbRefResolver = new DefaultDbRefResolver(databaseFactory);
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mappingContext);
        converter.setCustomConversions(customConversions);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        converter.setCodecRegistryProvider(databaseFactory);
        return converter;
    }

}
