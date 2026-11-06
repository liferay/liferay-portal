<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/browser/init.jsp" %>

<clay:navigation-bar
	navigationItems="<%= ddmFormBrowserDisplayContext.getNavigationItems() %>"
/>

<clay:management-toolbar
	clearResultsURL="<%= ddmFormBrowserDisplayContext.getClearResultsURL() %>"
	disabled="<%= ddmFormBrowserDisplayContext.isDisabledManagementBar() %>"
	itemsTotal="<%= ddmFormBrowserDisplayContext.getTotalItems() %>"
	orderDropdownItems="<%= ddmFormBrowserDisplayContext.getOrderItemsDropdownItems() %>"
	searchActionURL="<%= ddmFormBrowserDisplayContext.getSearchActionURL() %>"
	searchContainerId="<%= ddmFormBrowserDisplayContext.getSearchContainerId() %>"
	searchFormName="searchFm"
	sortingOrder="<%= ddmFormBrowserDisplayContext.getOrderByType() %>"
	sortingURL="<%= ddmFormBrowserDisplayContext.getSortingURL() %>"
/>

<clay:container-fluid
	id='<%= liferayPortletResponse.getNamespace() + "formContainer" %>'
>
	<aui:form action="<%= String.valueOf(ddmFormBrowserDisplayContext.getPortletURL()) %>" method="post" name="selectDDMFormFm">
		<liferay-ui:search-container
			id="<%= ddmFormBrowserDisplayContext.getSearchContainerId() %>"
			searchContainer="<%= ddmFormBrowserDisplayContext.getDDMFormInstanceSearch() %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.dynamic.data.mapping.model.DDMFormInstance"
				keyPropertyName="formInstanceId"
				modelVar="formInstance"
			>
				<liferay-ui:search-container-column-text
					cssClass="content-column title-column"
					name="name"
					truncate="<%= true %>"
				>
					<aui:a
						cssClass="selector-button"
						data='<%=
							HashMapBuilder.<String, Object>put(
								"forminstanceid", formInstance.getFormInstanceId()
							).put(
								"forminstancename", formInstance.getName(locale)
							).build()
						%>'
						href="javascript:void(0);"
					>
						<%= HtmlUtil.escape(formInstance.getName(locale)) %>
					</aui:a>
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-text
					cssClass="content-column"
					name="description"
					truncate="<%= true %>"
					value="<%= HtmlUtil.escape(formInstance.getDescription(locale)) %>"
				/>

				<liferay-ui:search-container-column-date
					cssClass="modified-date-column text-column"
					name="modified-date"
					value="<%= formInstance.getModifiedDate() %>"
				/>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				displayStyle="<%= ddmFormBrowserDisplayContext.getDisplayStyle() %>"
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>