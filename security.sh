#!/usr/bin/env bash
export TEST_URL=http://ia-case-documents-api-aat.service.core-compute-aat.internal
echo ${TEST_URL}
zap-api-scan.py -t ${TEST_URL}/v3/api-docs -f openapi -S -d -u ${SecurityRules} -P 1001 -l FAIL
curl --fail http://0.0.0.0:1001/OTHER/core/other/jsonreport/?formMethod=GET --output report.json
export LC_ALL=C.UTF-8
export LANG=C.UTF-8
zap-cli --zap-url http://0.0.0.0 -p 1001 report -o /zap/api-report.html -f html
zap-cli --zap-url http://0.0.0.0 -p 1001 alerts -l Informational --exit-code False
mkdir -p functional-output/
cp /zap/api-report.html functional-output/
cp *.* functional-output/
chmod -R 777 functional-output/
