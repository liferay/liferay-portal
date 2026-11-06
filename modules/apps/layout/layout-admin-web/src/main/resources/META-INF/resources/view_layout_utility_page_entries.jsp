<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
LayoutUtilityPageEntryDisplayContext layoutUtilityPageEntryDisplayContext = (LayoutUtilityPageEntryDisplayContext)request.getAttribute(LayoutUtilityPageEntryDisplayContext.class.getName());
%>

<liferay-ui:success key="layoutUpdated" message="the-page-was-updated-successfully" />

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new LayoutUtilityPageEntryManagementToolbarDisplayContext(request, layoutUtilityPageEntryDisplayContext, liferayPortletRequest, liferayPortletResponse) %>"
	propsTransformer="{LayoutUtilityPageEntriesManagementToolbarPropsTransformer} from layout-admin-web"
/>

<aui:form cssClass="container-fluid container-fluid-max-xl container-view" name="fm">
	<liferay-ui:success key="layoutPublished" message="the-utility-page-was-published-successfully" />
	<liferay-ui:success key="layoutUtilityPageDeleted" message='<%= GetterUtil.getString(MultiSessionMessages.get(renderRequest, "layoutUtilityPageDeleted")) %>' />

	<liferay-ui:search-container
		id="entries"
		searchContainer="<%= layoutUtilityPageEntryDisplayContext.getLayoutUtilityPageEntrySearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.layout.utility.page.model.LayoutUtilityPageEntry"
			keyPropertyName="layoutUtilityPageEntryId"
			modelVar="layoutUtilityPageEntry"
		>

			<%
			row.setData(
				HashMapBuilder.<String, Object>put(
					"actions", layoutUtilityPageEntryDisplayContext.getAvailableActions(layoutUtilityPageEntry)
				).build());
			%>

			<liferay-ui:search-container-column-text>
				<clay:vertical-card
					propsTransformer="{LayoutUtilityPageEntryDropdownPropsTransformer} from layout-admin-web"
					verticalCard="<%= new LayoutUtilityPageEntryVerticalCard(layoutUtilityPageEntry, renderRequest, renderResponse, searchContainer.getRowChecker()) %>"
				/>
			</liferay-ui:search-container-column-text>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="icon"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>

<portlet:actionURL name="/layout_admin/update_layout_utility_page_entry_preview" var="updateLayoutUtilityPageEntryPreviewURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<aui:form action="<%= updateLayoutUtilityPageEntryPreviewURL %>" name="layoutUtilityPageEntryPreviewFm">
	<aui:input name="layoutUtilityPageEntryId" type="hidden" />
	<aui:input name="fileEntryId" type="hidden" />
</aui:form>