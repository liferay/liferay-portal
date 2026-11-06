<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<liferay-ui:search-container
	emptyResultsMessage="there-are-no-identity-providers"
	headerNames="name"
	iteratorURL='<%=
		PortletURLBuilder.createRenderURL(
			renderResponse
		).setTabs1(
			"identity-provider-connections"
		).buildPortletURL()
	%>'
	total="<%= GetterUtil.getInteger(renderRequest.getAttribute(SamlWebKeys.SAML_SP_IDP_CONNECTIONS_COUNT)) %>"
>
	<liferay-ui:search-container-results
		results="<%= (List<SamlSpIdpConnection>)renderRequest.getAttribute(SamlWebKeys.SAML_SP_IDP_CONNECTIONS) %>"
	/>

	<liferay-ui:search-container-row
		className="com.liferay.saml.persistence.model.SamlSpIdpConnection"
		escapedModel="<%= true %>"
		keyPropertyName="samlSpIdpConnectionId"
		modelVar="samlSpIdpConnection"
	>
		<portlet:renderURL var="rowURL">
			<portlet:param name="mvcRenderCommandName" value="/admin/edit_identity_provider_connection" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
			<portlet:param name="samlSpIdpConnectionId" value="<%= String.valueOf(samlSpIdpConnection.getSamlSpIdpConnectionId()) %>" />
		</portlet:renderURL>

		<liferay-ui:search-container-column-text
			href="<%= rowURL %>"
			name="name"
			property="name"
		/>

		<liferay-ui:search-container-column-text
			href="<%= rowURL %>"
			name="saml-entity-id"
			property="samlIdpEntityId"
		/>

		<liferay-ui:search-container-column-text
			href="<%= rowURL %>"
			name="enabled"
			property="enabled"
		/>

		<liferay-ui:search-container-column-jsp
			align="right"
			cssClass="entry-action"
			path="/admin/identity_provider_connection_action.jsp"
			valign="top"
		/>
	</liferay-ui:search-container-row>

	<portlet:renderURL var="addIdentityProviderURL">
		<portlet:param name="mvcRenderCommandName" value="/admin/edit_identity_provider_connection" />
		<portlet:param name="redirect" value="<%= currentURL %>" />
	</portlet:renderURL>

	<aui:button-row cssClass="sheet-footer">
		<aui:button href="<%= addIdentityProviderURL %>" label="add-identity-provider" value="add-identity-provider" />
	</aui:button-row>

	<liferay-ui:search-iterator
		markupView="lexicon"
	/>
</liferay-ui:search-container>