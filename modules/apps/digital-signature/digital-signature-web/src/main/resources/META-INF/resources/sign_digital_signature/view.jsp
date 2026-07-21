<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String digitalSignatureRecipientStatus = GetterUtil.getString(request.getAttribute(DigitalSignatureWebKeys.DIGITAL_SIGNATURE_RECIPIENT_STATUS));

Object digitalSignatureSigningConfig = request.getAttribute(DigitalSignatureWebKeys.DIGITAL_SIGNATURE_SIGNING_CONFIG);
%>

<c:choose>
	<c:when test='<%= StringUtil.equalsIgnoreCase("completed", digitalSignatureRecipientStatus) %>'>
		<div class="alert alert-info" role="alert">
			<liferay-ui:message key="you-have-already-signed-this-document" />
		</div>
	</c:when>
	<c:when test="<%= digitalSignatureSigningConfig != null %>">
		<div class="digital-signature-sign-container" id="<portlet:namespace />digitalSignatureSignContainer">
			<liferay-ui:message key="loading-your-document-for-signing" />
		</div>

		<aui:script>
			var signingConfig = <%= digitalSignatureSigningConfig %>;

			var script = document.createElement('script');

			script.onload = function () {
				window.DocuSign.loadDocuSign(signingConfig.integrationKey).then(
					function (docuSign) {
						var signing = docuSign.signing({
							displayFormat: 'default',
							url: signingConfig.signingURL,
						});

						signing.on('sessionEnd', function (event) {
							window.location.href =
								signingConfig.returnURL +
								'&event=' +
								encodeURIComponent(event.sessionEndType);
						});

						signing.mount(
							'#<portlet:namespace />digitalSignatureSignContainer'
						);
					}
				);
			};

			script.src = signingConfig.jsURL;

			document.head.appendChild(script);
		</aui:script>
	</c:when>
	<c:otherwise>
		<div class="alert alert-info" role="alert">
			<liferay-ui:message key="there-is-no-document-available-for-you-to-sign" />
		</div>
	</c:otherwise>
</c:choose>