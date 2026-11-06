<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
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
	propsTransformer="{OAuthClientPRLocalMetadatasManagementToolbarPropsTransformer} from oauth-client-admin-web"
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
	<aui:form action="<%= currentURLObj %>" method="get" name="fm">
		<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
		<aui:input name="oAuthClientPRLocalMetadataIds" type="hidden" />

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
				keyPropertyName="OAuthClientPRLocalMetadataId"
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
					name="resource"
					property="protectedResourceURI"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand"
					name="oauth-client-pr-local-well-known-uri"
					property="localWellKnownURI"
				/>

				<liferay-ui:search-container-column-text>

					<%
					OAuthClientPRLocalMetadataActionDropdownItemsProvider oAuthClientPRLocalMetadataActionDropdownItemsProvider = new OAuthClientPRLocalMetadataActionDropdownItemsProvider(oAuthClientPRLocalMetadata, renderRequest, renderResponse);
					%>

					<clay:dropdown-actions
						aria-label='<%= LanguageUtil.format(request, "actions-for-x", oAuthClientPRLocalMetadata.getProtectedResourceURI()) %>'
						dropdownItems="<%= oAuthClientPRLocalMetadataActionDropdownItemsProvider.getActionDropdownItems() %>"
						propsTransformer="{OAuthClientPRLocalMetadataActionDropdownPropsTransformer} from oauth-client-admin-web"
					/>
				</liferay-ui:search-container-column-text>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				displayStyle="list"
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>