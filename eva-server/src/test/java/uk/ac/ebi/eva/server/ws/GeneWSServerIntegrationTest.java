/*
 * Copyright 2017 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.eva.server.ws;

import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.mongodb.MongoDbConfigurationBuilder;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import uk.ac.ebi.eva.commons.core.models.Annotation;
import uk.ac.ebi.eva.commons.core.models.ws.VariantSourceEntryWithSampleNames;
import uk.ac.ebi.eva.commons.core.models.ws.VariantWithSamplesAndAnnotation;
import uk.ac.ebi.eva.commons.mongodb.services.VariantWithSamplesAndAnnotationsService;
import uk.ac.ebi.eva.lib.Profiles;
import uk.ac.ebi.eva.server.configuration.MongoRepositoryTestConfiguration;
import uk.ac.ebi.eva.server.test.rule.FixSpringMongoDbRule;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(MongoRepositoryTestConfiguration.class)
@UsingDataSet(locations = {
        "/test-data/variants.json",
        "/test-data/files.json",
        "/test-data/annotations.json",
        "/test-data/annotation_metadata.json"
})
@ActiveProfiles(Profiles.TEST_MONGO_FACTORY)
public class GeneWSServerIntegrationTest {

    private static final String TEST_DB = "test-db";

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private VariantWithSamplesAndAnnotationsService service;

    @Autowired
    MongoDbFactory mongoDbFactory;

    @Rule
    public MongoDbRule mongoDbRule = new FixSpringMongoDbRule(
            MongoDbConfigurationBuilder.mongoDb().databaseName(TEST_DB).build());


    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testGetVariantsByGene() throws URISyntaxException {
        testGetVariantsByGeneHelper("SH3YL1", 1);
    }

    @Test
    public void testGetVariantsByGenes() throws URISyntaxException {
        testGetVariantsByGeneHelper("SH3YL1,DDX11L5", 2);
    }

    @Test
    public void testGetVariantsByNonExistingGene() throws URISyntaxException {
        testGetVariantsByGeneHelper("ABC", 0);
    }

    private void testGetVariantsByGeneHelper(String testGene, int expectedVariants) throws URISyntaxException {
        List<VariantWithSamplesAndAnnotation> results = geneWsHelper(testGene);
        WSTestHelpers.checkVariantsInFullResults(results, expectedVariants);
    }

    private List<VariantWithSamplesAndAnnotation> geneWsHelper(String testGene) {
        String url = "/v1/genes/" + testGene + "/variants?species=mmusculus_grcm38";
        return WSTestHelpers.testRestTemplateHelper(url, restTemplate);
    }

    @Test
    public void testExcludeSourceEntries() {
        String testGene = "SH3YL1";
        String testExclusion = "sourceEntries";
        List<VariantWithSamplesAndAnnotation> results = testExcludeHelper(testGene, testExclusion);
        for (VariantWithSamplesAndAnnotation variant : results) {
            for (VariantSourceEntryWithSampleNames sourceEntry : variant.getSourceEntries()) {
                assertTrue(sourceEntry.getCohortStats().isEmpty());
            }
        }
    }

    private List<VariantWithSamplesAndAnnotation> testExcludeHelper(String testGene, String testExclusion) {
        String url = "/v1/genes/" + testGene + "/variants?species=mmusculus_grcm38&exclude=" + testExclusion;
        return WSTestHelpers.testRestTemplateHelper(url, restTemplate);
    }

    @Test
    public void testVepVersionAndVepCacheVersionFilter() {
        String testGene = "DDX11L5";
        String annotationVepVersion = "78";
        String annotationVepCacheversion = "78";
        String url = "/v1/genes/" + testGene +
                "/variants?species=mmusculus_grcm38&annot-vep-version=" + annotationVepVersion +
                "&annot-vep-cache-version=" + annotationVepCacheversion;
        List<VariantWithSamplesAndAnnotation> variants = WSTestHelpers.testRestTemplateHelper(url, restTemplate);
        for (VariantWithSamplesAndAnnotation variant : variants) {
            Annotation annotation = variant.getAnnotation();
            assertEquals(annotationVepVersion, annotation.getVepVersion());
            assertEquals(annotationVepCacheversion, annotation.getVepCacheVersion());
        }
    }

}
