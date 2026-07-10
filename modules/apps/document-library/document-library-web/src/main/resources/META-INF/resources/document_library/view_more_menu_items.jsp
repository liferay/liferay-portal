<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/document_library/init.jsp" %>

<%
long folderId = ParamUtil.getLong(request, "folderId");

DLViewMoreMenuItemsDisplayContext dlViewMoreMenuItemsDisplayContext = new DLViewMoreMenuItemsDisplayContext(folderId, renderRequest, renderResponse);
%>

<clay:navigation-bar
	navigationItems="<%= dlViewMoreMenuItemsDisplayContext.getNavigationItems() %>"
/>

<clay:management-toolbar
	clearResultsURL="<%= dlViewMoreMenuItemsDisplayContext.getClearResultsURL() %>"
	disabled="<%= dlViewMoreMenuItemsDisplayContext.getTotalItems() == 0 %>"
	itemsTotal="<%= dlViewMoreMenuItemsDisplayContext.getTotalItems() %>"
	searchActionURL="<%= dlViewMoreMenuItemsDisplayContext.getSearchActionURL() %>"
	searchFormName="fm"
	selectable="<%= false %>"
/>

<aui:form cssClass="container-fluid container-fluid-max-xxxl" name="addMenuItemFm">
	<liferay-ui:search-container
		searchContainer="<%= dlViewMoreMenuItemsDisplayContext.getSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.document.library.kernel.model.DLFileEntryType"
			escapedModel="<%= true %>"
			keyProperty="fileEntryTypeId"
			modelVar="fileEntryType"
		>
			<liferay-ui:search-container-column-text
				name="name"
			>
				<aui:a
					cssClass="selector-button"
					data='<%=
						HashMapBuilder.<String, Object>put(
							"fileEntryTypeId", String.valueOf(fileEntryType.getFileEntryTypeId())
						).build()
					%>'
					href="javascript:void(0);"
				>
					<%= HtmlUtil.escape(fileEntryType.getName(locale)) %>
				</aui:a>
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-text
				name="scope"
				value="<%= HtmlUtil.escape(dlViewMoreMenuItemsDisplayContext.getDLFileEntryTypeScopeName(fileEntryType, locale)) %>"
			/>

			<liferay-ui:search-container-column-text
				name="description"
				value="<%= HtmlUtil.escape(fileEntryType.getDescription(locale)) %>"
			/>

			<liferay-ui:search-container-column-date
				name="modified-date"
				value="<%= fileEntryType.getModifiedDate() %>"
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>