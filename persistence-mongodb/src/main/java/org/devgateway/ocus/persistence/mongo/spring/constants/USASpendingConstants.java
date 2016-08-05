package org.devgateway.ocus.persistence.mongo.spring.constants;

import com.google.common.collect.ImmutableMap;
import org.devgateway.ocds.persistence.mongo.Tender;

/**
 * @author idobre
 * @since 7/22/16
 */
public final class USASpendingConstants {
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
}
