<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
UnicodeProperties unicodeProperties = PropertiesParamUtil.getProperties(request, "settings--");

String entityId = unicodeProperties.getProperty(PortletPropsKeys.SAML_ENTITY_ID, (String)request.getAttribute(SamlWebKeys.SAML_ENTITY_ID));

boolean keystoreException = false;
boolean keystoreIncorrectPassword = false;

if (Validator.isNotNull(entityId)) {
	GeneralTabDefaultViewDisplayContext.X509CertificateStatus x509CertificateStatus = generalTabDefaultViewDisplayContext.getX509CertificateStatus();

	keystoreException = x509CertificateStatus.getStatus() == GeneralTabDefaultViewDisplayContext.X509CertificateStatus.Status.SAML_KEYSTORE_EXCEPTION;
	keystoreIncorrectPassword = x509CertificateStatus.getStatus() == GeneralTabDefaultViewDisplayContext.X509CertificateStatus.Status.SAML_KEYSTORE_PASSWORD_INCORRECT;
}

boolean roleSp = StringUtil.equalsIgnoreCase(samlProviderConfiguration.role(), SamlProviderConfigurationKeys.SAML_ROLE_SP) || StringUtil.equalsIgnoreCase(samlProviderConfiguration.role(), SamlProviderConfigurationKeys.SAML_ROLE_IB);
String samlRole = unicodeProperties.getProperty(PortletPropsKeys.SAML_ROLE, samlProviderConfiguration.role());

String samlRoleHelpMessage = StringPool.BLANK;

boolean samlRoleIdpOptionDisabled = roleSp && !generalTabDefaultViewDisplayContext.isRoleIdPAvailable();

if (samlRoleIdpOptionDisabled) {
	samlRoleHelpMessage = "the-identity-provider-role-has-been-disabled-it-can-be-re-enabled-in-system-settings";
}
%>

<portlet:actionURL name="/admin/update_general" var="updateGeneralURL">
	<portlet:param name="tabs1" value="general" />
</portlet:actionURL>

<aui:form action="<%= updateGeneralURL %>">
	<liferay-ui:error key="certificateInvalid" message="please-create-a-signing-credential-before-enabling" />
	<liferay-ui:error key="entityIdInUse" message="saml-must-be-disabled-before-changing-the-entity-id" />
	<liferay-ui:error key="entityIdTooLong" message="entity-id-too-long" />
	<liferay-ui:error key="idpRoleNotConfigurable" message="the-identity-provider-role-has-been-disabled-it-can-be-re-enabled-in-system-settings" />
	<liferay-ui:error key="roleInUse" message="saml-must-be-disabled-before-changing-the-saml-role" />

	<aui:fieldset>
		<aui:input label="enabled" name='<%= "settings--" + PortletPropsKeys.SAML_ENABLED + "--" %>' type="checkbox" value="<%= samlProviderConfigurationHelper.isEnabled() %>" />

		<c:if test="<%= !samlRoleIdpOptionDisabled && !generalTabDefaultViewDisplayContext.isRoleIdPAvailable() %>">
			<div class="portlet-msg-info">
				<liferay-ui:message key="the-identity-provider-role-has-been-disabled-please-re-enable-it-in-system-settings" />
			</div>
		</c:if>

		<c:if test="<%= samlProviderConfigurationHelper.isEnabled() && roleSp && !localEntityManager.hasDefaultIdpRole() %>">
			<div class="portlet-msg-info">
				<liferay-ui:message key="you-must-configure-at-least-one-identity-provider-connection-for-saml-to-function" />
			</div>
		</c:if>

		<aui:select disabled="<%= samlRoleIdpOptionDisabled %>" helpMessage="<%= samlRoleHelpMessage %>" label="saml-role" name='<%= "settings--" + PortletPropsKeys.SAML_ROLE + "--" %>' required="<%= !samlRoleIdpOptionDisabled %>">
			<c:if test='<%= FeatureFlagManagerUtil.isEnabled("LPD-29737") %>'>
				<aui:option label="identity-broker" selected="<%= samlRole.equals(SamlProviderConfigurationKeys.SAML_ROLE_IB) %>" value="<%= SamlProviderConfigurationKeys.SAML_ROLE_IB %>" />
			</c:if>

			<aui:option label="identity-provider" selected="<%= samlRole.equals(SamlProviderConfigurationKeys.SAML_ROLE_IDP) %>" value="<%= SamlProviderConfigurationKeys.SAML_ROLE_IDP %>" />
			<aui:option label="service-provider" selected="<%= samlRole.equals(SamlProviderConfigurationKeys.SAML_ROLE_SP) %>" value="<%= SamlProviderConfigurationKeys.SAML_ROLE_SP %>" />
		</aui:select>

		<c:if test="<%= samlRoleIdpOptionDisabled %>">
			<aui:input name='<%= "settings--" + PortletPropsKeys.SAML_ROLE + "--" %>' type="hidden" value="<%= SamlProviderConfigurationKeys.SAML_ROLE_SP %>" />
		</c:if>

		<aui:input helpMessage="entity-id-help" label="saml-entity-id" name='<%= "settings--" + PortletPropsKeys.SAML_ENTITY_ID + "--" %>' required="<%= true %>" value="<%= entityId %>" />
	</aui:fieldset>

	<aui:button-row>
		<aui:button type="submit" value="save" />
	</aui:button-row>
</aui:form>

<br />

<portlet:actionURL name="/admin/update_certificate" var="updateCertificateURL">
	<portlet:param name="tabs1" value="general" />
</portlet:actionURL>

<c:choose>
	<c:when test="<%= keystoreException %>">
		<div class="portlet-msg-error">
			<liferay-ui:message key="keystore-exception" />
		</div>
	</c:when>
	<c:when test="<%= keystoreIncorrectPassword %>">
		<div class="portlet-msg-error">
			<liferay-ui:message key="keystore-password-incorrect" />
		</div>
	</c:when>
	<c:when test="<%= Validator.isNotNull(entityId) %>">
		<aui:fieldset label="certificate-and-private-key">
			<liferay-util:include page="/admin/certificate_info.jsp" servletContext="<%= application %>">
				<liferay-util:param name="certificateUsage" value="<%= LocalEntityManager.CertificateUsage.SIGNING.name() %>" />
			</liferay-util:include>
		</aui:fieldset>

		<br />

		<c:if test="<%= roleSp %>">
			<aui:fieldset label="encryption-certificate-and-private-key">
				<liferay-util:include page="/admin/certificate_info.jsp" servletContext="<%= application %>">
					<liferay-util:param name="certificateUsage" value="<%= LocalEntityManager.CertificateUsage.ENCRYPTION.name() %>" />
				</liferay-util:include>
			</aui:fieldset>
		</c:if>
	</c:when>
</c:choose>

<aui:script>
	window['<portlet:namespace />showCertificateDialog'] = function (uri) {
		Liferay.Util.openModal({
			id: '<portlet:namespace />certificateDialog',
			iframeBodyCssClass: 'dialog-with-footer',
			title: '<%= UnicodeLanguageUtil.get(request, "certificate-and-private-key") %>',
			url: uri,
		});
	};

	<portlet:renderURL var="refreshViewURL" />

	window['<portlet:namespace />closeDialog'] = function (dialogId, stateChange) {
		Liferay.fire('closeModal', {
			id: dialogId,
		});

		if (stateChange) {
			window.location.replace('<%= HtmlUtil.escapeJS(refreshViewURL) %>');
		}
	};
</aui:script>