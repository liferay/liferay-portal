<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewAnalyticsEngagementChartAnalyticsSectionDisplayContext viewAnalyticsEngagementChartAnalyticsSectionDisplayContext = (ViewAnalyticsEngagementChartAnalyticsSectionDisplayContext)request.getAttribute(ViewAnalyticsEngagementChartAnalyticsSectionDisplayContext.class.getName());

String type = viewAnalyticsEngagementChartAnalyticsSectionDisplayContext.getType();
%>

<div>
	<div class="custom-empty-state dsr-section">
		<c:choose>
			<c:when test='<%= type.equals("recent") %>'>
				<react:component
					module="{RecentEngagementChart} from digital-sales-room-web"
					props="<%= viewAnalyticsEngagementChartAnalyticsSectionDisplayContext.getProps() %>"
				/>
			</c:when>
			<c:when test='<%= type.equals("timeline") %>'>
				<react:component
					module="{TimelineEngagementChart} from digital-sales-room-web"
					props="<%= viewAnalyticsEngagementChartAnalyticsSectionDisplayContext.getProps() %>"
				/>
			</c:when>
			<c:otherwise />
		</c:choose>
	</div>
</div>