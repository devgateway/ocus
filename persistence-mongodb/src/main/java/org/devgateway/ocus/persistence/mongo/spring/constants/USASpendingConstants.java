package org.devgateway.ocus.persistence.mongo.spring.constants;

import com.google.common.collect.ImmutableMap;
import org.apache.log4j.Logger;
import org.devgateway.ocds.persistence.mongo.Tender;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author idobre
 * @since 7/22/16
 */
public final class USASpendingConstants {
    private static final Logger LOGGER = Logger.getLogger(USASpendingConstants.class);

    private static Map<String, String> naicsCodes;

    private USASpendingConstants() {

    }

    public static final class AwardStatus {
        public static final String ACTIVE = "Active";
    }

    public static final class ExtentCompeted {
        public static final ImmutableMap<String, Tender.ProcurementMethod> EXTENTCOMPETEDMAPPING =
                new ImmutableMap.Builder<String, Tender.ProcurementMethod>()
                        .put("A: FULL AND OPEN COMPETITION", Tender.ProcurementMethod.open)
                        .put("B: NOT AVAILABLE FOR COMPETITION", Tender.ProcurementMethod.limited)
                        .put("C: NOT COMPETED", Tender.ProcurementMethod.limited)
                        .put("D: FULL AND OPEN COMPETITION AFTER EXCLUSION OF SOURCES",
                                Tender.ProcurementMethod.selective)
                        .build();
    }

    public static String getNaicsCodeDescription(String code) {
        // read the codes from the properties file
        if (naicsCodes == null) {
            naicsCodes = new HashMap<>();

            Properties props = new Properties();
            try {
                props.load(USASpendingConstants.class.getResourceAsStream("naics-6-digit-codes.properties"));
            } catch (IOException e) {
                LOGGER.error(e);
            }

            for (Object obj : props.keySet()) {
                String key = obj.toString();
                String description = props.getProperty(key);
                naicsCodes.put(key, description);
            }
        }

        return naicsCodes.get(code);
    }
}
