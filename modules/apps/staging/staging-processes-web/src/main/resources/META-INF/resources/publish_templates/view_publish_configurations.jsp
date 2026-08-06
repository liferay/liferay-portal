<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<c:choose>
	<c:when test='<%= !FeatureFlagManagerUtil.isEnabled(themeDisplay.getCompanyId(), "LPD-101272") %>'>
		<liferay-util:include page="/view.jsp" servletContext="<%= application %>" />
	</c:when>
	<c:otherwise>

		<%
		long layoutSetBranchId = ParamUtil.getLong(request, "layoutSetBranchId");
		String layoutSetBranchName = ParamUtil.getString(request, "layoutSetBranchName");

		portletDisplay.setShowBackIcon(true);

		portletDisplay.setURLBack(
			PortletURLBuilder.create(
				PortalUtil.getControlPanelPortletURL(request, StagingProcessesPortletKeys.STAGING_PROCESSES, PortletRequest.RENDER_PHASE)
			).setMVCPath(
				"/view.jsp"
			).buildString());

		renderResponse.setTitle(LanguageUtil.get(request, "publish-templates"));
		%>

		<portlet:actionURL name="/staging_processes/edit_publish_configuration" var="restoreTrashEntriesURL">
			<portlet:param name="mvcRenderCommandName" value="/staging_processes/view_publish_configurations" />
			<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.RESTORE %>" />
		</portlet:actionURL>

		<liferay-trash:undo
			portletURL="<%= restoreTrashEntriesURL %>"
		/>

		<liferay-portlet:renderURL varImpl="portletURL">
			<portlet:param name="mvcRenderCommandName" value="/staging_processes/view_publish_configurations" />
			<portlet:param name="groupId" value="<%= String.valueOf(groupId) %>" />
			<portlet:param name="layoutSetBranchId" value="<%= String.valueOf(layoutSetBranchId) %>" />
			<portlet:param name="layoutSetBranchName" value="<%= layoutSetBranchName %>" />
			<portlet:param name="privateLayout" value="<%= String.valueOf(privateLayout) %>" />
		</liferay-portlet:renderURL>

		<%
		StagingProcessesWebPublishTemplatesToolbarDisplayContext stagingProcessesWebPublishTemplatesToolbarDisplayContext = new StagingProcessesWebPublishTemplatesToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, pageContext, portletURL);
		%>

		<clay:navigation-bar
			navigationItems="<%= publishTemplatesDisplayContext.getNavigationItems() %>"
		/>

		<clay:management-toolbar
			managementToolbarDisplayContext="<%= stagingProcessesWebPublishTemplatesToolbarDisplayContext %>"
			searchFormName="searchFm"
			selectable="<%= false %>"
			showCreationMenu="<%= true %>"
			showSearch="<%= true %>"
		/>

		<clay:container-fluid
			cssClass="closed sidenav-container sidenav-right"
			id='<%= liferayPortletResponse.getNamespace() + "infoPanelId" %>'
		>
			<liferay-site-navigation:breadcrumb
				breadcrumbEntries="<%= BreadcrumbEntriesUtil.getBreadcrumbEntries(request, true, false, false, true, true) %>"
			/>

			<aui:form action="<%= portletURL %>">
				<liferay-ui:search-container
					searchContainer="<%= stagingProcessesWebPublishTemplatesToolbarDisplayContext.getSearchContainer() %>"
				>
					<liferay-ui:search-container-row
						className="com.liferay.exportimport.kernel.model.ExportImportConfiguration"
						keyProperty="exportImportConfigurationId"
						modelVar="exportImportConfiguration"
					>
						<liferay-ui:search-container-column-text
							cssClass="background-task-user-column"
							name="user"
						>
							<liferay-user:user-portrait
								userId="<%= exportImportConfiguration.getUserId() %>"
							/>
						</liferay-ui:search-container-column-text>

						<liferay-portlet:renderURL varImpl="rowURL">
							<portlet:param name="mvcRenderCommandName" value="/staging_processes/edit_publish_configuration" />
							<portlet:param name="redirect" value="<%= searchContainer.getIteratorURL().toString() %>" />
							<portlet:param name="exportImportConfigurationId" value="<%= String.valueOf(exportImportConfiguration.getExportImportConfigurationId()) %>" />
							<portlet:param name="groupId" value="<%= String.valueOf(groupId) %>" />
							<portlet:param name="layoutSetBranchId" value="<%= String.valueOf(layoutSetBranchId) %>" />
							<portlet:param name="layoutSetBranchName" value="<%= layoutSetBranchName %>" />
							<portlet:param name="privateLayout" value="<%= String.valueOf(privateLayout) %>" />
						</liferay-portlet:renderURL>

						<liferay-ui:search-container-column-text
							href="<%= rowURL %>"
							name="title"
							value="<%= HtmlUtil.escape(exportImportConfiguration.getName()) %>"
						/>

						<liferay-ui:search-container-column-text
							name="description"
							value="<%= HtmlUtil.escape(exportImportConfiguration.getDescription()) %>"
						/>

						<liferay-ui:search-container-column-date
							name="create-date"
							value="<%= exportImportConfiguration.getCreateDate() %>"
						/>

						<%
						request.setAttribute("view.jsp-layoutSetBranchId", layoutSetBranchId);
						request.setAttribute("view.jsp-layoutSetBranchName", layoutSetBranchName);
						%>

						<liferay-ui:search-container-column-jsp
							align="right"
							cssClass="entry-action"
							path="/publish_templates/actions.jsp"
						/>
					</liferay-ui:search-container-row>

					<liferay-ui:search-iterator
						markupView="lexicon"
					/>
				</liferay-ui:search-container>
			</aui:form>
		</clay:container-fluid>
	</c:otherwise>
</c:choose>