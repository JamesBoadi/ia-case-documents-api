package uk.gov.hmcts.reform.iacasedocumentsapi.domain.handlers.presubmit;

import static java.util.Objects.requireNonNull;
import static uk.gov.hmcts.reform.iacasedocumentsapi.domain.entities.AsylumCaseDefinition.LEGAL_REPRESENTATIVE_DOCUMENTS;
import static uk.gov.hmcts.reform.iacasedocumentsapi.domain.entities.AsylumCaseDefinition.PA_APPEAL_TYPE_PAYMENT_OPTION;
import static uk.gov.hmcts.reform.iacasedocumentsapi.domain.entities.ccd.field.PaymentStatus.FAILED;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.iacasedocumentsapi.domain.entities.AsylumCase;
import uk.gov.hmcts.reform.iacasedocumentsapi.domain.entities.AsylumCaseDefinition;
import uk.gov.hmcts.reform.iacasedocumentsapi.domain.entities.DocumentTag;
import uk.gov.hmcts.reform.iacasedocumentsapi.domain.entities.ccd.CaseDetails;
import uk.gov.hmcts.reform.iacasedocumentsapi.domain.entities.ccd.Event;
import uk.gov.hmcts.reform.iacasedocumentsapi.domain.entities.ccd.callback.Callback;
import uk.gov.hmcts.reform.iacasedocumentsapi.domain.entities.ccd.callback.PreSubmitCallbackResponse;
import uk.gov.hmcts.reform.iacasedocumentsapi.domain.entities.ccd.callback.PreSubmitCallbackStage;
import uk.gov.hmcts.reform.iacasedocumentsapi.domain.entities.ccd.field.Document;
import uk.gov.hmcts.reform.iacasedocumentsapi.domain.entities.ccd.field.PaymentStatus;
import uk.gov.hmcts.reform.iacasedocumentsapi.domain.handlers.PreSubmitCallbackHandler;
import uk.gov.hmcts.reform.iacasedocumentsapi.domain.service.DocumentCreator;
import uk.gov.hmcts.reform.iacasedocumentsapi.domain.service.DocumentHandler;

@Component
public class AppealSubmissionCreator implements PreSubmitCallbackHandler<AsylumCase> {

    private final DocumentCreator<AsylumCase> appealSubmissionDocumentCreator;
    private final DocumentHandler documentHandler;

    public AppealSubmissionCreator(
        @Qualifier("appealSubmission") DocumentCreator<AsylumCase> appealSubmissionDocumentCreator,
        DocumentHandler documentHandler
    ) {
        this.appealSubmissionDocumentCreator = appealSubmissionDocumentCreator;
        this.documentHandler = documentHandler;
    }

    public boolean canHandle(
        PreSubmitCallbackStage callbackStage,
        Callback<AsylumCase> callback
    ) {
        requireNonNull(callbackStage, "callbackStage must not be null");
        requireNonNull(callback, "callback must not be null");

        final AsylumCase asylumCase =
            callback
                .getCaseDetails()
                .getCaseData();

        boolean paymentFailed =
            asylumCase
                .read(AsylumCaseDefinition.PAYMENT_STATUS, PaymentStatus.class)
                .map(paymentStatus -> paymentStatus == FAILED).orElse(false);

        boolean payLater =
            asylumCase
                .read(PA_APPEAL_TYPE_PAYMENT_OPTION, String.class)
                .map(paymentOption -> paymentOption.equals("payOffline") || paymentOption.equals("payLater")).orElse(false);

        boolean paymentFailedChangedToPayLater = paymentFailed && payLater;

        return callbackStage == PreSubmitCallbackStage.ABOUT_TO_SUBMIT
               && Arrays.asList(
                    Event.SUBMIT_APPEAL,
                    Event.EDIT_APPEAL_AFTER_SUBMIT,
                    Event.PAY_AND_SUBMIT_APPEAL)
                   .contains(callback.getEvent())
               && (!paymentFailed || paymentFailedChangedToPayLater);
    }

    public PreSubmitCallbackResponse<AsylumCase> handle(
        PreSubmitCallbackStage callbackStage,
        Callback<AsylumCase> callback
    ) {
        if (!canHandle(callbackStage, callback)) {
            throw new IllegalStateException("Cannot handle callback");
        }

        final CaseDetails<AsylumCase> caseDetails = callback.getCaseDetails();
        final AsylumCase asylumCase = caseDetails.getCaseData();

        Document appealSubmission = appealSubmissionDocumentCreator.create(caseDetails);

        documentHandler.addWithMetadata(
            asylumCase,
            appealSubmission,
            LEGAL_REPRESENTATIVE_DOCUMENTS,
            DocumentTag.APPEAL_SUBMISSION
        );

        return new PreSubmitCallbackResponse<>(asylumCase);
    }
}
