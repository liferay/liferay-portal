<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String tabs1 = ParamUtil.getString(request, "tabs1", "customized");

String tabs1Names = "classic,controlled,customized,minimum,react,empty,custom_internal_view";

PortletURL portletURL = PortletURLBuilder.createRenderURL(
	renderResponse
).setTabs1(
	tabs1
).buildPortletURL();
%>

<clay:container-fluid>
	<liferay-ui:tabs
		names="<%= tabs1Names %>"
		url="<%= portletURL.toString() %>"
		value="<%= tabs1 %>"
	>

		<%
		String[] sections = tabs1Names.split(StringPool.COMMA);

		for (int i = 0; i < sections.length; i++) {
		%>

			<liferay-ui:section>
				<c:if test="<%= tabs1.equals(sections[i]) %>">
					<liferay-util:include page='<%= "/partials/" + tabs1 + ".jsp" %>' servletContext="<%= pageContext.getServletContext() %>" />
				</c:if>
			</liferay-ui:section>

		<%
		}
		%>

	</liferay-ui:tabs>
</clay:container-fluid>