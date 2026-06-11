<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
LocalEntityManager.CertificateUsage certificateUsage = LocalEntityManager.CertificateUsage.valueOf(ParamUtil.getString(request, "certificateUsage"));
KeyStore keyStore = (KeyStore)request.getAttribute(SamlWebKeys.SAML_KEYSTORE);
String keyStorePassword = ParamUtil.getString(request, "keyStorePassword", null);
String tempFileName = ParamUtil.getString(request, "selectUploadedFile");

FileEntry fileEntry = null;

if (Validator.isNotNull(tempFileName)) {
	fileEntry = SamlTempFileEntryUtil.getTempFileEntry(themeDisplay.getUser(), tempFileName);
}
%>

<c:choose>
	<c:when test="<%= keyStore == null %>">
		<div class="lfr-form-content lfr-dynamic-uploader <%= (fileEntry == null) ? "hide-dialog-footer" : StringPool.BLANK %>">
			<liferay-ui:error key="certificateException" message="there-was-a-problem-reading-one-or-more-certificates-in-the-keystore" />
			<liferay-ui:error key="incorrectKeyStorePassword" message="incorrect-keystore-password" />
			<liferay-ui:error key="keyStoreIntegrityCheckingAlgorithmNotSupported" message="the-keystore-uses-an-integrity-checking-algorithm-which-is-not-supported" />

			<div class="lfr-upload-container" id="<portlet:namespace />fileUpload"></div>

			<aui:input label="keystore-password" name="keyStorePassword" type="password" />
		</div>

		<aui:script use="liferay-upload">
			var liferayUpload = new Liferay.Upload({
				'boundingBox': '#<portlet:namespace />fileUpload',

				<%
				DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance(locale);
				%>

				'decimalSeparator': '<%= decimalFormatSymbols.getDecimalSeparator() %>',
				'deleteFile':
					'<liferay-portlet:resourceURL copyCurrentRenderParameters="<%= false %>" id="/admin/update_certificate"><portlet:param name="<%= Constants.CMD %>" value="<%= Constants.DELETE_TEMP %>" /></liferay-portlet:resourceURL>',
				'fileDescription': '*.p12 *.pfx',
				'maxFileSize':
					'<%= UploadServletRequestConfigurationProviderUtil.getMaxSize() %> B',
				'multipleFiles': false,
				'namespace': '<portlet:namespace />',
				'tempFileURL':
					'<liferay-portlet:resourceURL copyCurrentRenderParameters="<%= false %>" id="/admin/update_certificate"><portlet:param name="selectUploadedFile" value="<%= Constants.GET_TEMP %>" /><portlet:param name="selectUploadedFile" value='<%= ParamUtil.getString(request, "selectUploadedFile") %>' /></liferay-portlet:resourceURL>',
				'tempRandomSuffix': '<%= SamlTempFileEntryUtil.TEMP_RANDOM_SUFFIX %>',
				'strings.dropFileText':
					'<liferay-ui:message key="drop-a-pkcs12-formatted-keystore-file-here-to-inspect" />',
				'strings.fileCannotBeSavedText':
					'<liferay-ui:message key="the-file-x-cannot-be-inspected" />',
				'uploadFile':
					'<liferay-portlet:resourceURL copyCurrentRenderParameters="<%= false %>" id="/admin/update_certificate"><portlet:param name="<%= Constants.CMD %>" value="<%= Constants.ADD_TEMP %>" /></liferay-portlet:resourceURL>',
			});

			liferayUpload._uploader.on('alluploadscomplete', (event) => {
				toggleContinueButton();
			});

			Liferay.on('tempFileRemoved', (event) => {
				toggleContinueButton();
			});

			function toggleContinueButton() {
				var lfrDynamicUploader = liferayUpload
					.get('boundingBox')
					.ancestor('.lfr-dynamic-uploader');
				var uploadedFiles = liferayUpload._fileListContent.all(
					'.upload-file.upload-complete'
				);

				if (uploadedFiles.size() == 1) {
					lfrDynamicUploader.removeClass('hide-dialog-footer');
				}
				else {
					lfrDynamicUploader.addClass('hide-dialog-footer');
				}
			}
		</aui:script>

		<aui:button-row>
			<aui:button name="continueButton" type="submit" value="continue" />
		</aui:button-row>
	</c:when>
	<c:otherwise>
		<aui:input name="keyStorePassword" type="hidden" value="<%= keyStorePassword %>" />
		<aui:input name="selectUploadedFile" type="hidden" value="<%= tempFileName %>" />

		<%
		List<String> aliases = new ArrayList<>();
		CertificateTool certificateTool = (CertificateTool)request.getAttribute(SamlWebKeys.SAML_CERTIFICATE_TOOL);
		int otherEntriesCount = 0;

		Enumeration<String> enumeration = keyStore.aliases();
		%>

		<liferay-util:buffer
			var="previews"
		>
			<div data-keyStoreEntryAlias=""><liferay-ui:message key="select-a-keystore-entry-to-see-a-preview" /></div>

			<%
			while (enumeration.hasMoreElements()) {
				String alias = enumeration.nextElement();

				if (!keyStore.entryInstanceOf(alias, KeyStore.PrivateKeyEntry.class)) {
					otherEntriesCount++;

					continue;
				}

				aliases.add(alias);

				X509Certificate x509Certificate = (X509Certificate)keyStore.getCertificate(alias);
			%>

				<div class="hide" data-keyStoreEntryAlias="<%= alias %>">
					<%@ include file="/admin/certificate_info.jspf" %>
				</div>

			<%
			}
			%>

		</liferay-util:buffer>

		<div class="container-fluid lfr-form-content">
			<liferay-ui:error key="certificateAlgorithmNotAllowed" message="only-rsa-keys-with-2048-bits-or-more-are-allowed-in-fips-mode" />
			<liferay-ui:error key="certificateException" message="there-was-a-problem-reading-one-or-more-certificates-in-the-keystore" />
			<liferay-ui:error key="incorrectKeyPassword" message="incorrect-key-password" />
			<liferay-ui:error key="keyEncryptionAlgorithmNotSupported" message="the-private-key-associated-with-the-alias-is-encrypted-with-an-unsupported-algorithm" />

			<div class="row">
				<div class="col-lg-12">
					<div class="portlet-msg-info">
						<liferay-ui:message arguments="<%= aliases.size() %>" key="found-x-keystore-entries-containing-a-private-key-with-its-associated-certificate-chain" />

						<c:if test="<%= otherEntriesCount > 0 %>">
							<br />

							<liferay-ui:message arguments="<%= otherEntriesCount %>" key="a-further-x-entries-of-other-types-were-ignored" />
						</c:if>
					</div>
				</div>
			</div>

			<c:if test="<%= !aliases.isEmpty() %>">
				<div class="row">
					<div class="col-lg-3 col-sm-12">
						<aui:fieldset label="keystore-entry-to-import">

							<%
							for (String alias : aliases) {
							%>

								<aui:input label="<%= alias %>" name="selectKeyStoreAlias" type="radio" value="<%= alias %>" />

							<%
							}
							%>

						</aui:fieldset>
					</div>

					<div class="certificate-preview col-lg-9 col-sm-12">
						<div class="sheet">
							<div class="panel-group panel-group-flush">
								<aui:fieldset label="preview">
									<%= previews %>
								</aui:fieldset>
							</div>
						</div>
					</div>
				</div>
			</c:if>
		</div>

		<portlet:renderURL var="backURL">
			<portlet:param name="mvcRenderCommandName" value="/admin/update_certificate" />
			<portlet:param name="<%= Constants.CMD %>" value="<%= ParamUtil.getString(request, Constants.CMD) %>" />
			<portlet:param name="certificateUsage" value="<%= certificateUsage.name() %>" />
			<portlet:param name="selectUploadedFile" value="<%= fileEntry.getFileName() %>" />
		</portlet:renderURL>

		<aui:button-row>
			<aui:button href="<%= backURL %>" name="back" value="back" />

			<c:if test="<%= !aliases.isEmpty() %>">
				<aui:button type="submit" value="import" />
			</c:if>
		</aui:button-row>

		<aui:script use="aui-base">
			var keyStoreEntryRadios = A.all(
				'input[name="<portlet:namespace />selectKeyStoreAlias"]'
			);

			keyStoreEntryRadios.on('click', (event) => {
				var keyStoreEntryAlias = event.currentTarget.val();

				A.all('.certificate-preview div[data-keyStoreEntryAlias]').hide();
				A.all(
					'.certificate-preview div[data-keyStoreEntryAlias="' +
						keyStoreEntryAlias +
						'"]'
				).show();
			});
		</aui:script>
	</c:otherwise>
</c:choose>