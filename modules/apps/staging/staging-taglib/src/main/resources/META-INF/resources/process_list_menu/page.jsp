<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/process_list_menu/init.jsp" %>

<liferay-ui:icon-menu
	direction="left-side"
	icon="<%= StringPool.BLANK %>"
	markupView="lexicon"
	message="<%= StringPool.BLANK %>"
	showWhenSingleIcon="<%= true %>"
>
	<c:if test="<%= relaunchMenu %>">
		<%@ include file="/process_list_menu/items/relaunch.jspf" %>
	</c:if>

	<c:if test="<%= deleteMenu %>">
		<%@ include file="/process_list_menu/items/delete.jspf" %>
	</c:if>

	<c:if test="<%= summaryMenu %>">
		<%@ include file="/process_list_menu/items/summary.jspf" %>
	</c:if>

	<c:if test="<%= detailsMenu %>">
		<%@ include file="/process_list_menu/items/details.jspf" %>
	</c:if>
</liferay-ui:icon-menu>