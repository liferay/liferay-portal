<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
SelectSiteNavigationMenuDisplayContext selectSiteNavigationMenuDisplayContext = (SelectSiteNavigationMenuDisplayContext)request.getAttribute(SiteNavigationItemSelectorWebKeys.SELECT_SITE_NAVIGATION_ITEM_SELECTOR_DISPLAY_CONTEXT);
%>

<div class="container-fluid p-4" id="<portlet:namespace />siteNavigationMenuLevelSelector">
	<clay:alert
		displayType="info"
		message="select-the-page-level-of-the-navigation-menu-to-be-displayed"
	/>

	<div class="align-items-center d-flex justify-content-between">
		<liferay-site-navigation:breadcrumb
			breadcrumbEntries="<%= selectSiteNavigationMenuDisplayContext.getBreadcrumbEntries() %>"
		/>

		<clay:button
			cssClass="site-navigation-menu-selector"
			data-parent-site-navigation-menu-item-id="<%= selectSiteNavigationMenuDisplayContext.getParentSiteNavigationMenuItemId() %>"
			data-private-layout="<%= selectSiteNavigationMenuDisplayContext.isPrivateLayout() %>"
			data-site-navigation-menu-external-reference-code="<%= selectSiteNavigationMenuDisplayContext.getSiteNavigationMenuExternalReferenceCode() %>"
			data-site-navigation-menu-id="<%= selectSiteNavigationMenuDisplayContext.getSiteNavigationMenuId() %>"
			data-title="<%= selectSiteNavigationMenuDisplayContext.getCurrentLevelTitle() %>"
			displayType="primary"
			label="select-this-level"
			small="<%= true %>"
		/>
	</div>

	<liferay-ui:search-container
		cssClass="table-hover"
		searchContainer="<%= selectSiteNavigationMenuDisplayContext.getSiteNavigationMenuItemSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.site.navigation.item.selector.web.internal.display.context.SiteNavigationMenuEntry"
			modelVar="siteNavigationMenuEntry"
		>
			<liferay-ui:search-container-column-text
				colspan="<%= 2 %>"
				cssClass="table-title"
				name="name"
			>
				<clay:sticker
					cssClass="bg-light mr-3"
					displayType="dark"
					icon="page"
				/>

				<a href="<%= siteNavigationMenuEntry.getURL() %>">
					<%= siteNavigationMenuEntry.getName() %>
				</a>
			</liferay-ui:search-container-column-text>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
			paginate="<%= false %>"
			searchResultCssClass="table table-autofit"
		/>
	</liferay-ui:search-container>
</div>

<liferay-frontend:component
	componentId="SelectEntityHandler"
	context="<%= selectSiteNavigationMenuDisplayContext.getContext(liferayPortletResponse) %>"
	module="{SelectEntityHandler} from site-navigation-item-selector-web"
/>