<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
SiteNavigationMenuContextualMenusItemSelectorViewDisplayContext siteNavigationMenuContextualMenusItemSelectorViewDisplayContext = (SiteNavigationMenuContextualMenusItemSelectorViewDisplayContext)request.getAttribute(SiteNavigationItemSelectorWebKeys.SITE_NAVIGATION_MENU_CONTEXTUAL_MENUS_ITEM_SELECTOR_DISPLAY_CONTEXT);
%>

<liferay-util:html-top>
	<aui:link hashedFile="<%= true %>" href="site-navigation-item-selector-web/css/ContextualMenus.css" rel="stylesheet" />
</liferay-util:html-top>

<clay:container-fluid
	cssClass="contextual-menu-selector p-4 text-3"
	fullWidth="<%= true %>"
	id='<%= liferayPortletResponse.getNamespace() + "contextualMenuSelector" %>'
>
	<clay:alert
		displayType="info"
		message="this-will-make-the-menu-show-only-related-pages.-select-here-the-type-of-relationship-of-the-pages-to-display"
	/>

	<clay:row
		cssClass="mt-5 text-center"
	>

		<%
		for (JSONObject jsonObject : (Iterable<JSONObject>)siteNavigationMenuContextualMenusItemSelectorViewDisplayContext.getLevelsJSONArray()) {
		%>

			<clay:col
				cssClass="align-items-center d-flex flex-column"
				md="4"
			>
				<clay:button
					cssClass="align-items-center contextual-menu-option contextual-menu-selector d-flex justify-content-center"
					data-contextual-menu='<%= jsonObject.getString("value") %>'
					data-title='<%= jsonObject.getString("title") %>'
					displayType="unstyled"
				>
					<img alt="<%= jsonObject.getString("title") %>" class="contextual-menu-image p-5" src="<%= jsonObject.getString("imageURL") %>" />
				</clay:button>

				<p class="font-weight-bold mb-2 mt-3">
					<%= jsonObject.getString("title") %>
				</p>

				<p class="text-secondary">
					<%= jsonObject.getString("description") %>
				</p>
			</clay:col>

		<%
		}
		%>

	</clay:row>
</clay:container-fluid>

<liferay-frontend:component
	componentId="SelectEntityHandler"
	context="<%= siteNavigationMenuContextualMenusItemSelectorViewDisplayContext.getContext(liferayPortletResponse) %>"
	module="{SelectEntityHandler} from site-navigation-item-selector-web"
/>