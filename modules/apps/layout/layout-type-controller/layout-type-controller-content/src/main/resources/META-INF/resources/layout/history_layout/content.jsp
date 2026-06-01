<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/layout/history_layout/init.jsp" %>

<c:choose>
	<c:when test="<%= layout.getMasterLayoutPlid() > 0 %>">
		<div id="master-layout-wrapper">
			<liferay-layout:render-fragment-layout
				showPreview="<%= true %>"
			/>
		</div>
	</c:when>
	<c:otherwise>
		<div class="layout-content portlet-layout" id="main-content" role="main">
			<liferay-layout:render-fragment-layout
				showPreview="<%= true %>"
			/>
		</div>
	</c:otherwise>
</c:choose>

<div class="layout-content-version">
	<liferay-portlet:runtime
		portletName="com_liferay_layout_content_web_internal_portlet_LayoutContentVersionPortlet"
	/>
</div>

<liferay-layout:layout-common />