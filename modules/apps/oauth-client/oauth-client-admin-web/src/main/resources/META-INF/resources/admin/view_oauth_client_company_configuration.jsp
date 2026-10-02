<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
OAuthClientCompanyConfiguration oAuthClientCompanyConfiguration = (OAuthClientCompanyConfiguration)request.getAttribute(OAuthClientCompanyConfiguration.class.getName());
%>

<portlet:actionURL name="/oauth_client_admin/update_oauth_client_company_configuration" var="updateOAuthClientCompanyConfigurationURL">
	<portlet:param name="mvcRenderCommandName" value="/oauth_client_admin/view_oauth_client_company_configuration" />
	<portlet:param name="navigation" value="oauth-client-company-configuration" />
</portlet:actionURL>

<aui:form action="<%= updateOAuthClientCompanyConfigurationURL %>" method="post" name="oauth-client-company-configuration-fm">
	<clay:container-fluid
		cssClass="container-view"
	>
		<div class="sheet">
			<aui:fieldset>
				<aui:fieldset label="general">
					<aui:input helpMessage="oauth-client-as-hosts-allowed-help" label="oauth-client-as-hosts-allowed" name="authServerHostsAllowed" type="textarea" value="<%= (oAuthClientCompanyConfiguration != null) ? StringUtil.merge(oAuthClientCompanyConfiguration.authServerHostsAllowed()) : StringPool.BLANK %>" />

					<aui:input checked="<%= (oAuthClientCompanyConfiguration != null) && oAuthClientCompanyConfiguration.authServerLocalNetworkAccessEnabled() %>" helpMessage="oauth-client-as-local-network-access-enabled-help" label="oauth-client-as-local-network-access-enabled" name="authServerLocalNetworkAccessEnabled" type="checkbox" />
				</aui:fieldset>

				<aui:button-row>
					<aui:button type="submit" />
				</aui:button-row>
			</aui:fieldset>
		</div>
	</clay:container-fluid>
</aui:form>