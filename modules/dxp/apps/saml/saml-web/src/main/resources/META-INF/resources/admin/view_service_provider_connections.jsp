<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<liferay-ui:search-container
	emptyResultsMessage="there-are-no-service-providers"
	headerNames="name"
	iteratorURL='<%=
		PortletURLBuilder.createRenderURL(
			renderResponse
		).setTabs1(
			"service-provider-connections"
		).buildPortletURL()
	%>'
	total="<%= GetterUtil.getInteger(renderRequest.getAttribute(SamlWebKeys.SAML_IDP_SP_CONNECTIONS_COUNT)) %>"
>
	<liferay-ui:search-container-results
		results="<%= (List<SamlIdpSpConnection>)renderRequest.getAttribute(SamlWebKeys.SAML_IDP_SP_CONNECTIONS) %>"
	/>

	<liferay-ui:search-container-row
		className="com.liferay.saml.persistence.model.SamlIdpSpConnection"
		escapedModel="<%= true %>"
		keyPropertyName="samlIdpSpConnectionId"
		modelVar="samlIdpSpConnection"
	>
		<portlet:renderURL var="rowURL">
			<portlet:param name="mvcRenderCommandName" value="/admin/edit_service_provider_connection" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
			<portlet:param name="samlIdpSpConnectionId" value="<%= String.valueOf(samlIdpSpConnection.getSamlIdpSpConnectionId()) %>" />
		</portlet:renderURL>

		<liferay-ui:search-container-column-text
			href="<%= rowURL %>"
			name="name"
			property="name"
		/>

		<liferay-ui:search-container-column-text
			href="<%= rowURL %>"
			name="saml-entity-id"
			property="samlSpEntityId"
		/>

		<liferay-ui:search-container-column-text
			href="<%= rowURL %>"
			name="enabled"
			property="enabled"
		/>

		<liferay-ui:search-container-column-jsp
			align="right"
			cssClass="entry-action"
			path="/admin/service_provider_connection_action.jsp"
			valign="top"
		/>
	</liferay-ui:search-container-row>

	<portlet:renderURL var="addServiceProviderURL">
		<portlet:param name="mvcRenderCommandName" value="/admin/edit_service_provider_connection" />
		<portlet:param name="redirect" value="<%= currentURL %>" />
	</portlet:renderURL>

	<aui:button-row cssClass="sheet-footer">
		<aui:button href="<%= addServiceProviderURL %>" label="add-service-provider" value="add-service-provider" />
	</aui:button-row>

	<liferay-ui:search-iterator
		markupView="lexicon"
	/>
</liferay-ui:search-container>