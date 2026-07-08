<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/export/init.jsp" %>

<c:choose>
	<c:when test='<%= !FeatureFlagManagerUtil.isEnabled(themeDisplay.getCompanyId(), "LPD-96546") %>'>
		<liferay-util:include page="/export/view_export_layouts.jsp" servletContext="<%= application %>" />
	</c:when>
	<c:otherwise>

		<%
		portletDisplay.setShowBackIcon(true);

		portletDisplay.setURLBack(
			PortletURLBuilder.create(
				PortalUtil.getControlPanelPortletURL(request, portletDisplay.getPortletName(), PortletRequest.RENDER_PHASE)
			).setMVCPath(
				"/export/view_export_layouts.jsp"
			).buildString());

		renderResponse.setTitle(LanguageUtil.get(request, "export-templates"));
		%>

		<liferay-staging:defineObjects />

		<%
		if (liveGroup == null) {
			liveGroup = group;
			liveGroupId = groupId;
		}
		%>

		<liferay-util:include page="/export/export_templates/navigation.jsp" servletContext="<%= application %>" />

		<liferay-portlet:renderURL varImpl="portletURL">
			<portlet:param name="mvcRenderCommandName" value="/export_import/view_export_configurations" />
			<portlet:param name="groupId" value="<%= String.valueOf(groupId) %>" />
			<portlet:param name="liveGroupId" value="<%= String.valueOf(liveGroupId) %>" />
			<portlet:param name="privateLayout" value="<%= String.valueOf(privateLayout) %>" />
		</liferay-portlet:renderURL>

		<portlet:actionURL name="/export_import/edit_export_configuration" var="restoreTrashEntriesURL">
			<portlet:param name="mvcRenderCommandName" value="/export_import/view_export_configurations" />
			<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.RESTORE %>" />
		</portlet:actionURL>

		<liferay-trash:undo
			portletURL="<%= restoreTrashEntriesURL %>"
		/>

		<%
		ExportTemplatesToolbarDisplayContext exportTemplatesToolbarDisplayContext = new ExportTemplatesToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, liveGroupId, company, portletURL);
		%>

		<clay:management-toolbar
			managementToolbarDisplayContext="<%= exportTemplatesToolbarDisplayContext %>"
			searchFormName="searchFm"
			selectable="<%= false %>"
			showCreationMenu="<%= true %>"
			showSearch="<%= true %>"
		/>

		<clay:container-fluid>
			<aui:form action="<%= portletURL %>">
				<liferay-ui:search-container
					searchContainer="<%= exportTemplatesToolbarDisplayContext.getSearchContainer() %>"
				>
					<liferay-ui:search-container-row
						className="com.liferay.exportimport.kernel.model.ExportImportConfiguration"
						keyProperty="exportImportConfigurationId"
						modelVar="exportImportConfiguration"
					>
						<liferay-ui:search-container-column-text
							cssClass="export-configuration-user-column"
							name="user"
						>
							<liferay-user:user-portrait
								userId="<%= exportImportConfiguration.getUserId() %>"
							/>
						</liferay-ui:search-container-column-text>

						<liferay-portlet:renderURL varImpl="rowURL">
							<portlet:param name="mvcRenderCommandName" value="/export_import/edit_export_configuration" />
							<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.UPDATE %>" />
							<portlet:param name="redirect" value="<%= searchContainer.getIteratorURL().toString() %>" />
							<portlet:param name="exportImportConfigurationId" value="<%= String.valueOf(exportImportConfiguration.getExportImportConfigurationId()) %>" />
							<portlet:param name="groupId" value="<%= String.valueOf(groupId) %>" />
							<portlet:param name="liveGroupId" value="<%= String.valueOf(liveGroupId) %>" />
							<portlet:param name="privateLayout" value="<%= String.valueOf(privateLayout) %>" />
						</liferay-portlet:renderURL>

						<liferay-ui:search-container-column-text
							cssClass="table-cell-expand"
							href="<%= rowURL %>"
							name="title"
							value="<%= HtmlUtil.escape(exportImportConfiguration.getName()) %>"
						/>

						<liferay-ui:search-container-column-text
							cssClass="table-cell-expand"
							name="description"
							value="<%= HtmlUtil.escape(exportImportConfiguration.getDescription()) %>"
						/>

						<liferay-ui:search-container-column-date
							name="create-date"
							value="<%= exportImportConfiguration.getCreateDate() %>"
						/>

						<%
						request.setAttribute("view.jsp-groupId", groupId);
						request.setAttribute("view.jsp-liveGroupId", liveGroupId);
						request.setAttribute("view.jsp-privateLayout", privateLayout);
						%>

						<liferay-ui:search-container-column-jsp
							path="/export/export_templates/actions.jsp"
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