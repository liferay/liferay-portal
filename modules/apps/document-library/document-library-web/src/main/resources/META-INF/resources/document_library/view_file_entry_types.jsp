<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/document_library/init.jsp" %>

<%
DLViewFileEntryTypesDisplayContext dlViewFileEntryTypesDisplayContext = new DLViewFileEntryTypesDisplayContext(request, renderRequest, renderResponse);
%>

<liferay-util:include page="/document_library/navigation.jsp" servletContext="<%= application %>" />

<clay:management-toolbar
	clearResultsURL="<%= dlViewFileEntryTypesDisplayContext.getClearResultsURL() %>"
	creationMenu="<%= dlViewFileEntryTypesDisplayContext.getCreationMenu() %>"
	disabled="<%= dlViewFileEntryTypesDisplayContext.isSearchDisabled() %>"
	itemsTotal="<%= dlViewFileEntryTypesDisplayContext.getTotalItems() %>"
	searchActionURL="<%= dlViewFileEntryTypesDisplayContext.getSearchActionURL() %>"
	searchFormName="fm"
	selectable="<%= false %>"
/>

<clay:container-fluid
	size="xxxl"
>
	<liferay-site-navigation:breadcrumb
		breadcrumbEntries="<%= BreadcrumbEntriesUtil.getBreadcrumbEntries(request, true, false, false, true, true) %>"
	/>

	<liferay-ui:error exception="<%= RequiredFileEntryTypeException.class %>" message="cannot-delete-a-document-type-that-is-presently-used-by-one-or-more-documents" />

	<liferay-ui:search-container
		searchContainer="<%= dlViewFileEntryTypesDisplayContext.getSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.document.library.kernel.model.DLFileEntryType"
			escapedModel="<%= true %>"
			keyPropertyName="fileEntryTypeId"
			modelVar="fileEntryType"
		>

			<%
			PortletURL rowURL = PortletURLBuilder.createRenderURL(
				liferayPortletResponse
			).setMVCRenderCommandName(
				"/document_library/edit_file_entry_type"
			).setRedirect(
				currentURL
			).setParameter(
				"fileEntryTypeId", fileEntryType.getFileEntryTypeId()
			).buildPortletURL();
			%>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand table-cell-minw-200 table-title"
				href="<%= DLFileEntryTypePermission.contains(permissionChecker, fileEntryType, ActionKeys.UPDATE) ? rowURL : null %>"
				name="name"
				value="<%= fileEntryType.getName(locale) %>"
			/>

			<%
			Group group = GroupLocalServiceUtil.getGroup(fileEntryType.getGroupId());
			%>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand-small table-cell-minw-150"
				name="scope"
				value="<%= LanguageUtil.get(request, group.getScopeLabel(themeDisplay)) %>"
			/>

			<liferay-ui:search-container-column-date
				cssClass="table-cell-expand-small table-cell-ws-nowrap"
				name="modified-date"
				value="<%= fileEntryType.getModifiedDate() %>"
			/>

			<liferay-ui:search-container-column-jsp
				cssClass="entry-action"
				path="/document_library/file_entry_type_action.jsp"
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</clay:container-fluid>