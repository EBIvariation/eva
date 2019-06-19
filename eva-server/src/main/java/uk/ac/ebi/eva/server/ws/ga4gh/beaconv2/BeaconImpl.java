/*
 * European Variation Archive (EVA) - Open-access database of all types of genetic
 * variation data from all species
 *
 * Copyright 2019 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.eva.server.ws.ga4gh.beaconv2;

import uk.ac.ebi.eva.commons.beacon.models.Beacon;
import uk.ac.ebi.eva.commons.beacon.models.BeaconDataset;
import uk.ac.ebi.eva.commons.beacon.models.BeaconOrganization;

import java.util.List;

public class BeaconImpl extends Beacon {

    static final String ID = "uk.ac.ebi.eva";

    static final String NAME = "European Variation Archive Beacon";

    static final String APIVERSION = "v1.0";

    static final String DESCRIPTION = "descriptionString";

    static final String VERSION = "v2";

    static final String WELCOME_URL = "welcomeUrlString";

    static final String ALTERNATIVE_URL = "alternativeUrlString";

    static final String CREATED_DATE_TIME = "date1";

    static final String UPDATE_DATE_TIME = "date2";

    public BeaconImpl() {
        this.id(ID);
        this.name(NAME);
        this.apiVersion(APIVERSION);
        this.organization(new BeaconOrganizationImpl());
        this.description(DESCRIPTION);
        this.version(VERSION);
        this.welcomeUrl(WELCOME_URL);
        this.alternativeUrl(ALTERNATIVE_URL);
        this.createDateTime(CREATED_DATE_TIME);
        this.updateDateTime(UPDATE_DATE_TIME);
    }
}
