<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/wiki/init.jsp" %>

<%
WikiNode node = (WikiNode)request.getAttribute(WikiWebKeys.WIKI_NODE);
%>

<liferay-util:include page="/wiki/top_links.jsp" servletContext="<%= application %>" />

<div class="main-content-body mt-4">
	<liferay-ui:header
		title="recent-changes"
	/>

	<liferay-util:include page="/wiki/page_iterator.jsp" servletContext="<%= application %>">
		<liferay-util:param name="navigation" value="recent-changes" />
	</liferay-util:include>

	<br />

	<c:if test="<%= WikiUtil.isRSSFeedsEnabled(siteWikiGroupServiceConfiguration) %>">
		<liferay-rss:rss
			delta="<%= GetterUtil.getInteger(siteWikiGroupServiceConfiguration.rssDelta()) %>"
			displayStyle="<%= siteWikiGroupServiceConfiguration.rssDisplayStyle() %>"
			feedType="<%= siteWikiGroupServiceConfiguration.rssFeedType() %>"
			url='<%= themeDisplay.getPathMain() + "/wiki/rss?nodeId=" + node.getNodeId() %>'
		/>
	</c:if>
</div>