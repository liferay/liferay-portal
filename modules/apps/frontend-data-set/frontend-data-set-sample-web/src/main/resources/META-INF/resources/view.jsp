<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String navigation = ParamUtil.getString(request, "navigation", "advanced");
%>

<clay:container-fluid>
	<clay:navigation-bar
		navigationItems='<%=
			new JSPNavigationItemList(pageContext) {
				{
					add(
						navigationItem -> {
							navigationItem.setActive(navigation.equals("advanced"));
							navigationItem.setHref(renderResponse.createRenderURL());
							navigationItem.setLabel("Advanced");
						});
					add(
						navigationItem -> {
							navigationItem.setActive(navigation.equals("classic"));
							navigationItem.setHref(renderResponse.createRenderURL(), "navigation", "classic");
							navigationItem.setLabel("Classic");
						});
					add(
						navigationItem -> {
							navigationItem.setActive(navigation.equals("controlled"));
							navigationItem.setHref(renderResponse.createRenderURL(), "navigation", "controlled");
							navigationItem.setLabel("Controlled");
						});
					add(
						navigationItem -> {
							navigationItem.setActive(navigation.equals("custom-internal-view"));
							navigationItem.setHref(renderResponse.createRenderURL(), "navigation", "custom-internal-view");
							navigationItem.setLabel("Custom Internal View");
						});
					add(
						navigationItem -> {
							navigationItem.setActive(navigation.equals("empty"));
							navigationItem.setHref(renderResponse.createRenderURL(), "navigation", "empty");
							navigationItem.setLabel("Empty");
						});
					add(
						navigationItem -> {
							navigationItem.setActive(navigation.equals("minimum"));
							navigationItem.setHref(renderResponse.createRenderURL(), "navigation", "minimum");
							navigationItem.setLabel("Minimum");
						});
					add(
						navigationItem -> {
							navigationItem.setActive(navigation.equals("react"));
							navigationItem.setHref(renderResponse.createRenderURL(), "navigation", "react");
							navigationItem.setLabel("React");
						});
					add(
						navigationItem -> {
							navigationItem.setActive(navigation.equals("single-selection"));
							navigationItem.setHref(renderResponse.createRenderURL(), "navigation", "single-selection");
							navigationItem.setLabel("Single Selection");
						});
					add(
						navigationItem -> {
							navigationItem.setActive(navigation.equals("items-actions-groups"));
							navigationItem.setHref(renderResponse.createRenderURL(), "navigation", "items-actions-groups");
							navigationItem.setLabel("Items Actions Groups");
						});
				}
			}
		%>'
	/>

	<c:choose>
		<c:when test='<%= navigation.equals("classic") %>'>
			<liferay-util:include page="/partials/classic.jsp" servletContext="<%= application %>" />
		</c:when>
		<c:when test='<%= navigation.equals("controlled") %>'>
			<liferay-util:include page="/partials/controlled.jsp" servletContext="<%= application %>" />
		</c:when>
		<c:when test='<%= navigation.equals("custom-internal-view") %>'>
			<liferay-util:include page="/partials/custom_internal_view.jsp" servletContext="<%= application %>" />
		</c:when>
		<c:when test='<%= navigation.equals("empty") %>'>
			<liferay-util:include page="/partials/empty.jsp" servletContext="<%= application %>" />
		</c:when>
		<c:when test='<%= navigation.equals("items-actions-groups") %>'>
			<liferay-util:include page="/partials/items_actions_groups.jsp" servletContext="<%= application %>" />
		</c:when>
		<c:when test='<%= navigation.equals("minimum") %>'>
			<liferay-util:include page="/partials/minimum.jsp" servletContext="<%= application %>" />
		</c:when>
		<c:when test='<%= navigation.equals("react") %>'>
			<liferay-util:include page="/partials/react.jsp" servletContext="<%= application %>" />
		</c:when>
		<c:when test='<%= navigation.equals("single-selection") %>'>
			<liferay-util:include page="/partials/single_selection.jsp" servletContext="<%= application %>" />
		</c:when>
		<c:otherwise>
			<liferay-util:include page="/partials/advanced.jsp" servletContext="<%= application %>" />
		</c:otherwise>
	</c:choose>
</clay:container-fluid>