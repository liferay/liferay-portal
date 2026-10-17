<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String tabs2 = ParamUtil.getString(request, "tabs2", "export");

String returnToFullPageURL = ParamUtil.getString(request, "returnToFullPageURL");

PortletURL portletURL = PortletURLBuilder.createRenderURL(
	renderResponse
).setMVCRenderCommandName(
	"/export_import/export_import"
).setRedirect(
	ParamUtil.getString(request, "redirect")
).setPortletResource(
	portletResource
).setParameter(
	"returnToFullPageURL", returnToFullPageURL
).buildPortletURL();
%>

<c:choose>
	<c:when test="<%= !GroupPermissionUtil.contains(permissionChecker, themeDisplay.getScopeGroup(), ActionKeys.MANAGE_STAGING) %>">
		<div class="alert alert-info">
			<liferay-ui:message key="you-do-not-have-permission-to-access-the-requested-resource" />
		</div>
	</c:when>
	<c:otherwise>
		<c:if test='<%= !FeatureFlagManagerUtil.isEnabled(themeDisplay.getCompanyId(), "LPD-57655") %>'>
			<clay:navigation-bar
				navigationItems='<%=
					new JSPNavigationItemList(pageContext) {
						{
							portletURL.setParameter("tabs2", "export");

							add(
								navigationItem -> {
									navigationItem.setActive(tabs2.equals("export"));
									navigationItem.setHref(portletURL.toString());
									navigationItem.setLabel(LanguageUtil.get(httpServletRequest, "export"));
								});

							portletURL.setParameter("tabs2", "import");

							add(
								navigationItem -> {
									navigationItem.setActive(tabs2.equals("import"));
									navigationItem.setHref(portletURL.toString());
									navigationItem.setLabel(LanguageUtil.get(httpServletRequest, "import"));
								});
						}
					}
				%>'
			/>
		</c:if>

		<%
		Group group = themeDisplay.getScopeGroup();
		%>

		<div class="portlet-export-import-container <%= group.isCMS() ? "site-cms-export-import-dialog" : "" %>" id="<portlet:namespace />exportImportPortletContainer">
			<liferay-util:include page="/export_import_error.jsp" servletContext="<%= application %>" />

			<c:choose>
				<c:when test='<%= tabs2.equals("export") %>'>
					<liferay-util:include page="/export_portlet.jsp" servletContext="<%= application %>" />
				</c:when>
				<c:when test='<%= tabs2.equals("import") %>'>
					<liferay-util:include page="/import_portlet.jsp" servletContext="<%= application %>" />
				</c:when>
			</c:choose>
		</div>
	</c:otherwise>
</c:choose>