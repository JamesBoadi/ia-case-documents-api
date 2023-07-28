package uk.gov.hmcts.reform.iacasedocumentsapi.domain.entities;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;
import uk.gov.hmcts.reform.iacasedocumentsapi.domain.entities.ccd.CaseType;

public enum DocumentTag {

    CASE_ARGUMENT("caseArgument", CaseType.ASYLUM),
    RESPONDENT_EVIDENCE("respondentEvidence", CaseType.ASYLUM),
    APPEAL_RESPONSE("appealResponse", CaseType.ASYLUM),
    APPEAL_SUBMISSION("appealSubmission", CaseType.ASYLUM),
    INTERNAL_APPEAL_SUBMISSION("internalAppealSubmission", CaseType.ASYLUM),
    ADDITIONAL_EVIDENCE("additionalEvidence", CaseType.ASYLUM),
    HEARING_REQUIREMENTS("hearingRequirements", CaseType.ASYLUM),
    HEARING_NOTICE("hearingNotice", CaseType.ASYLUM),
    REHEARD_HEARING_NOTICE("reheardHearingNotice", CaseType.ASYLUM),
    ADA_SUITABILITY("adaSuitability", CaseType.ASYLUM),
    INTERNAL_ADA_SUITABILITY("internalAdaSuitability", CaseType.ASYLUM),
    CASE_SUMMARY("caseSummary", CaseType.ASYLUM),
    HEARING_BUNDLE("hearingBundle", CaseType.ASYLUM),
    ADDENDUM_EVIDENCE("addendumEvidence", CaseType.ASYLUM),
    DECISION_AND_REASONS_DRAFT("decisionAndReasons", CaseType.ASYLUM),
    REHEARD_DECISION_AND_REASONS_DRAFT("reheardDecisionAndReasons", CaseType.ASYLUM),
    DECISION_AND_REASONS_COVER_LETTER("decisionAndReasonsCoverLetter", CaseType.ASYLUM),
    INTERNAL_DET_DECISION_AND_REASONS_LETTER("internalDetDecisionAndReasonsLetter", CaseType.ASYLUM),
    FINAL_DECISION_AND_REASONS_PDF("finalDecisionAndReasonsPdf", CaseType.ASYLUM),
    APPEAL_SKELETON_BUNDLE("submitCaseBundle", CaseType.ASYLUM),
    END_APPEAL("endAppeal", CaseType.ASYLUM),
    END_APPEAL_AUTOMATICALLY("endAppealAutomatically", CaseType.ASYLUM),
    CMA_REQUIREMENTS("cmaRequirements", CaseType.ASYLUM),
    CMA_NOTICE("cmaNotice", CaseType.ASYLUM),
    HO_DECISION_LETTER("homeOfficeDecisionLetter", CaseType.ASYLUM),
    FTPA_APPELLANT("ftpaAppellant", CaseType.ASYLUM),
    FTPA_RESPONDENT("ftpaRespondent", CaseType.ASYLUM),
    FTPA_DECISION_AND_REASONS("ftpaDecisionAndReasons", CaseType.ASYLUM),
    RECORD_OUT_OF_TIME_DECISION_DOCUMENT("recordOutOfTimeDecisionDocument", CaseType.ASYLUM),
    UPPER_TRIBUNAL_BUNDLE("upperTribunalBundle", CaseType.ASYLUM),
    APPEAL_REASONS("appealReasons", CaseType.ASYLUM),
    CLARIFYING_QUESTIONS("clarifyingQuestions", CaseType.ASYLUM),
    APPEAL_FORM("appealForm", CaseType.ASYLUM),
    NOTICE_OF_DECISION_UT_TRANSFER("noticeOfDecisionUtTransfer", CaseType.ASYLUM),

    REQUEST_CASE_BUILDING("requestCaseBuilding", CaseType.ASYLUM),
    REQUEST_RESPONDENT_REVIEW("requestRespondentReview", CaseType.ASYLUM),
    UPLOAD_THE_APPEAL_RESPONSE("uploadTheAppealResponse", CaseType.ASYLUM),

    HEARING_BUNDLE_READY_LETTER("hearingBundleReadyLetter", CaseType.ASYLUM),
    BAIL_SUBMISSION("bailSubmission", CaseType.BAIL),
    BAIL_EVIDENCE("uploadTheBailEvidenceDocs", CaseType.BAIL),
    BAIL_DECISION_UNSIGNED("bailDecisionUnsigned", CaseType.BAIL),
    BAIL_END_APPLICATION("bailEndApplication", CaseType.BAIL),
    SIGNED_DECISION_NOTICE("signedDecisionNotice", CaseType.BAIL),
    UPLOAD_DOCUMENT("uploadDocument", CaseType.BAIL),
    BAIL_SUMMARY("uploadBailSummary", CaseType.BAIL),
    B1_DOCUMENT("b1Document", CaseType.BAIL),

    @JsonEnumDefaultValue
    NONE("", CaseType.UNKNOWN);


    @JsonValue
    private final String id;
    private final CaseType caseType;

    DocumentTag(String id, CaseType caseType) {
        this.id = id;
        this.caseType = caseType;
    }

    public CaseType getCaseType() {
        return caseType;
    }

    @Override
    public String toString() {
        return id;
    }
}

