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

<c:choose>
	<c:when test="<%= selectSiteNavigationMenuDisplayContext.getSiteNavigationMenuId() >= 0 %>">
		<liferay-util:include page="/select_site_navigation_menu_level.jsp" servletContext="<%= application %>" />
	</c:when>
	<c:otherwise>
		<div class="container-fluid p-4">
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
					disabled="<%= true %>"
					displayType="primary"
					label="select-this-level"
					small="<%= true %>"
				/>
			</div>

			<liferay-ui:search-container
				cssClass="table-hover"
				searchContainer="<%= selectSiteNavigationMenuDisplayContext.getSiteNavigationMenuSearchContainer() %>"
			>
				<liferay-ui:search-container-row
					className="com.liferay.site.navigation.model.SiteNavigationMenu"
					keyPropertyName="siteNavigationMenuId"
					modelVar="siteNavigationMenu"
				>

					<%
					String name = siteNavigationMenu.getName();

					if (siteNavigationMenu.getGroupId() != scopeGroupId) {
						Group group = GroupLocalServiceUtil.getGroup(siteNavigationMenu.getGroupId());

						name = StringUtil.appendParentheticalSuffix(name, group.getDescriptiveName(locale));
					}
					%>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand"
						name="name"
					>
						<clay:sticker
							cssClass="bg-light mr-3"
							displayType="dark"
							icon="sites"
						/>

						<a href="<%= selectSiteNavigationMenuDisplayContext.getSelectSiteNavigationMenuLevelURL(siteNavigationMenu.getSiteNavigationMenuId(), siteNavigationMenu.getType()) %>">
							<b><%= HtmlUtil.escape(name) %></b>
						</a>
					</liferay-ui:search-container-column-text>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-minw-300"
						name="marked-as"
					>
						<liferay-ui:message key="<%= siteNavigationMenu.getTypeKey() %>" />
					</liferay-ui:search-container-column-text>
				</liferay-ui:search-container-row>

				<liferay-ui:search-iterator
					markupView="lexicon"
					searchResultCssClass="table table-autofit"
				/>
			</liferay-ui:search-container>
		</div>
	</c:otherwise>
</c:choose>