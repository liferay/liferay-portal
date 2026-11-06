<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
InformationTemplatesTemplateDisplayContext informationTemplatesTemplateDisplayContext = new InformationTemplatesTemplateDisplayContext(liferayPortletRequest, liferayPortletResponse);

InformationTemplatesManagementToolbarDisplayContext informationTemplatesManagementToolbarDisplayContext = new InformationTemplatesManagementToolbarDisplayContext(request, informationTemplatesTemplateDisplayContext, liferayPortletRequest, liferayPortletResponse);
%>

<clay:navigation-bar
	navigationItems="<%= informationTemplatesTemplateDisplayContext.getNavigationItems() %>"
/>

<clay:management-toolbar
	additionalProps="<%= informationTemplatesManagementToolbarDisplayContext.getAdditionalProps() %>"
	managementToolbarDisplayContext="<%= informationTemplatesManagementToolbarDisplayContext %>"
	propsTransformer="{InformationTemplatesManagementToolbarPropsTransformer} from template-web"
/>

<portlet:actionURL name="/template/delete_template_entry" var="deleteTemplateEntryURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<clay:container-fluid>
	<aui:form action="<%= deleteTemplateEntryURL %>" name="fm">
		<liferay-ui:search-container
			id="<%= informationTemplatesManagementToolbarDisplayContext.getSearchContainerId() %>"
			searchContainer="<%= informationTemplatesTemplateDisplayContext.getTemplateSearchContainer() %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.template.model.TemplateEntry"
				keyPropertyName="templateEntryId"
				modelVar="templateEntry"
			>

				<%
				row.setData(
					HashMapBuilder.<String, Object>put(
						"actions", informationTemplatesManagementToolbarDisplayContext.getAvailableActions(templateEntry)
					).build());

				DDMTemplate ddmTemplate = DDMTemplateLocalServiceUtil.fetchDDMTemplate(templateEntry.getDDMTemplateId());
				%>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand table-title"
					href="<%= informationTemplatesTemplateDisplayContext.getTemplateEntryEditURL(templateEntry) %>"
					name="name"
					value="<%= HtmlUtil.escape(ddmTemplate.getName(locale)) %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand"
					name="item-type"
					value="<%= HtmlUtil.escape(informationTemplatesTemplateDisplayContext.getTemplateTypeLabel(templateEntry)) %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand"
					name="item-subtype"
					value="<%= HtmlUtil.escape(informationTemplatesTemplateDisplayContext.getTemplateSubtypeLabel(templateEntry)) %>"
				/>

				<liferay-ui:search-container-column-text>
					<clay:dropdown-actions
						aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
						dropdownItems="<%= informationTemplatesTemplateDisplayContext.getTemplateEntryActionDropdownItems(templateEntry) %>"
						propsTransformer="{InformationTemplatesDropdownPropsTransformer} from template-web"
					/>
				</liferay-ui:search-container-column-text>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>