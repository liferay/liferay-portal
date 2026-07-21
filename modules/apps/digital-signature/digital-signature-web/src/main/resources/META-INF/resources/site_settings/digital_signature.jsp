<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
DigitalSignatureConfiguration digitalSignatureConfiguration = (DigitalSignatureConfiguration)request.getAttribute(DigitalSignatureConfiguration.class.getName());
%>

<div class="form-group row">
	<div class="col-md-12">
		<label class="control-label">
			<liferay-ui:message key="site-settings-strategy" />

			<liferay-ui:icon-help message='<%= LanguageUtil.format(resourceBundle, "site-settings-strategy-description", "digital-signature") %>' />
		</label>
	</div>

	<c:if test="<%= Validator.isNotNull(digitalSignatureConfiguration.siteSettingsStrategy()) %>">
		<div class="col-md-12">
			<liferay-ui:message key='<%= "site-settings-strategy-" + digitalSignatureConfiguration.siteSettingsStrategy() %>' />
		</div>
	</c:if>
</div>

<div class="row">
	<div class="col-md-12">

		<%
		boolean digitalSignatureEnabled = GetterUtil.getBoolean(request.getAttribute(DigitalSignatureWebKeys.DIGITAL_SIGNATURE_ENABLED));

		boolean digitalSignatureEnableEmbeddedView = GetterUtil.getBoolean(request.getAttribute(DigitalSignatureWebKeys.DIGITAL_SIGNATURE_ENABLE_EMBEDDED_VIEW));

		boolean disabled = false;

		if (Objects.equals(digitalSignatureConfiguration.siteSettingsStrategy(), "always-inherit") || Validator.isNull(digitalSignatureConfiguration.siteSettingsStrategy())) {
			disabled = true;
		}
		%>

		<aui:input checked="<%= digitalSignatureEnabled %>" disabled="<%= disabled %>" inlineLabel="right" label='<%= LanguageUtil.get(resourceBundle, "enabled") %>' labelCssClass="simple-toggle-switch" name="enabled" type="toggle-switch" value="<%= digitalSignatureEnabled %>" />

		<aui:input checked="<%= digitalSignatureEnableEmbeddedView %>" disabled="<%= disabled %>" inlineLabel="right" label='<%= LanguageUtil.get(resourceBundle, "enable-embedded-view") %>' labelCssClass="simple-toggle-switch" name="enableEmbeddedView" type="toggle-switch" value="<%= digitalSignatureEnableEmbeddedView %>" />
	</div>
</div>

<div id="<portlet:namespace />digitalSignatureProviderCredentials">
	<div class="mb-4">
		<liferay-learn:message
			key="general"
			resource="digital-signature-web"
		/>
	</div>

	<div class="form-group row">
		<div class="col-md-6">
			<aui:input disabled="<%= disabled %>" label="api-username" name="apiUsername" type="text" value="<%= GetterUtil.getString(request.getAttribute(DigitalSignatureWebKeys.DIGITAL_SIGNATURE_API_USERNAME)) %>" />
		</div>

		<div class="col-md-6">
			<aui:input disabled="<%= disabled %>" label="api-account-id" name="apiAccountId" type="text" value="<%= GetterUtil.getString(request.getAttribute(DigitalSignatureWebKeys.DIGITAL_SIGNATURE_API_ACCOUNT_ID)) %>" />
		</div>
	</div>

	<div class="form-group row">
		<div class="col-md-6">
			<aui:input disabled="<%= disabled %>" label="account's-base-uri" name="accountBaseURI" type="text" value="<%= GetterUtil.getString(request.getAttribute(DigitalSignatureWebKeys.DIGITAL_SIGNATURE_ACCOUNT_BASE_URI)) %>" />
		</div>

		<div class="col-md-6">
			<aui:input disabled="<%= disabled %>" label="integration-key" name="integrationKey" type="text" value="<%= GetterUtil.getString(request.getAttribute(DigitalSignatureWebKeys.DIGITAL_SIGNATURE_INTEGRATION_KEY)) %>" />
		</div>
	</div>

	<div class="form-group row">
		<div class="col-md-6">
			<aui:select disabled="<%= disabled %>" label="environment" name="environment" required="<%= true %>" value="<%= GetterUtil.getString(request.getAttribute(DigitalSignatureWebKeys.DIGITAL_SIGNATURE_ENVIRONMENT)) %>">
				<aui:option label="" value="" />

				<%
				for (String environment : DigitalSignatureConstants.ENVIRONMENTS) {
				%>

					<aui:option label="<%= environment %>" value="<%= environment %>" />

				<%
				}
				%>

			</aui:select>
		</div>
	</div>

	<div class="form-group row">
		<div class="col-md-12">
			<aui:input disabled="<%= disabled %>" label="rsa-private-key" name="rsaPrivateKey" type="textarea" value="<%= GetterUtil.getString(request.getAttribute(DigitalSignatureWebKeys.DIGITAL_SIGNATURE_RSA_PRIVATE_KEY)) %>" />
		</div>
	</div>
</div>