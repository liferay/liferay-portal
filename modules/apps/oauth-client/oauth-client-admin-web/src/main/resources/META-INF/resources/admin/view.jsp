<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
boolean companyAdmin = permissionChecker.isCompanyAdmin();
String navigation = ParamUtil.getString(request, "navigation", "oauth-clients");
%>

<clay:navigation-bar
	navigationItems='<%=
		new JSPNavigationItemList(pageContext) {
			{
				add(
					navigationItem -> {
						navigationItem.setActive(navigation.equals("oauth-clients"));

						PortletURL portletURL = PortletURLBuilder.createRenderURL(
							renderResponse
						).setMVCRenderCommandName(
							"/oauth_client_admin/view_oauth_client_entries"
						).setNavigation(
							"oauth-clients"
						).buildPortletURL();

						navigationItem.setHref(portletURL.toString());

						navigationItem.setLabel(LanguageUtil.get(httpServletRequest, "oauth-clients"));
					});

				add(
					navigationItem -> {
						navigationItem.setActive(navigation.contains("oauth-client-as-local-metadata"));

						PortletURL portletURL = PortletURLBuilder.createRenderURL(
							renderResponse
						).setMVCRenderCommandName(
							"/oauth_client_admin/view_oauth_client_as_local_metadata"
						).setNavigation(
							"oauth-client-as-local-metadata"
						).buildPortletURL();

						navigationItem.setHref(portletURL.toString());

						navigationItem.setLabel(LanguageUtil.get(httpServletRequest, "oauth-client-as-local-metadata"));
					});

				add(
					navigationItem -> {
						navigationItem.setActive(navigation.equals("oauth-client-pr-local-metadata"));

						PortletURL portletURL = PortletURLBuilder.createRenderURL(
							renderResponse
						).setMVCRenderCommandName(
							"/oauth_client_admin/view_oauth_client_pr_local_metadata"
						).setNavigation(
							"oauth-client-pr-local-metadata"
						).buildPortletURL();

						navigationItem.setHref(portletURL.toString());

						navigationItem.setLabel(LanguageUtil.get(httpServletRequest, "oauth-client-pr-local-metadata"));
					});

				if (companyAdmin) {
					add(
						navigationItem -> {
							navigationItem.setActive(navigation.equals("oauth-client-company-configuration"));

							PortletURL portletURL = PortletURLBuilder.createRenderURL(
								renderResponse
							).setMVCRenderCommandName(
								"/oauth_client_admin/view_oauth_client_company_configuration"
							).setNavigation(
								"oauth-client-company-configuration"
							).buildPortletURL();

							navigationItem.setHref(portletURL.toString());

							navigationItem.setLabel(LanguageUtil.get(httpServletRequest, "configuration"));
						});
				}
			}
		}
	%>'
/>

<c:choose>
	<c:when test='<%= navigation.equals("oauth-clients") %>'>
		<liferay-util:include page="/admin/view_oauth_client_entries.jsp" servletContext="<%= application %>" />
	</c:when>
	<c:when test='<%= navigation.contains("oauth-client-as-local-metadata") %>'>
		<liferay-util:include page="/admin/view_oauth_client_as_local_metadata.jsp" servletContext="<%= application %>" />
	</c:when>
	<c:when test='<%= navigation.equals("oauth-client-pr-local-metadata") %>'>
		<liferay-util:include page="/admin/view_oauth_client_pr_local_metadata.jsp" servletContext="<%= application %>" />
	</c:when>
	<c:when test='<%= companyAdmin && navigation.equals("oauth-client-company-configuration") %>'>
		<liferay-util:include page="/admin/view_oauth_client_company_configuration.jsp" servletContext="<%= application %>" />
	</c:when>
</c:choose>