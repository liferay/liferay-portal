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

<div class="row">
	<div class="col-md-12">
		<aui:input checked="<%= digitalSignatureConfiguration.enabled() %>" inlineLabel="right" label='<%= LanguageUtil.get(resourceBundle, "enabled") %>' labelCssClass="simple-toggle-switch" name="enabled" type="toggle-switch" value="<%= digitalSignatureConfiguration.enabled() %>" />
	</div>
</div>

<div class="form-group row">
	<div class="col-md-12">
		<aui:select label="site-settings-strategy" name="siteSettingsStrategy" onchange='<%= liferayPortletResponse.getNamespace() + "onChangeDigitalSignatureSiteSettingsStrategy(event);" %>' required="<%= true %>" value="<%= digitalSignatureConfiguration.siteSettingsStrategy() %>">
			<aui:option label="" value="" />

			<%
			for (String digitalSignatureSiteSettingsStrategy : DigitalSignatureConstants.SITE_SETTINGS_STRATEGIES) {
			%>

				<aui:option label='<%= "site-settings-strategy-" + digitalSignatureSiteSettingsStrategy %>' value="<%= digitalSignatureSiteSettingsStrategy %>" />

			<%
			}
			%>

		</aui:select>

		<label class="text-secondary">
			<liferay-ui:message arguments="digital-signature" key="site-settings-strategy-description" />
		</label>
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
			<aui:input label="api-username" name="apiUsername" type="text" value="<%= digitalSignatureConfiguration.apiUsername() %>" />
		</div>

		<div class="col-md-6">
			<aui:input label="api-account-id" name="apiAccountId" type="text" value="<%= digitalSignatureConfiguration.apiAccountId() %>" />
		</div>
	</div>

	<div class="form-group row">
		<div class="col-md-6">
			<aui:input label="account's-base-uri" name="accountBaseURI" type="text" value="<%= digitalSignatureConfiguration.accountBaseURI() %>" />
		</div>

		<div class="col-md-6">
			<aui:input label="integration-key" name="integrationKey" type="text" value="<%= digitalSignatureConfiguration.integrationKey() %>" />
		</div>
	</div>

	<div class="form-group row">
		<div class="col-md-6">
			<aui:select label="environment" name="environment" required="<%= !Objects.equals(digitalSignatureConfiguration.siteSettingsStrategy(), "always-override") %>" value="<%= digitalSignatureConfiguration.environment() %>">
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
			<aui:input label="rsa-private-key" name="rsaPrivateKey" type="textarea" value="<%= digitalSignatureConfiguration.rsaPrivateKey() %>" />
		</div>
	</div>
</div>

<aui:script>
	function <portlet:namespace />onChangeDigitalSignatureSiteSettingsStrategy(
		event
	) {
		var digitalSignatureProviderCredentialsElement = document.getElementById(
			'<portlet:namespace />digitalSignatureProviderCredentials'
		);

		var digitalSignatureSiteSettingsStrategyElement = document.getElementById(
			'<portlet:namespace />siteSettingsStrategy'
		);

		var alwaysOverride =
			digitalSignatureSiteSettingsStrategyElement.value === 'always-override';

		if (alwaysOverride) {
			digitalSignatureProviderCredentialsElement.classList.add('hide');
		}
		else {
			digitalSignatureProviderCredentialsElement.classList.remove('hide');
		}

		// The credentials are hidden when every site overrides them, but a hidden
		// field is still validated, and its message renders inside the hidden
		// block. Keep the rule in step with the block so the form can submit.

		var form = Liferay.Form.get('<portlet:namespace />fm');

		if (form) {
			var rules = form.formValidator._getAttr('rules');

			rules['<portlet:namespace />environment'] = {
				required: !alwaysOverride,
			};
		}
	}

	<portlet:namespace />onChangeDigitalSignatureSiteSettingsStrategy();
</aui:script>