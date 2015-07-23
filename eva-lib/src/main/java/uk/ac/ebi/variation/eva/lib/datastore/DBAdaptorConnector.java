package uk.ac.ebi.variation.eva.lib.datastore;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.opencb.datastore.core.config.DataStoreServerAddress;
import org.opencb.opencga.lib.auth.IllegalOpenCGACredentialsException;
import org.opencb.opencga.storage.core.adaptors.StudyDBAdaptor;
import org.opencb.opencga.storage.core.variant.adaptors.VariantDBAdaptor;
import org.opencb.opencga.storage.core.variant.adaptors.VariantSourceDBAdaptor;
import org.opencb.opencga.storage.mongodb.utils.MongoCredentials;
import org.opencb.opencga.storage.mongodb.variant.StudyMongoDBAdaptor;
import org.opencb.opencga.storage.mongodb.variant.VariantMongoDBAdaptor;
import org.opencb.opencga.storage.mongodb.variant.VariantSourceMongoDBAdaptor;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia <cyenyxe@ebi.ac.uk>
 */
public class DBAdaptorConnector {
    
    public static VariantDBAdaptor getVariantDBAdaptor(String species) 
            throws UnknownHostException, IllegalOpenCGACredentialsException, IOException {
        Properties properties = new Properties(); 
        properties.load(DBAdaptorConnector.class.getResourceAsStream("/eva.properties"));
        return new VariantMongoDBAdaptor(getCredentials(species, properties),
                properties.getProperty("eva.mongo.collections.variants"), 
                properties.getProperty("eva.mongo.collections.files"));
    }
    
    public static StudyDBAdaptor getStudyDBAdaptor(String species)
            throws UnknownHostException, IllegalOpenCGACredentialsException, IOException {
        Properties properties = new Properties(); 
        properties.load(DBAdaptorConnector.class.getResourceAsStream("/eva.properties"));
        return new StudyMongoDBAdaptor(getCredentials(species, properties),
                properties.getProperty("eva.mongo.collections.files"));
    }
    
    public static VariantSourceDBAdaptor getVariantSourceDBAdaptor(String species)
            throws UnknownHostException, IllegalOpenCGACredentialsException, IOException {
        Properties properties = new Properties(); 
        properties.load(DBAdaptorConnector.class.getResourceAsStream("/eva.properties"));
        return new VariantSourceMongoDBAdaptor(getCredentials(species, properties),
                properties.getProperty("eva.mongo.collections.files"));
    }
    
    private static MongoCredentials getCredentials(String species, Properties properties)
            throws IllegalOpenCGACredentialsException, IOException {
        if (species == null || species.isEmpty()) {
            throw new IllegalArgumentException("Please specify a species");
        }
        
        String[] hosts = properties.getProperty("eva.mongo.host").split(",");
        List<DataStoreServerAddress> servers = new ArrayList();
        
        // Get the list of hosts (optionally including the port number)
        for (String host : hosts) {
            String[] params = host.split(":");
            if (params.length > 1) {
                servers.add(new DataStoreServerAddress(params[0], Integer.parseInt(params[1])));
            } else {
                servers.add(new DataStoreServerAddress(params[0], 27017));
            }
        }
        
        MongoCredentials credentials = new MongoCredentials(servers,
                "eva_" + species,
                properties.getProperty("eva.mongo.user"),
                properties.getProperty("eva.mongo.passwd"));
        
        // Set authentication database, if specified in the configuration
        credentials.setAuthenticationDatabase(properties.getProperty("eva.mongo.auth.db", null));
        
        return credentials;
    }
    
}
