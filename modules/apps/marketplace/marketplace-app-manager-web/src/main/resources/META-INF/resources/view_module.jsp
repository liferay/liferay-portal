<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String app = ParamUtil.getString(request, "app");

PortletURL backURL = renderResponse.createRenderURL();

if (Validator.isNull(app)) {
	backURL.setParameter("mvcPath", "/view.jsp");
}
else {
	backURL.setParameter("mvcPath", "/view_modules.jsp");
	backURL.setParameter("app", app);
}

ViewModuleManagementToolbarDisplayContext viewModuleManagementToolbarDisplayContext = new ViewModuleManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse);

Bundle bundle = viewModuleManagementToolbarDisplayContext.getBundle();
String pluginType = viewModuleManagementToolbarDisplayContext.getPluginType();
SearchContainer<Object> searchContainer = viewModuleManagementToolbarDisplayContext.getSearchContainer();

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(backURL.toString());

Dictionary<String, String> headers = bundle.getHeaders(StringPool.BLANK);

String bundleName = GetterUtil.getString(headers.get(Constants.BUNDLE_NAME));

renderResponse.setTitle(bundleName);

if (Validator.isNull(app)) {
	PortalUtil.addPortletBreadcrumbEntry(
		request, LanguageUtil.get(request, "app-manager"),
		PortletURLBuilder.createRenderURL(
			renderResponse
		).setMVCPath(
			"/view.jsp"
		).buildString());

	PortalUtil.addPortletBreadcrumbEntry(request, bundleName, null);
}
else {
	MarketplaceAppManagerUtil.addPortletBreadcrumbEntry(viewModuleManagementToolbarDisplayContext.getAppDisplay(), bundle, request, renderResponse);
}
%>

<clay:navigation-bar
	navigationItems="<%= appManagerDisplayContext.getModuleNavigationItems() %>"
/>

<clay:management-toolbar
	searchActionURL="<%= viewModuleManagementToolbarDisplayContext.getSearchActionURL() %>"
	searchContainerId="plugins"
	searchFormName="searchFm"
	selectable="<%= false %>"
	showSearch="<%= true %>"
	sortingOrder="<%= searchContainer.getOrderByType() %>"
	sortingURL="<%= viewModuleManagementToolbarDisplayContext.getSortingURL() %>"
/>

<clay:container-fluid>
	<liferay-site-navigation:breadcrumb
		breadcrumbEntries="<%= BreadcrumbEntriesUtil.getBreadcrumbEntries(request, false, false, false, false, true) %>"
	/>

	<liferay-ui:search-container
		id="plugins"
		searchContainer="<%= searchContainer %>"
		var="pluginSearch"
	>
		<liferay-ui:search-container-row
			className="org.osgi.framework.ServiceReference"
			modelVar="serviceReference"
		>
			<liferay-ui:search-container-column-text>
				<c:choose>
					<c:when test='<%= pluginType.equals("portlets") %>'>
						<liferay-util:include page="/icon.jsp" servletContext="<%= application %>">
							<liferay-util:param name="iconURL" value='<%= themeDisplay.getPathThemeSpritemap() + "#portlets" %>' />
						</liferay-util:include>
					</c:when>
					<c:otherwise>
						<liferay-util:include page="/icon.jsp" servletContext="<%= application %>">
							<liferay-util:param name="iconURL" value='<%= themeDisplay.getPathThemeSpritemap() + "#components" %>' />
						</liferay-util:include>
					</c:otherwise>
				</c:choose>
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-text
				colspan="<%= 2 %>"
			>

				<%
				String description = StringPool.BLANK;
				String name = StringPool.BLANK;

				if (pluginType.equals("portlets")) {
					name = MarketplaceAppManagerUtil.getSearchContainerFieldText(serviceReference.getProperty("jakarta.portlet.display-name"));

					String modulePortletDescription = MarketplaceAppManagerUtil.getSearchContainerFieldText(serviceReference.getProperty("jakarta.portlet.description"));
					String modulePortletName = MarketplaceAppManagerUtil.getSearchContainerFieldText(serviceReference.getProperty("jakarta.portlet.name"));

					if (Validator.isNotNull(modulePortletDescription)) {
						description = modulePortletName + " - " + modulePortletDescription;
					}
					else {
						description = modulePortletName;
					}
				}
				else {
					name = MarketplaceAppManagerUtil.getSearchContainerFieldText(serviceReference.getProperty("component.name"));
				}
				%>

				<h2 class="list-group-title">
					<%= name %>
				</h2>

				<c:if test="Validator.isNotNull(description)">
					<p class="list-group-text"><%= description %></p>
				</c:if>
			</liferay-ui:search-container-column-text>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="descriptive"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</clay:container-fluid>