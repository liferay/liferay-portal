<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
LayoutPageTemplateCollectionsDisplayContext layoutPageTemplateCollectionsDisplayContext = new LayoutPageTemplateCollectionsDisplayContext(request, renderRequest, renderResponse);
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new LayoutPageTemplateCollectionsManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, layoutPageTemplateCollectionsDisplayContext) %>"
/>

<aui:form cssClass="container-fluid container-fluid-max-xxxl" name="fm">
	<liferay-ui:search-container
		id="layoutPageTemplateCollections"
		searchContainer="<%= layoutPageTemplateCollectionsDisplayContext.getSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.layout.page.template.model.LayoutPageTemplateCollection"
			keyPropertyName="layoutPageTemplateCollectionId"
			modelVar="layoutPageTemplateCollection"
		>
			<liferay-ui:search-container-column-text
				name="name"
				truncate="<%= true %>"
				value="<%= HtmlUtil.escape(layoutPageTemplateCollection.getName()) %>"
			/>

			<liferay-ui:search-container-column-date
				name="create-date"
				property="createDate"
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="list"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>