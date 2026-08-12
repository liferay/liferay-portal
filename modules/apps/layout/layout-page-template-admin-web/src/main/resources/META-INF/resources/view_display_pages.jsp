<%--
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
DisplayPageDisplayContext displayPageDisplayContext = (DisplayPageDisplayContext)request.getAttribute(DisplayPageDisplayContext.class.getName());

if (displayPageDisplayContext == null) {
	InfoItemServiceRegistry infoItemServiceRegistry = (InfoItemServiceRegistry)request.getAttribute(InfoItemServiceRegistry.class.getName());

	displayPageDisplayContext = new DisplayPageDisplayContext(request, infoItemServiceRegistry, liferayPortletRequest, liferayPortletResponse);
}
%>

<clay:navigation-bar
	navigationItems="<%= layoutPageTemplatesAdminDisplayContext.getNavigationItems() %>"
/>

<liferay-ui:success key="displayPagePublished" message="the-display-page-template-was-published-successfully" />

<%
DisplayPageManagementToolbarDisplayContext displayPageManagementToolbarDisplayContext = new DisplayPageManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, displayPageDisplayContext);
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= displayPageManagementToolbarDisplayContext %>"
	propsTransformer="{DisplayPageManagementToolbarPropsTransformer} from layout-page-template-admin-web"
/>

<div class="closed sidenav-container sidenav-right" id="<portlet:namespace />infoPanelId">
	<liferay-portlet:resourceURL copyCurrentRenderParameters="<%= false %>" id="/layout_page_template_admin/info_panel" var="sidebarPanelURL">
		<portlet:param name="layoutPageTemplateCollectionId" value="<%= String.valueOf(displayPageDisplayContext.getLayoutPageTemplateCollectionId()) %>" />
	</liferay-portlet:resourceURL>

	<liferay-frontend:sidebar-panel
		resourceURL="<%= sidebarPanelURL %>"
		searchContainerId="<%= displayPageManagementToolbarDisplayContext.getSearchContainerId() %>"
		title='<%= LanguageUtil.get(request, "info-panel") %>'
	>
		<liferay-util:include page="/info_panel.jsp" servletContext="<%= application %>" />
	</liferay-frontend:sidebar-panel>

	<clay:container-fluid
		cssClass="container-view sidenav-content"
		size="xxxl"
	>
		<portlet:actionURL name="/layout_page_template_admin/delete_layout_page_template_entry" var="deleteDisplayPageURL">
			<portlet:param name="tabs1" value="display-page-templates" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
		</portlet:actionURL>

		<aui:form action="<%= deleteDisplayPageURL %>" cssClass="container-fluid container-fluid-max-xl" name="fm">
			<liferay-ui:error exception="<%= PortalException.class %>" message="one-or-more-entries-could-not-be-deleted" />
			<liferay-ui:error exception="<%= RequiredLayoutPageTemplateEntryException.class %>" message="you-cannot-delete-display-page-templates-that-are-used-by-one-or-more-items.-please-view-the-usages-and-try-to-unassign-them" />

			<liferay-ui:success key="displayPageContentTypeChanged" message='<%= GetterUtil.getString(SessionMessages.get(renderRequest, "displayPageContentTypeChanged")) %>' />
			<liferay-ui:success key="displayPageTemplateDeleted" message='<%= GetterUtil.getString(MultiSessionMessages.get(renderRequest, "displayPageTemplateDeleted")) %>' />

			<liferay-site-navigation:breadcrumb
				breadcrumbEntries="<%= displayPageDisplayContext.getLayoutPageTemplateBreadcrumbEntries() %>"
			/>

			<liferay-ui:search-container
				searchContainer="<%= displayPageDisplayContext.getDisplayPagesSearchContainer() %>"
			>
				<liferay-ui:search-container-row
					className="Object"
					modelVar="object"
				>

					<%
					LayoutPageTemplateCollection curLayoutPageTemplateCollection = null;
					LayoutPageTemplateEntry curLayoutPageTemplateEntry = null;

					Object result = row.getObject();

					if (result instanceof LayoutPageTemplateEntry) {
						curLayoutPageTemplateEntry = (LayoutPageTemplateEntry)result;
					}
					else {
						curLayoutPageTemplateCollection = (LayoutPageTemplateCollection)result;
					}
					%>

					<c:choose>
						<c:when test="<%= curLayoutPageTemplateCollection != null %>">

							<%
							row.setCssClass("card-page-item card-page-item-directory " + row.getCssClass());
							row.setData(
								HashMapBuilder.<String, Object>put(
									"actions", displayPageManagementToolbarDisplayContext.getAvailableLayoutPageTemplateCollectionActions(curLayoutPageTemplateCollection)
								).build());
							%>

							<liferay-ui:search-container-column-text
								colspan="<%= 2 %>"
							>
								<clay:horizontal-card
									horizontalCard="<%= new DisplayPageTemplateCollectionHorizontalCard (curLayoutPageTemplateCollection, renderRequest, renderResponse, searchContainer.getRowChecker()) %>"
									propsTransformer="{LayoutPageTemplateCollectionPropsTransformer} from layout-page-template-admin-web"
								/>
							</liferay-ui:search-container-column-text>
						</c:when>
						<c:when test="<%= curLayoutPageTemplateEntry != null %>">

							<%
							row.setData(
								HashMapBuilder.<String, Object>put(
									"actions", displayPageManagementToolbarDisplayContext.getAvailableLayoutPageTemplateEntryActions(curLayoutPageTemplateEntry)
								).build());
							%>

							<liferay-ui:search-container-column-text>
								<clay:vertical-card
									additionalProps='<%=
										HashMapBuilder.<String, Object>put(
											"mappingTypes", displayPageDisplayContext.getMappingTypesJSONArray()
										).build()
									%>'
									propsTransformer="{DisplayPageDropdownPropsTransformer} from layout-page-template-admin-web"
									verticalCard="<%= new DisplayPageVerticalCard(displayPageDisplayContext.isAllowedMappedContentType(curLayoutPageTemplateEntry), curLayoutPageTemplateEntry, displayPageDisplayContext.existsMappedContentType(curLayoutPageTemplateEntry), renderRequest, renderResponse, searchContainer.getRowChecker()) %>"
								/>
							</liferay-ui:search-container-column-text>
						</c:when>
					</c:choose>
				</liferay-ui:search-container-row>

				<liferay-ui:search-iterator
					displayStyle="icon"
					markupView="lexicon"
					resultRowSplitter="<%= displayPageDisplayContext.isSearch() ? null : new LayoutPageTemplateResultRowSplitter() %>"
				/>
			</liferay-ui:search-container>
		</aui:form>
	</clay:container-fluid>
</div>

<aui:form name="actionEntriesFm">
	<aui:input name="layoutPageTemplateCollectionsIds" type="hidden" />
	<aui:input name="layoutPageTemplateEntriesIds" type="hidden" />
	<aui:input name="layoutParentPageTemplateCollectionId" type="hidden" />
	<aui:input name="copyPermisisons" type="hidden" />
</aui:form>

<portlet:actionURL name="/layout_page_template_admin/update_layout_page_template_entry_preview" var="updateLayoutPageTemplateEntryPreviewURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<aui:form action="<%= updateLayoutPageTemplateEntryPreviewURL %>" name="layoutPageTemplateEntryPreviewFm">
	<aui:input name="layoutPageTemplateEntryId" type="hidden" />
	<aui:input name="fileEntryId" type="hidden" />
</aui:form>