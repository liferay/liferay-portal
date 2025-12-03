<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
int oAuthClientASLocalMetadatasCount = OAuthClientASLocalMetadataLocalServiceUtil.getOAuthClientASLocalMetadatasCount();

OAuthClientASLocalMetadataManagementToolbarDisplayContext oAuthClientASLocalMetadataManagementToolbarDisplayContext = new OAuthClientASLocalMetadataManagementToolbarDisplayContext(currentURLObj, liferayPortletRequest, liferayPortletResponse);
%>

<clay:management-toolbar
	actionDropdownItems="<%= oAuthClientASLocalMetadataManagementToolbarDisplayContext.getActionDropdownItems() %>"
	additionalProps="<%= oAuthClientASLocalMetadataManagementToolbarDisplayContext.getAdditionalProps() %>"
	creationMenu="<%= oAuthClientASLocalMetadataManagementToolbarDisplayContext.getCreationMenu() %>"
	disabled="<%= oAuthClientASLocalMetadatasCount == 0 %>"
	itemsTotal="<%= oAuthClientASLocalMetadatasCount %>"
	orderDropdownItems="<%= oAuthClientASLocalMetadataManagementToolbarDisplayContext.getOrderByDropdownItems() %>"
	searchContainerId="oAuthClientASLocalMetadataSearchContainer"
	selectable="<%= true %>"
	showCreationMenu="<%= true %>"
	showSearch="<%= false %>"
	sortingOrder="<%= oAuthClientASLocalMetadataManagementToolbarDisplayContext.getOrderByType() %>"
	sortingURL="<%= String.valueOf(oAuthClientASLocalMetadataManagementToolbarDisplayContext.getSortingURL()) %>"
	viewTypeItems="<%= oAuthClientASLocalMetadataManagementToolbarDisplayContext.getViewTypes() %>"
/>

<%
String navigation = ParamUtil.getString(request, "navigation", "oauth-client-as-local-metadata-oic");
%>

<clay:navigation-bar
	navigationItems='<%=
		new JSPNavigationItemList(pageContext) {
			{
				add(
					navigationItem -> {
						navigationItem.setActive(navigation.equals("oauth-client-as-local-metadata-oic") || navigation.equals("oauth-client-as-local-metadata"));

						PortletURL portletURL = PortletURLBuilder.createRenderURL(
							renderResponse
						).setMVCRenderCommandName(
							"/oauth_client_admin/view_oauth_client_as_local_metadata"
						).setNavigation(
							"oauth-client-as-local-metadata-oic"
						).buildPortletURL();

						navigationItem.setHref(portletURL.toString());

						navigationItem.setLabel(LanguageUtil.get(httpServletRequest, "oauth-client-as-local--openid-configuration"));
					});

				add(
					navigationItem -> {
						navigationItem.setActive(navigation.equals("oauth-client-as-local-metadata-oas"));

						PortletURL portletURL = PortletURLBuilder.createRenderURL(
							renderResponse
						).setMVCRenderCommandName(
							"/oauth_client_admin/view_oauth_client_as_local_metadata"
						).setNavigation(
							"oauth-client-as-local-metadata-oas"
						).buildPortletURL();

						navigationItem.setHref(portletURL.toString());

						navigationItem.setLabel(LanguageUtil.get(httpServletRequest, "oauth-client-as-local-oauth-authorization-server"));
					});
			}
		}
	%>'
/>

<c:choose>
	<c:when test='<%= navigation.equals("oauth-client-as-local-metadata") || navigation.equals("oauth-client-as-local-metadata-oic") %>'>
		<liferay-util:include page="/admin/view_oauth_client_as_local_metadata_oic.jsp" servletContext="<%= application %>" />
	</c:when>
	<c:when test='<%= navigation.equals("oauth-client-as-local-metadata-oas") %>'>
		<liferay-util:include page="/admin/view_oauth_client_as_local_metadata_oas.jsp" servletContext="<%= application %>" />
	</c:when>
</c:choose>