<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<portlet:actionURL name="/admin/update_service_provider" var="updateServiceProviderURL">
	<portlet:param name="tabs1" value="service-provider" />
</portlet:actionURL>

<aui:form action="<%= updateServiceProviderURL %>">
	<aui:fieldset label="general">
		<aui:input helpMessage="saml-sp-assertion-signature-required-description" label="saml-sp-assertion-signature-required" name='<%= "settings--" + PortletPropsKeys.SAML_SP_ASSERTION_SIGNATURE_REQUIRED + "--" %>' type="checkbox" value="<%= samlProviderConfiguration.assertionSignatureRequired() %>" />

		<aui:input helpMessage="saml-sp-clock-skew-description" label="saml-sp-clock-skew" name='<%= "settings--" + PortletPropsKeys.SAML_SP_CLOCK_SKEW + "--" %>' value="<%= samlProviderConfiguration.clockSkew() %>" />

		<aui:input helpMessage="saml-sp-ldap-import-enabled-description" label="saml-sp-ldap-import-enabled" name='<%= "settings--" + PortletPropsKeys.SAML_SP_LDAP_IMPORT_ENABLED + "--" %>' type="checkbox" value="<%= samlProviderConfiguration.ldapImportEnabled() %>" />

		<aui:input helpMessage="saml-sp-sign-authn-request-description" label="saml-sp-sign-authn-request" name='<%= "settings--" + PortletPropsKeys.SAML_SP_SIGN_AUTHN_REQUEST + "--" %>' type="checkbox" value="<%= samlProviderConfiguration.signAuthnRequest() %>" />

		<aui:input helpMessage="saml-sign-metadata-description" label="saml-sign-metadata" name='<%= "settings--" + PortletPropsKeys.SAML_SIGN_METADATA + "--" %>' type="checkbox" value="<%= samlProviderConfiguration.signMetadata() %>" />

		<aui:input helpMessage="saml-ssl-required-description" label="saml-ssl-required" name='<%= "settings--" + PortletPropsKeys.SAML_SSL_REQUIRED + "--" %>' type="checkbox" value="<%= samlProviderConfiguration.sslRequired() %>" />

		<aui:input helpMessage="saml-sp-allow-showing-the-login-portlet-description" label="saml-sp-allow-showing-the-login-portlet" name='<%= "settings--" + PortletPropsKeys.SAML_SP_ALLOW_SHOWING_THE_LOGIN_PORTLET + "--" %>' type="checkbox" value="<%= samlProviderConfiguration.allowShowingTheLoginPortlet() %>" />
	</aui:fieldset>

	<aui:button-row>
		<aui:button type="submit" value="save" />
	</aui:button-row>
</aui:form>