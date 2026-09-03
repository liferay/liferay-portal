<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String navigation = ParamUtil.getString(request, "navigation", "advanced_classic");
%>

<clay:container-fluid>
	<clay:navigation-bar
		navigationItems='<%=
			new JSPNavigationItemList(pageContext) {
				{
					add(
						navigationItem -> {
							navigationItem.setActive(navigation.equals("advanced_classic"));
							navigationItem.setHref(renderResponse.createRenderURL());
							navigationItem.setLabel("Advanced Classic");
						});
					add(
						navigationItem -> {
							navigationItem.setActive(navigation.equals("basic_classic"));
							navigationItem.setHref(renderResponse.createRenderURL(), "navigation", "basic_classic");
							navigationItem.setLabel("Basic Classic");
						});
					add(
						navigationItem -> {
							navigationItem.setActive(navigation.equals("react"));
							navigationItem.setHref(renderResponse.createRenderURL(), "navigation", "react");
							navigationItem.setLabel("React");
						});
					add(
						navigationItem -> {
							navigationItem.setActive(navigation.equals("react_cet"));
							navigationItem.setHref(renderResponse.createRenderURL(), "navigation", "react_cet");
							navigationItem.setLabel("React + CET");
						});
					add(
						navigationItem -> {
							navigationItem.setActive(navigation.equals("react_cet_premium"));
							navigationItem.setHref(renderResponse.createRenderURL(), "navigation", "react_cet_premium");
							navigationItem.setLabel("React + CET Premium");
						});
					add(
						navigationItem -> {
							navigationItem.setActive(navigation.equals("balloon"));
							navigationItem.setHref(renderResponse.createRenderURL(), "navigation", "balloon");
							navigationItem.setLabel("Balloon");
						});
					add(
						navigationItem -> {
							navigationItem.setActive(navigation.equals("input_localized"));
							navigationItem.setHref(renderResponse.createRenderURL(), "navigation", "input_localized");
							navigationItem.setLabel("Input Localized");
						});
				}
			}
		%>'
	/>

	<div class="mt-3">
		<c:choose>
			<c:when test='<%= StringUtil.equals(navigation, "balloon") %>'>
				<liferay-util:include page="/partials/balloon.jsp" servletContext="<%= application %>" />
			</c:when>
			<c:when test='<%= StringUtil.equals(navigation, "basic_classic") %>'>
				<liferay-util:include page="/partials/basic_classic.jsp" servletContext="<%= application %>" />
			</c:when>
			<c:when test='<%= StringUtil.equals(navigation, "input_localized") %>'>
				<liferay-util:include page="/partials/input_localized.jsp" servletContext="<%= application %>" />
			</c:when>
			<c:when test='<%= StringUtil.equals(navigation, "react") %>'>
				<liferay-util:include page="/partials/react.jsp" servletContext="<%= application %>" />
			</c:when>
			<c:when test='<%= StringUtil.equals(navigation, "react_cet") %>'>
				<liferay-util:include page="/partials/react_cet.jsp" servletContext="<%= application %>" />
			</c:when>
			<c:when test='<%= StringUtil.equals(navigation, "react_cet_premium") %>'>
				<liferay-util:include page="/partials/react_cet_premium.jsp" servletContext="<%= application %>" />
			</c:when>
			<c:otherwise>
				<liferay-util:include page="/partials/advanced_classic.jsp" servletContext="<%= application %>" />
			</c:otherwise>
		</c:choose>
	</div>
</clay:container-fluid>