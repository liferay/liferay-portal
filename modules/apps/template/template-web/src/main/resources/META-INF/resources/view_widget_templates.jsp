<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
WidgetTemplatesTemplateDisplayContext widgetTemplatesTemplateDisplayContext = new WidgetTemplatesTemplateDisplayContext(liferayPortletRequest, liferayPortletResponse);

WidgetTemplatesManagementToolbarDisplayContext widgetTemplatesManagementToolbarDisplayContext = new WidgetTemplatesManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, widgetTemplatesTemplateDisplayContext);
%>

<clay:navigation-bar
	navigationItems="<%= widgetTemplatesTemplateDisplayContext.getNavigationItems() %>"
/>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= widgetTemplatesManagementToolbarDisplayContext %>"
	propsTransformer="{WidgetTemplatesManagementToolbarPropsTransformer} from template-web"
/>

<portlet:actionURL name="/template/delete_ddm_template" var="deleteDDMTemplateURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<clay:container-fluid>
	<aui:form action="<%= deleteDDMTemplateURL %>" name="fm">
		<liferay-ui:search-container
			id="<%= widgetTemplatesManagementToolbarDisplayContext.getSearchContainerId() %>"
			searchContainer="<%= widgetTemplatesTemplateDisplayContext.getTemplateSearchContainer() %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.dynamic.data.mapping.model.DDMTemplate"
				keyPropertyName="templateId"
				modelVar="ddmTemplate"
			>

				<%
				row.setData(
					HashMapBuilder.<String, Object>put(
						"actions", widgetTemplatesManagementToolbarDisplayContext.getAvailableActions(ddmTemplate)
					).build());
				%>

				<liferay-ui:search-container-column-text
					name="id"
					property="templateId"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand table-title"
					href="<%= widgetTemplatesTemplateDisplayContext.getDDMTemplateEditURL(ddmTemplate) %>"
					name="name"
					value="<%= HtmlUtil.escape(ddmTemplate.getName(locale)) %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand"
					name="description"
					value="<%= HtmlUtil.escape(ddmTemplate.getDescription(locale)) %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-smallest"
					name="type"
					value="<%= HtmlUtil.escape(widgetTemplatesTemplateDisplayContext.getTemplateTypeLabel(ddmTemplate.getClassNameId())) %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-smallest"
					name="scope"
					value="<%= HtmlUtil.escape(widgetTemplatesTemplateDisplayContext.getDDMTemplateScope(ddmTemplate)) %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-smallest"
					name="usages"
					value='<%= String.valueOf(PortletPreferenceValueLocalServiceUtil.getPortletPreferenceValuesCount(ddmTemplate.getCompanyId(), "displayStyle", PortletDisplayTemplate.DISPLAY_STYLE_PREFIX + HtmlUtil.escape(ddmTemplate.getTemplateKey()))) %>'
				/>

				<liferay-ui:search-container-column-date
					name="modified-date"
					value="<%= ddmTemplate.getModifiedDate() %>"
				/>

				<liferay-ui:search-container-column-text>
					<clay:dropdown-actions
						aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
						dropdownItems="<%= widgetTemplatesTemplateDisplayContext.getDDMTemplateActionDropdownItems(ddmTemplate) %>"
						propsTransformer="{WidgetTemplatesDropdownPropsTransformer} from template-web"
					/>
				</liferay-ui:search-container-column-text>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>