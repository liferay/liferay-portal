<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewAnalyticsRoomTrendAnalyticsSectionDisplayContext viewAnalyticsRoomTrendAnalyticsSectionDisplayContext = (ViewAnalyticsRoomTrendAnalyticsSectionDisplayContext)request.getAttribute(ViewAnalyticsRoomTrendAnalyticsSectionDisplayContext.class.getName());
%>

<div>
	<div class="custom-empty-state dsr-section">
		<react:component
			module="{RoomTrend} from digital-sales-room-web"
			props="<%= viewAnalyticsRoomTrendAnalyticsSectionDisplayContext.getProps() %>"
		/>
	</div>
</div>