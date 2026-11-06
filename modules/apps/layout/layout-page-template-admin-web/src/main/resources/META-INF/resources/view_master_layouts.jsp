<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
MasterLayoutDisplayContext masterLayoutDisplayContext = new MasterLayoutDisplayContext(request, renderRequest, renderResponse);
%>

<clay:navigation-bar
	navigationItems="<%= layoutPageTemplatesAdminDisplayContext.getNavigationItems() %>"
/>

<%
MasterLayoutManagementToolbarDisplayContext masterLayoutManagementToolbarDisplayContext = new MasterLayoutManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, masterLayoutDisplayContext);
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= masterLayoutManagementToolbarDisplayContext %>"
	propsTransformer="{MasterLayoutManagementToolbarPropsTransformer} from layout-page-template-admin-web"
/>

<liferay-ui:success key="masterPagePublished" message="the-master-page-was-published-successfully" />

<portlet:actionURL name="/layout_page_template_admin/delete_master_layout" var="deleteMasterLayoutURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<aui:form action="<%= deleteMasterLayoutURL %>" cssClass="container-fluid container-fluid-max-xxxl" name="fm">
	<liferay-ui:error key="<%= PortalException.class.getName() %>" message="one-or-more-entries-could-not-be-deleted" />
	<liferay-ui:error key="<%= RequiredLayoutPageTemplateEntryException.class.getName() %>" message="you-cannot-delete-master-pages-that-are-used-by-a-page,-a-page-template,-or-a-display-page-template" />

	<liferay-ui:search-container
		id="<%= masterLayoutManagementToolbarDisplayContext.getSearchContainerId() %>"
		searchContainer="<%= masterLayoutDisplayContext.getMasterLayoutsSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.layout.page.template.model.LayoutPageTemplateEntry"
			escapedModel="<%= true %>"
			keyPropertyName="layoutPrototypeId"
			modelVar="layoutPageTemplateEntry"
		>

			<%
			row.setData(
				HashMapBuilder.<String, Object>put(
					"actions", masterLayoutManagementToolbarDisplayContext.getAvailableActions(layoutPageTemplateEntry)
				).build());
			%>

			<liferay-ui:search-container-column-text>
				<clay:vertical-card
					propsTransformer="{MasterLayoutDropdownPropsTransformer} from layout-page-template-admin-web"
					verticalCard="<%= new MasterLayoutVerticalCard(layoutPageTemplateEntry, renderRequest, renderResponse, searchContainer.getRowChecker()) %>"
				/>
			</liferay-ui:search-container-column-text>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="icon"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>

<portlet:actionURL name="/layout_page_template_admin/update_layout_page_template_entry_preview" var="masterLayoutPreviewURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<aui:form action="<%= masterLayoutPreviewURL %>" name="masterLayoutPreviewFm">
	<aui:input name="layoutPageTemplateEntryId" type="hidden" />
	<aui:input name="fileEntryId" type="hidden" />
</aui:form>