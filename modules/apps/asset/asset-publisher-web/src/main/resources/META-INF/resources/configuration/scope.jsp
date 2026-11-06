<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
PortletURL configurationRenderURL = (PortletURL)request.getAttribute("configuration.jsp-configurationRenderURL");

List<Group> selectedGroups = GroupLocalServiceUtil.getGroups(assetPublisherDisplayContext.getGroupIds());
%>

<liferay-ui:search-container
	compactEmptyResultsMessage="<%= true %>"
	emptyResultsMessage="none"
	iteratorURL="<%= configurationRenderURL %>"
	total="<%= selectedGroups.size() %>"
>
	<liferay-ui:search-container-results
		results="<%= selectedGroups %>"
	/>

	<liferay-ui:search-container-row
		className="com.liferay.portal.kernel.model.Group"
		keyPropertyName="groupId"
		modelVar="group"
	>
		<liferay-ui:search-container-column-text
			name="name"
			truncate="<%= true %>"
			value="<%= group.getScopeDescriptiveName(themeDisplay) %>"
		/>

		<liferay-ui:search-container-column-text
			name="type"
			value="<%= LanguageUtil.get(request, group.getScopeLabel(themeDisplay)) %>"
		/>

		<liferay-ui:search-container-column-text>
			<liferay-portlet:actionURL portletConfiguration="<%= true %>" var="deleteURL">
				<portlet:param name="<%= Constants.CMD %>" value="remove-scope" />
				<portlet:param name="redirect" value="<%= configurationRenderURL.toString() %>" />
				<portlet:param name="scopeId" value="<%= assetPublisherHelper.getScopeId(group, scopeGroupId) %>" />
			</liferay-portlet:actionURL>

			<clay:link
				aria-label='<%= LanguageUtil.get(request, "delete") %>'
				cssClass="lfr-portal-tooltip"
				href="<%= deleteURL %>"
				icon="times-circle"
				title='<%= LanguageUtil.get(request, "delete") %>'
			/>
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		markupView="lexicon"
		paginate="<%= false %>"
	/>
</liferay-ui:search-container>

<liferay-portlet:actionURL portletConfiguration="<%= true %>" varImpl="addScopeURL">
	<portlet:param name="<%= Constants.CMD %>" value="add-scope" />
	<portlet:param name="redirect" value="<%= configurationRenderURL.toString() %>" />
</liferay-portlet:actionURL>

<clay:dropdown-menu
	displayType="secondary"
	dropdownItems="<%= assetPublisherDisplayContext.getScopeDropdownItems(addScopeURL) %>"
	label="select"
	propsTransformer="{ScopeActionDropdownPropsTransformer} from asset-publisher-web"
/>