<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
EditPromptDisplayContext editPromptDisplayContext = new EditPromptDisplayContext(liferayPortletResponse, renderRequest);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(editPromptDisplayContext.getBackURL());

renderResponse.setTitle(editPromptDisplayContext.getTitle(request));
%>

<clay:container-fluid
	cssClass="container-fluid-max-xl container-view"
>
	<react:component
		module="{EditPrompt} from mcp-server-web"
		props="<%= editPromptDisplayContext.getEditPromptProps() %>"
	/>
</clay:container-fluid>