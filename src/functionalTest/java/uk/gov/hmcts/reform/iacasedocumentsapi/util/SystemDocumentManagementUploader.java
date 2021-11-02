package uk.gov.hmcts.reform.iacasedocumentsapi.util;

import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.util.Collections;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.reform.ccd.document.am.feign.CaseDocumentClientApi;
import uk.gov.hmcts.reform.ccd.document.am.model.DocumentUploadRequest;
import uk.gov.hmcts.reform.ccd.document.am.model.UploadResponse;
import uk.gov.hmcts.reform.ccd.document.am.util.InMemoryMultipartFile;
import uk.gov.hmcts.reform.iacasedocumentsapi.domain.entities.ccd.field.Document;

@Service
public class SystemDocumentManagementUploader {

    private final CaseDocumentClientApi caseDocumentClientApi;
    private final AuthorizationHeadersProvider authorizationHeadersProvider;

    public SystemDocumentManagementUploader(
        CaseDocumentClientApi caseDocumentClientApi,
        AuthorizationHeadersProvider authorizationHeadersProvider
    ) {
        this.caseDocumentClientApi = caseDocumentClientApi;
        this.authorizationHeadersProvider = authorizationHeadersProvider;
    }

    public Document upload(
        Resource resource,
        String classification,
        String caseTypeId,
        String jurisdictionId,
        String contentType
    ) {
        final String serviceAuthorizationToken =
            authorizationHeadersProvider
                .getLegalRepresentativeAuthorization()
                .getValue("ServiceAuthorization");

        final String accessToken =
            authorizationHeadersProvider
                .getLegalRepresentativeAuthorization()
                .getValue("Authorization");

        try {

            MultipartFile file = new InMemoryMultipartFile(
                resource.getFilename(),
                resource.getFilename(),
                contentType,
                ByteStreams.toByteArray(resource.getInputStream())
            );

            DocumentUploadRequest request = new DocumentUploadRequest(classification,
                caseTypeId,jurisdictionId,Collections.singletonList(file));

            UploadResponse uploadResponse =
                caseDocumentClientApi
                    .uploadDocuments(
                        accessToken,
                        serviceAuthorizationToken,
                        request
                    );

            uk.gov.hmcts.reform.ccd.document.am.model.Document uploadedDocument =  uploadResponse
                .getDocuments()
                .get(0);

            return new Document(
                uploadedDocument
                    .links
                    .self
                    .href,
                uploadedDocument
                    .links
                    .binary
                    .href,
                uploadedDocument
                    .originalDocumentName,
                uploadedDocument
                    .hashToken
            );

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
