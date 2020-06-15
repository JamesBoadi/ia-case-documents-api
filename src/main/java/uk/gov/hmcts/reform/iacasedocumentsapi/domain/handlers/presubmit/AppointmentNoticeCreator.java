package uk.gov.hmcts.reform.iacasedocumentsapi.domain.handlers.presubmit;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static uk.gov.hmcts.reform.iacasedocumentsapi.domain.entities.AsylumCaseDefinition.HEARING_DOCUMENTS;
import static uk.gov.hmcts.reform.iacasedocumentsapi.domain.entities.AsylumCaseDefinition.JOURNEY_TYPE;
import static uk.gov.hmcts.reform.iacasedocumentsapi.domain.entities.JourneyType.AIP;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.iacasedocumentsapi.domain.entities.AsylumCase;
import uk.gov.hmcts.reform.iacasedocumentsapi.domain.entities.DocumentTag;
import uk.gov.hmcts.reform.iacasedocumentsapi.domain.entities.JourneyType;
import uk.gov.hmcts.reform.iacasedocumentsapi.domain.entities.ccd.CaseDetails;
import uk.gov.hmcts.reform.iacasedocumentsapi.domain.entities.ccd.Event;
import uk.gov.hmcts.reform.iacasedocumentsapi.domain.entities.ccd.callback.Callback;
import uk.gov.hmcts.reform.iacasedocumentsapi.domain.entities.ccd.callback.PreSubmitCallbackResponse;
import uk.gov.hmcts.reform.iacasedocumentsapi.domain.entities.ccd.callback.PreSubmitCallbackStage;
import uk.gov.hmcts.reform.iacasedocumentsapi.domain.entities.ccd.field.Document;
import uk.gov.hmcts.reform.iacasedocumentsapi.domain.handlers.PreSubmitCallbackHandler;
import uk.gov.hmcts.reform.iacasedocumentsapi.domain.service.DocumentCreator;
import uk.gov.hmcts.reform.iacasedocumentsapi.domain.service.DocumentHandler;

@Component
public class AppointmentNoticeCreator implements PreSubmitCallbackHandler<AsylumCase> {

    private final DocumentCreator<AsylumCase> aipAppointmentDocumentCreator;
    private final DocumentHandler documentHandler;

    public AppointmentNoticeCreator(
        @Qualifier("aipAppointmentNotice") DocumentCreator<AsylumCase> aipAppointmentDocumentCreator,
        DocumentHandler documentHandler
    ) {
        this.aipAppointmentDocumentCreator = aipAppointmentDocumentCreator;
        this.documentHandler = documentHandler;
    }

    public boolean canHandle(
        PreSubmitCallbackStage callbackStage,
        Callback<AsylumCase> callback
    ) {
        requireNonNull(callbackStage, "callbackStage must not be null");
        requireNonNull(callback, "callback must not be null");

        AsylumCase asylumCase = callback.getCaseDetails().getCaseData();

        boolean isAipJourney = asylumCase
            .read(JOURNEY_TYPE, JourneyType.class)
            .map(type -> type == AIP).orElse(false);


        return callbackStage == PreSubmitCallbackStage.ABOUT_TO_SUBMIT
               && isAipJourney
               && asList(Event.LIST_CASE).contains(callback.getEvent());
    }

    public PreSubmitCallbackResponse<AsylumCase> handle(
        PreSubmitCallbackStage callbackStage,
        Callback<AsylumCase> callback
    ) {
        if (!canHandle(callbackStage, callback)) {
            throw new IllegalStateException("Cannot handle callback");
        }

        final CaseDetails<AsylumCase> caseDetails = callback.getCaseDetails();
        requireNonNull(caseDetails, "caseDetails must not be null");
        final AsylumCase asylumCase = caseDetails.getCaseData();
        requireNonNull(asylumCase, "asylumCase must not be null");

        Document appointmentNotice = aipAppointmentDocumentCreator.create(caseDetails);

        documentHandler.addWithMetadataWithoutReplacingExistingDocuments(
            asylumCase,
            appointmentNotice,
            HEARING_DOCUMENTS,
            DocumentTag.HEARING_NOTICE
        );

        return new PreSubmitCallbackResponse<>(asylumCase);
    }
}
