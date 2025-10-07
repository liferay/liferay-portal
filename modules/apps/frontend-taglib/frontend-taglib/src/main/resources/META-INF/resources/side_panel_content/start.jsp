<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/side_panel_content/init.jsp" %>

<aui:link hashedFile="<%= true %>" href="frontend-taglib/side_panel_content/main.css" rel="stylesheet" />

<div class="side-panel-iframe">
	<c:if test="<%= Validator.isNotNull(title) %>">
		<div class="side-panel-iframe-header">
			<div class="side-panel-iframe-title">
				<h3 class="mb-0">
					<%= HtmlUtil.escape(title) %>
				</h3>
			</div>

			<button class="btn btn-unstyled side-panel-iframe-close">
				<clay:icon
					symbol="times"
				/>
			</button>
		</div>
	</c:if>

	<c:if test="<%= Validator.isNull(screenNavigatorKey) %>">
		<div class="side-panel-iframe-content">
	</c:if>