package fhhgb.mqtt.mqttservice.config;

import fhhgb.mqtt.mqttservice.converter.OffsetDateTimeReadConverter;
import fhhgb.mqtt.mqttservice.converter.OffsetDateTimeWriteConverter;
import org.apache.naming.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.List;

@Configuration
public class MongoDbConfig {

    @Bean
    public MongoCustomConversions mongoCustomConversions(){
        return new MongoCustomConversions(List.of(new OffsetDateTimeWriteConverter(),new OffsetDateTimeReadConverter()));

    }
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
