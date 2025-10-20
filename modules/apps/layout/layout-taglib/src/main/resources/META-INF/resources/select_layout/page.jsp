<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/select_layout/init.jsp" %>

<liferay-util:html-top>
	<aui:link hashedFile="<%= true %>" href="layout-taglib/select_layout/css/tree.css" rel="stylesheet" type="text/css" />
</liferay-util:html-top>

<div>
	<react:component
		module="{SelectLayout} from layout-taglib"
		props='<%= (Map<String, Object>)request.getAttribute("liferay-layout:select-layout:data") %>'
	/>
</div>