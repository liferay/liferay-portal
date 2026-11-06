<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
LayoutPrototypeDisplayContext layoutPrototypeDisplayContext = new LayoutPrototypeDisplayContext(request, renderRequest, renderResponse);

portletDisplay.setShowStagingIcon(false);
%>

<clay:navigation-bar
	navigationItems="<%= layoutPageTemplatesAdminDisplayContext.getNavigationItems() %>"
/>

<%
LayoutPrototypeManagementToolbarDisplayContext layoutPrototypeManagementToolbarDisplayContext = new LayoutPrototypeManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, layoutPrototypeDisplayContext);
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= layoutPrototypeManagementToolbarDisplayContext %>"
	propsTransformer="{LayoutPrototypeManagementToolbarPropsTransformer} from layout-page-template-admin-web"
/>

<portlet:actionURL name="/layout_page_template_admin/delete_layout_prototype" var="deleteLayoutPrototypesURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<aui:form action="<%= deleteLayoutPrototypesURL %>" cssClass="container-fluid container-fluid-max-xxxl" name="fm">
	<liferay-ui:error embed="<%= false %>" exception="<%= RequiredLayoutPrototypeException.class %>" message="you-cannot-delete-page-templates-that-are-used-by-a-page" />

	<liferay-ui:search-container
		searchContainer="<%= layoutPrototypeDisplayContext.getSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.layout.page.template.model.LayoutPageTemplateEntry"
			escapedModel="<%= true %>"
			keyPropertyName="layoutPrototypeId"
			modelVar="layoutPageTemplateEntry"
		>

			<%
			LayoutPrototype layoutPrototype = LayoutPrototypeServiceUtil.getLayoutPrototype(layoutPageTemplateEntry.getLayoutPrototypeId());

			row.setData(
				HashMapBuilder.<String, Object>put(
					"actions", layoutPrototypeManagementToolbarDisplayContext.getAvailableActions(layoutPrototype)
				).build());
			%>

			<liferay-ui:search-container-column-text>
				<clay:vertical-card
					propsTransformer="{LayoutPrototypeDropdownPropsTransformer} from layout-page-template-admin-web"
					verticalCard="<%= new LayoutPrototypeVerticalCard(layoutPrototype, renderRequest, renderResponse, searchContainer.getRowChecker()) %>"
				/>
			</liferay-ui:search-container-column-text>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="icon"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>