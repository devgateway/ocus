package org.devgateway.ocus.forms.wicket.page;

import java.io.Serializable;

/**
 * @author idobre
 * @since 6/28/16
 */
public class USASpendingImportBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private Boolean dropData = true;

    private Boolean validateData = true;

    public Boolean getDropData() {
        return dropData;
    }

    public void setDropData(Boolean dropData) {
        this.dropData = dropData;
    }

    public Boolean getValidateData() {
        return validateData;
    }

    public void setValidateData(Boolean validateData) {
        this.validateData = validateData;
    }
}
