<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String innerNavigation = ParamUtil.getString(request, "innerNavigation", "advanced_classic");
%>

<clay:navigation-bar
	navigationItems='<%=
		new JSPNavigationItemList(pageContext) {
			{
				add(
					navigationItem -> {
						navigationItem.setActive(innerNavigation.equals("advanced_classic"));
						navigationItem.setHref(renderResponse.createRenderURL(), "navigation", "ckeditor5");
						navigationItem.setLabel("Advanced Classic");
					});
				add(
					navigationItem -> {
						navigationItem.setActive(innerNavigation.equals("basic_classic"));
						navigationItem.setHref(renderResponse.createRenderURL(), "navigation", "ckeditor5", "innerNavigation", "basic_classic");
						navigationItem.setLabel("Basic Classic");
					});
				add(
					navigationItem -> {
						navigationItem.setActive(innerNavigation.equals("react"));
						navigationItem.setHref(renderResponse.createRenderURL(), "navigation", "ckeditor5", "innerNavigation", "react");
						navigationItem.setLabel("React");
					});
				add(
					navigationItem -> {
						navigationItem.setActive(innerNavigation.equals("react_cet"));
						navigationItem.setHref(renderResponse.createRenderURL(), "navigation", "ckeditor5", "innerNavigation", "react_cet");
						navigationItem.setLabel("React + CET");
					});
				add(
					navigationItem -> {
						navigationItem.setActive(innerNavigation.equals("balloon"));
						navigationItem.setHref(renderResponse.createRenderURL(), "navigation", "ckeditor5", "innerNavigation", "balloon");
						navigationItem.setLabel("Balloon");
					});
				add(
					navigationItem -> {
						navigationItem.setActive(innerNavigation.equals("input_localized"));
						navigationItem.setHref(renderResponse.createRenderURL(), "navigation", "ckeditor5", "innerNavigation", "input_localized");
						navigationItem.setLabel("Input Localized");
					});
			}
		}
	%>'
/>

<div class="mt-3">
	<c:choose>
		<c:when test='<%= StringUtil.equals(innerNavigation, "advanced_classic") %>'>
			<liferay-util:include page="/ckeditor5/partials/advanced_classic.jsp" servletContext="<%= application %>" />
		</c:when>
		<c:when test='<%= StringUtil.equals(innerNavigation, "balloon") %>'>
			<liferay-util:include page="/ckeditor5/partials/balloon.jsp" servletContext="<%= application %>" />
		</c:when>
		<c:when test='<%= StringUtil.equals(innerNavigation, "basic_classic") %>'>
			<liferay-util:include page="/ckeditor5/partials/basic_classic.jsp" servletContext="<%= application %>" />
		</c:when>
		<c:when test='<%= StringUtil.equals(innerNavigation, "input_localized") %>'>
			<liferay-util:include page="/ckeditor5/partials/input_localized.jsp" servletContext="<%= application %>" />
		</c:when>
		<c:when test='<%= StringUtil.equals(innerNavigation, "react") %>'>
			<liferay-util:include page="/ckeditor5/partials/react.jsp" servletContext="<%= application %>" />
		</c:when>
		<c:otherwise>
			<liferay-util:include page="/ckeditor5/partials/react_cet.jsp" servletContext="<%= application %>" />
		</c:otherwise>
	</c:choose>
</div>