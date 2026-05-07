<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata = (OAuthClientPRLocalMetadata)request.getAttribute(OAuthClientPRLocalMetadata.class.getName());

String redirect = ParamUtil.getString(request, "redirect");

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);

renderResponse.setTitle((oAuthClientPRLocalMetadata == null) ? LanguageUtil.get(request, "new-oauth-client-pr-local-metadata") : LanguageUtil.get(request, "edit-oauth-client-pr-local-metadata"));
%>

<portlet:actionURL name="/oauth_client_admin/update_oauth_client_pr_local_metadata" var="updateOAuthClientPRLocalMetadataURL">
	<portlet:param name="mvcRenderCommandName" value="/oauth_client_admin/update_oauth_client_pr_local_metadata" />
</portlet:actionURL>

<aui:form action="<%= updateOAuthClientPRLocalMetadataURL %>" id="oauth-client-pr-fm" method="post" name="oauth-client-pr-fm">
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
	<aui:input name="oAuthClientPRLocalMetadataId" type="hidden" value="<%= (oAuthClientPRLocalMetadata != null) ? oAuthClientPRLocalMetadata.getOAuthClientPRLocalMetadataId() : StringPool.BLANK %>" />

	<aui:model-context bean="<%= oAuthClientPRLocalMetadata %>" model="<%= OAuthClientPRLocalMetadata.class %>" />

	<clay:container-fluid
		cssClass="container-view"
	>
		<div class="sheet">
			<aui:fieldset>
				<liferay-ui:error exception="<%= DuplicateOAuthClientPRLocalMetadataException.class %>" message="oauth-client-pr-local-metadata-duplicate-resource" />

				<liferay-ui:error exception="<%= OAuthClientPRLocalMetadataLocalWellKnownURIException.class %>">
					<liferay-ui:message arguments="<%= HtmlUtil.escape(((OAuthClientPRLocalMetadataLocalWellKnownURIException)errorException).getMessage()) %>" key="oauth-client-pr-local-metadata-invalid-well-known-uri-x" />
				</liferay-ui:error>

				<liferay-ui:error exception="<%= OAuthClientPRLocalMetadataMetadataJSONException.class %>">
					<liferay-ui:message arguments="<%= HtmlUtil.escape(((OAuthClientPRLocalMetadataMetadataJSONException)errorException).getMessage()) %>" key="oauth-client-pr-local-metadata-invalid-metadata-json-x" />
				</liferay-ui:error>

				<liferay-ui:error exception="<%= OAuthClientPRLocalMetadataResourceException.class %>">
					<liferay-ui:message arguments="<%= HtmlUtil.escape(((OAuthClientPRLocalMetadataResourceException)errorException).getMessage()) %>" key="oauth-client-pr-local-metadata-invalid-https-resource-x" />
				</liferay-ui:error>

				<aui:fieldset label="general">
					<aui:input helpMessage="oauth-client-pr-local-metadata-resource-help" label="oauth-client-pr-resource" name="resource" required="<%= true %>" type="text" />

					<aui:input label="oauth-client-pr-resource-name" name="resourceName" type="text" value='<%= request.getAttribute("resourceName") %>' />

					<aui:input helpMessage="oauth-client-pr-authorization-servers-help" label="oauth-client-pr-authorization-servers" name="authorizationServers" required="<%= true %>" type="text" value='<%= request.getAttribute("authorizationServers") %>' />

					<aui:input helpMessage="oauth-client-pr-bearer-methods-supported-help" label="oauth-client-pr-bearer-methods-supported" name="bearerMethodsSupported" type="text" value='<%= (request.getAttribute("bearerMethodsSupported") != null) ? request.getAttribute("bearerMethodsSupported") : "header" %>' />

					<aui:input helpMessage="oauth-client-pr-scopes-supported-help" label="oauth-client-pr-scopes-supported" name="scopesSupported" type="text" value='<%= request.getAttribute("scopesSupported") %>' />
				</aui:fieldset>

				<aui:fieldset label="oauth-client-pr-local-metadata-publishing">
					<aui:input checked="<%= (oAuthClientPRLocalMetadata != null) ? oAuthClientPRLocalMetadata.getLocalWellKnownEnabled() : false %>" label="enable" name="localWellKnownEnabled" type="checkbox" />

					<aui:input helpMessage="oauth-client-pr-local-well-known-uri-help" label="oauth-client-pr-local-well-known-uri" name="localWellKnownURI" readonly="true" type="text" value="<%= (oAuthClientPRLocalMetadata != null) ? oAuthClientPRLocalMetadata.getLocalWellKnownURI() : StringPool.BLANK %>" />

					<aui:input label="oauth-client-pr-metadata-json" name="metadataJSON" readonly="true" style="min-height: 240px;" type="textarea" value="<%= (oAuthClientPRLocalMetadata != null) ? oAuthClientPRLocalMetadata.getMetadataJSON() : StringPool.BLANK %>" />
				</aui:fieldset>

				<aui:button-row>
					<aui:button type="submit" />
					<aui:button href="<%= redirect %>" type="cancel" />
				</aui:button-row>
			</aui:fieldset>
		</div>
	</clay:container-fluid>
</aui:form>