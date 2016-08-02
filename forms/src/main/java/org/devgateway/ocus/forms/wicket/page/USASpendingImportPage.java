package org.devgateway.ocus.forms.wicket.page;

import java.io.File;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.ValidationError;
import org.devgateway.ocds.persistence.mongo.reader.XMLFile;
import org.devgateway.ocds.persistence.mongo.repository.ReleaseRepository;
import org.devgateway.ocus.forms.wicket.components.LogLabel;
import org.devgateway.toolkit.forms.security.SecurityConstants;
import org.devgateway.toolkit.forms.wicket.components.form.CheckBoxToggleBootstrapFormComponent;
import org.devgateway.toolkit.forms.wicket.events.EditingDisabledEvent;
import org.devgateway.toolkit.forms.wicket.page.BasePage;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.wicketstuff.annotation.mount.MountPath;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapForm;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.ladda.LaddaAjaxButton;
import nl.dries.wicket.hibernate.dozer.DozerModel;

/**
 * @author idobre
 * @since 6/28/16
 */
@AuthorizeInstantiation(SecurityConstants.Roles.ROLE_ADMIN)
@MountPath("/importusaspending")
public class USASpendingImportPage extends BasePage {
    private static final Logger LOGGER = Logger.getLogger(USASpendingImportPage.class);

    private static final long serialVersionUID = 1L;

    @SpringBean
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @SpringBean
    private XMLFile usaSpendingXMLImport;

    private LogLabel logText;

    private BootstrapForm<USASpendingImportBean> importForm;

    private LaddaAjaxButton importButton;

    private TransparentWebMarkupContainer importContainer;

    private WebMarkupContainer spinner;

    private CheckBoxToggleBootstrapFormComponent dropData;

    private CheckBoxToggleBootstrapFormComponent validateData;

    /**
     * Construct.
     *
     * @param parameters current page parameters
     */
    public USASpendingImportPage(final PageParameters parameters) {
        super(parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        usaSpendingXMLImport.newMsgBuffer();

        addForm();
        addImportButton();
        addLogText();
        addDropData();
        addValidateData();

        switchFieldsBasedOnExecutorAvailability();
    }

    private void addForm() {
        importForm = new BootstrapForm<>("form",
                new CompoundPropertyModel<>(new DozerModel<>(new USASpendingImportBean())));
        importForm.setOutputMarkupId(true);
        add(importForm);
    }

    private void addDropData() {
        dropData = new CheckBoxToggleBootstrapFormComponent("dropData");
        importForm.add(dropData);
    }

    private void addValidateData() {
        validateData = new CheckBoxToggleBootstrapFormComponent("validateData");
        importForm.add(validateData);
    }

    private void addImportButton() {
        importButton = new LaddaAjaxButton("import", Buttons.Type.Danger) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
                send(getPage(), Broadcast.BREADTH, new EditingDisabledEvent());
                logText.getSelfUpdatingBehavior().restart(target);
                importContainer.setVisibilityAllowed(true);
                target.add(form);

                try {
                    usaSpendingXMLImport.process(new File("/opt/Data_Feed.xml"),
                            importForm.getModelObject().getDropData(), importForm.getModelObject().getValidateData());
                } catch (Exception e) {
                    logger.error(e);
                } finally {
                    this.setEnabled(false);
                    target.add(this);
                }

            }

            @Override
            protected void onError(final AjaxRequestTarget target, final Form<?> form) {
                ValidationError error = new ValidationError();
                error.addKey("formHasErrors");
                error(error);

                target.add(form);
                target.add(feedbackPanel);
            }
        };
        importButton.setLabel(new ResourceModel("startImportProcess"));
        importButton.setIconType(FontAwesomeIconType.hourglass_start);
        importForm.add(importButton);
    }

    private void addLogText() {
        importContainer = new TransparentWebMarkupContainer("importContainer");
        importContainer.setOutputMarkupId(true);
        importForm.add(importContainer);

        AbstractReadOnlyModel<String> logTextModel = new AbstractReadOnlyModel<String>() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getObject() {
                return usaSpendingXMLImport.getMsgBuffer().toString();
            }
        };

        logText = new LogLabel("logText", logTextModel) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onPostProcessTarget(final AjaxRequestTarget target) {
                if (threadPoolTaskExecutor.getActiveCount() == 0) {
                    getSelfUpdatingBehavior().stop(target);
                    spinner.setVisibilityAllowed(false);
                    target.add(spinner);
                }
            }
        };
        importContainer.add(logText);

        spinner = new WebMarkupContainer("spinner");
        spinner.setOutputMarkupId(true);
        importContainer.add(spinner);
    }

    private void switchFieldsBasedOnExecutorAvailability() {
        boolean enabled = threadPoolTaskExecutor.getActiveCount() == 0;

        importContainer.setVisibilityAllowed(!enabled);
        importButton.setEnabled(enabled);
        dropData.setEnabled(enabled);
        validateData.setEnabled(enabled);
    }
}
