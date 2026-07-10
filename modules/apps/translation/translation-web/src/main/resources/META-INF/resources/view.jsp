<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewDisplayContext viewDisplayContext = (ViewDisplayContext)request.getAttribute(ViewDisplayContext.class.getName());
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= viewDisplayContext.getTranslationEntryManagementToolbarDisplayContext() %>"
	propsTransformer="{TranslationManagementToolbarPropsTransformer} from translation-web"
/>

<clay:container-fluid
	cssClass="container-view"
>
	<aui:form action="<%= viewDisplayContext.getActionURL() %>" name="fm">
		<liferay-site-navigation:breadcrumb
			breadcrumbEntries="<%= BreadcrumbEntriesUtil.getBreadcrumbEntries(request, true, false, false, true, true) %>"
		/>

		<liferay-ui:search-container
			id="searchContainer"
			searchContainer="<%= viewDisplayContext.getSearchContainer() %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.translation.model.TranslationEntry"
				keyProperty="translationEntryId"
				modelVar="translationEntry"
			>

				<%
				row.setData(
					HashMapBuilder.<String, Object>put(
						"actions", viewDisplayContext.getAvailableActions(translationEntry)
					).build());
				%>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand table-title"
					href="<%= viewDisplayContext.getTranslatePortletURL(translationEntry) %>"
					name="title"
					value="<%= HtmlUtil.escape(viewDisplayContext.getTitle(translationEntry)) %>"
				/>

				<liferay-ui:search-container-column-text
					name="language"
				>
					<clay:icon
						symbol="<%= viewDisplayContext.getLanguageIcon(translationEntry) %>"
					/>

					<%= viewDisplayContext.getLanguageLabel(translationEntry) %>
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-status
					cssClass="table-cell-expand-smallest"
					name="status"
					property="status"
				/>

				<liferay-ui:search-container-column-date
					cssClass="table-cell-expand-smallest"
					name="modified-date"
					property="modifiedDate"
				/>

				<liferay-ui:search-container-column-text>
					<clay:dropdown-actions
						aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
						dropdownItems="<%= viewDisplayContext.getActionDropdownItems(translationEntry) %>"
						propsTransformer="{ElementsDefaultPropsTransformer} from translation-web"
					/>
				</liferay-ui:search-container-column-text>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>