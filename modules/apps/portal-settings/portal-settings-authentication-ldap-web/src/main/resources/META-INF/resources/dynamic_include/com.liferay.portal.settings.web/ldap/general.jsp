<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/dynamic_include/init.jsp" %>

<%
ConfigurationProvider<LDAPAuthConfiguration> ldapAuthConfigurationProvider = ConfigurationProviderUtil.getLDAPAuthConfigurationProvider();

long companyId = 0L;

String portletId = PortalUtil.getPortletId(request);

if (portletId.equals(ConfigurationAdminPortletKeys.INSTANCE_SETTINGS)) {
	companyId = themeDisplay.getCompanyId();
}

LDAPAuthConfiguration ldapAuthConfiguration = ldapAuthConfigurationProvider.getConfiguration(companyId);
%>

<aui:fieldset>
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= LDAPSettingsConstants.CMD_UPDATE_AUTH %>" />

	<liferay-ui:error key="ldapExportAndImportOnPasswordAutogeneration" message="ldap-export-must-not-be-enabled-when-autogeneration-of-user-passwords-is-enabled-for-ldap-import" />

	<liferay-ui:error exception="<%= LocalizedLDAPConfigurationException.class %>">

		<%
		LocalizedLDAPConfigurationException localizedLDAPConfigurationException = (LocalizedLDAPConfigurationException)errorException;
		%>

		<liferay-ui:message arguments="<%= localizedLDAPConfigurationException.getMessageArguments() %>" key="<%= localizedLDAPConfigurationException.getMessageKey() %>" translateArguments="<%= false %>" />
	</liferay-ui:error>

	<aui:input label="enabled" name='<%= "ldap--" + LDAPConstants.AUTH_ENABLED + "--" %>' type="checkbox" value="<%= ldapAuthConfiguration.enabled() %>" />

	<aui:input label="required" name='<%= "ldap--" + LDAPConstants.AUTH_REQUIRED + "--" %>' type="checkbox" value="<%= ldapAuthConfiguration.required() %>" />

	<aui:input helpMessage="ldap-password-policy-help" label="use-ldap-password-policy" name='<%= "ldap--" + LDAPConstants.PASSWORD_POLICY_ENABLED + "--" %>' type="checkbox" value="<%= ldapAuthConfiguration.passwordPolicyEnabled() %>" />

	<aui:select label="method" name='<%= "ldap--" + LDAPConstants.AUTH_METHOD + "--" %>' value="<%= ldapAuthConfiguration.method() %>">
		<aui:option label="bind" value="<%= LDAPConstants.AUTH_METHOD_BIND %>" />
		<aui:option label="password-compare" value="<%= LDAPConstants.AUTH_METHOD_PASSWORD_COMPARE %>" />
	</aui:select>

	<aui:select helpMessage='<%= FIPSModeUtil.isEnabled() ? "ldap-password-encryption-algorithm-fips-help" : null %>' label="password-encryption-algorithm" name='<%= "ldap--" + LDAPConstants.PASSWORD_ENCRYPTION_ALGORITHM + "--" %>' value="<%= ldapAuthConfiguration.passwordEncryptionAlgorithm() %>">
		<c:if test="<%= !FIPSModeUtil.isEnabled() %>">
			<aui:option label="bcrypt" value="<%= LDAPSettingsConstants.BCRYPT %>" />
			<aui:option label="md2" value="<%= LDAPSettingsConstants.MD2 %>" />
			<aui:option label="md5" value="<%= LDAPSettingsConstants.MD5 %>" />
		</c:if>

		<aui:option label="none" value="<%= LDAPSettingsConstants.NONE %>" />

		<c:if test="<%= !FIPSModeUtil.isEnabled() %>">
			<aui:option label="sha" value="<%= LDAPSettingsConstants.SHA %>" />
		</c:if>

		<aui:option label="sha-256" value="<%= LDAPSettingsConstants.SHA_256 %>" />
		<aui:option label="sha-384" value="<%= LDAPSettingsConstants.SHA_384 %>" />
		<aui:option label="sha-512" value="<%= LDAPSettingsConstants.SHA_512 %>" />
		<aui:option label="pbkdf2" value="<%= LDAPSettingsConstants.PBKDF2 %>" />

		<c:if test="<%= !FIPSModeUtil.isEnabled() %>">
			<aui:option label="ssha" value="<%= LDAPSettingsConstants.SSHA %>" />
			<aui:option label="ufc-crypt" value="<%= LDAPSettingsConstants.UFC_CRYPT %>" />
		</c:if>
	</aui:select>
</aui:fieldset>