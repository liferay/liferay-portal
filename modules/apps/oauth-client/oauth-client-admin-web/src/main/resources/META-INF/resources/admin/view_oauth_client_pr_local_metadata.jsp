<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
int oAuthClientPRLocalMetadatasCount = OAuthClientPRLocalMetadataLocalServiceUtil.getOAuthClientPRLocalMetadatasCount(themeDisplay.getCompanyId());

OAuthClientPRLocalMetadataManagementToolbarDisplayContext oAuthClientPRLocalMetadataManagementToolbarDisplayContext = new OAuthClientPRLocalMetadataManagementToolbarDisplayContext(currentURLObj, liferayPortletRequest, liferayPortletResponse);
%>

<clay:management-toolbar
	actionDropdownItems="<%= oAuthClientPRLocalMetadataManagementToolbarDisplayContext.getActionDropdownItems() %>"
	additionalProps="<%= oAuthClientPRLocalMetadataManagementToolbarDisplayContext.getAdditionalProps() %>"
	creationMenu="<%= oAuthClientPRLocalMetadataManagementToolbarDisplayContext.getCreationMenu() %>"
	disabled="<%= oAuthClientPRLocalMetadatasCount == 0 %>"
	itemsTotal="<%= oAuthClientPRLocalMetadatasCount %>"
	orderDropdownItems="<%= oAuthClientPRLocalMetadataManagementToolbarDisplayContext.getOrderByDropdownItems() %>"
	searchContainerId="oAuthClientPRLocalMetadataSearchContainer"
	selectable="<%= true %>"
	showCreationMenu="<%= true %>"
	showSearch="<%= false %>"
	sortingOrder="<%= oAuthClientPRLocalMetadataManagementToolbarDisplayContext.getOrderByType() %>"
	sortingURL="<%= String.valueOf(oAuthClientPRLocalMetadataManagementToolbarDisplayContext.getSortingURL()) %>"
	viewTypeItems="<%= oAuthClientPRLocalMetadataManagementToolbarDisplayContext.getViewTypes() %>"
/>

<clay:container-fluid
	cssClass="closed"
>
	<liferay-ui:search-container
		emptyResultsMessage="no-oauth-client-pr-local-metadata-were-found"
		id="oAuthClientPRLocalMetadataSearchContainer"
		iteratorURL="<%= currentURLObj %>"
		rowChecker="<%= new EmptyOnClickRowChecker(renderResponse) %>"
		total="<%= oAuthClientPRLocalMetadatasCount %>"
	>
		<liferay-ui:search-container-results
			results="<%= OAuthClientPRLocalMetadataServiceUtil.getCompanyOAuthClientPRLocalMetadata(themeDisplay.getCompanyId(), searchContainer.getStart(), searchContainer.getEnd()) %>"
		/>

		<liferay-ui:search-container-row
			className="com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata"
			escapedModel="<%= true %>"
			keyProperty="resource"
			modelVar="oAuthClientPRLocalMetadata"
		>
			<portlet:renderURL var="editURL">
				<portlet:param name="mvcRenderCommandName" value="/oauth_client_admin/update_oauth_client_pr_local_metadata" />
				<portlet:param name="redirect" value="<%= currentURL %>" />
				<portlet:param name="oAuthClientPRLocalMetadataId" value="<%= String.valueOf(oAuthClientPRLocalMetadata.getOAuthClientPRLocalMetadataId()) %>" />
			</portlet:renderURL>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				href="<%= editURL %>"
				name="oauth-client-pr-resource"
				property="resource"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="oauth-client-pr-local-well-known-uri"
				property="localWellKnownURI"
			/>

			<liferay-ui:search-container-column-jsp
				align="right"
				path="/admin/oauth_client_pr_local_metadata_actions.jsp"
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="list"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</clay:container-fluid>