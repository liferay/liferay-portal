<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/dynamic_include/init.jsp" %>

<%
TokenConfiguration tokenConfiguration = ConfigurationProviderUtil.getConfiguration(TokenConfiguration.class, new ParameterMapSettingsLocator(request.getParameterMap(), PortalSettingsTokenConstants.FORM_PARAMETER_NAMESPACE, new CompanyServiceSettingsLocator(company.getCompanyId(), TokenConstants.SERVICE_NAME)));

String[] authenticationCookies = tokenConfiguration.authenticationCookies();

int[] authenticationCookiesIndexes = new int[authenticationCookies.length];

boolean enabled = tokenConfiguration.enabled();
boolean importFromLDAP = tokenConfiguration.importFromLDAP();
String logoutRedirectURL = tokenConfiguration.logoutRedirectURL();
String tokenLocation = tokenConfiguration.tokenLocation();
String userTokenName = tokenConfiguration.userTokenName();
%>

<aui:fieldset>
	<liferay-ui:error key="logoutRedirectURLInvalid" message="the-logout-redirect-url-is-invalid" />

	<aui:fieldset>
		<aui:input label="enabled" name='<%= PortalSettingsTokenConstants.FORM_PARAMETER_NAMESPACE + "enabled" %>' type="checkbox" value="<%= enabled %>" />

		<aui:input helpMessage="import-from-ldap-help" label="import-from-ldap" name='<%= PortalSettingsTokenConstants.FORM_PARAMETER_NAMESPACE + "importFromLDAP" %>' type="checkbox" value="<%= importFromLDAP %>" />

		<aui:input cssClass="lfr-input-text-container" helpMessage="user-token-name-help" label="user-token-name" name="<%= PortalSettingsTokenConstants.FORM_PARAMETER_NAMESPACE + TokenConfigurationKeys.USER_TOKEN_NAME %>" type="text" value="<%= userTokenName %>" />

		<aui:select helpMessage="token-location-help" label="token-location" name="<%= PortalSettingsTokenConstants.FORM_PARAMETER_NAMESPACE + TokenConfigurationKeys.TOKEN_LOCATION %>">
			<aui:option label="token-location-<%= TokenLocation.COOKIE %>" selected="<%= tokenLocation.equals(TokenLocation.COOKIE) %>" value="TokenLocation.COOKIE %>" />
			<aui:option label='<%= "token-location-" + TokenLocation.REQUEST %>' selected="<%= tokenLocation.equals(TokenLocation.REQUEST) %>" value="<%= TokenLocation.REQUEST %>" />
			<aui:option label='<%= "token-location-" + TokenLocation.REQUEST_HEADER %>' selected="<%= tokenLocation.equals(TokenLocation.REQUEST_HEADER) %>" value="<%= TokenLocation.REQUEST_HEADER %>" />
			<aui:option label='<%= "token-location-" + TokenLocation.SESSION %>' selected="<%= tokenLocation.equals(TokenLocation.SESSION) %>" value="<%= TokenLocation.SESSION %>" />
		</aui:select>
	</aui:fieldset>

	<aui:fieldset id='<%= liferayPortletResponse.getNamespace() + "authenticationCookies" %>'>

		<%
		if (authenticationCookies == null) {
			authenticationCookies = new String[0];
		}

		for (int i = 0; i < authenticationCookies.length; i++) {
			String authenticationCookie = authenticationCookies[i];
			authenticationCookiesIndexes[i] = i;
		%>

			<div class="lfr-form-row">
				<div class="form-group-item">
					<aui:input cssClass="lfr-input-text-container" fieldParam="<%= TokenConfigurationKeys.AUTHENTICATION_COOKIES + i %>" helpMessage="authentication-cookies-help" label="authentication-cookies" name="<%= PortalSettingsTokenConstants.FORM_PARAMETER_NAMESPACE + TokenConfigurationKeys.AUTHENTICATION_COOKIES + i %>" type="text" value="<%= authenticationCookie %>" />
				</div>
			</div>

		<%
		}
		%>

		<aui:input name='<%= PortalSettingsTokenConstants.FORM_PARAMETER_NAMESPACE + "authenticationCookiesIndexes" %>' type="hidden" value="<%= StringUtil.merge(authenticationCookiesIndexes) %>" />
	</aui:fieldset>

	<aui:fieldset>
		<aui:input cssClass="lfr-input-text-container" helpMessage="logout-redirect-url-help" label="logout-redirect-url" name="<%= PortalSettingsTokenConstants.FORM_PARAMETER_NAMESPACE + TokenConfigurationKeys.LOGOUT_REDIRECT_URL %>" type="text" value="<%= logoutRedirectURL %>" />
	</aui:fieldset>
</aui:fieldset>

<aui:script type="module">
	import {autoFields} from '<%= FrontendESMUtil.buildURL(themeDisplay, "frontend-js-web", "auto_fields") %>';

	autoFields({
		contentBox: '#<portlet:namespace />authenticationCookies',
		fieldIndexes:
			'<portlet:namespace /><%= PortalSettingsTokenConstants.FORM_PARAMETER_NAMESPACE %>authenticationCookiesIndexes',
		namespace: '<portlet:namespace />',
	});
</aui:script>