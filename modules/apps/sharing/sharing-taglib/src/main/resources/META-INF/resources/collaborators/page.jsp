<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/collaborators/init.jsp" %>

<liferay-util:html-top
	outputKey="com.liferay.sharing.taglib#/collaborators/page.jsp"
>
	<aui:link hashedFile="<%= true %>" href="sharing-taglib/collaborators/css/main.css" rel="stylesheet" type="text/css" />
</liferay-util:html-top>

<div class="collaborators" id="<portlet:namespace />collaborators-root">
	<react:component
		module="{Collaborators} from sharing-taglib"
		props='<%= (Map<String, Object>)request.getAttribute("liferay-sharing:collaborators:data") %>'
	/>
</div>