<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<liferay-ui:icon-menu
	direction="right-side"
	extended="<%= false %>"
	icon="plus"
	id="iconMenu"
	markupView="lexicon"
	message="Sample Add"
	scroll="<%= false %>"
	showArrow="<%= false %>"
	showWhenSingleIcon="<%= true %>"
>
	<liferay-ui:icon
		message="First Menu Option"
	/>
</liferay-ui:icon-menu>