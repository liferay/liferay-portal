<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(ParamUtil.getString(request, "backURL", String.valueOf(renderResponse.createRenderURL())));
portletDisplay.setURLBackTitle(portletDisplay.getPortletDisplayName());

renderResponse.setTitle(LanguageUtil.get(request, "import"));

ImportDisplayContext importDisplayContext = new ImportDisplayContext(request, renderRequest, renderResponse);
%>

<react:component
	module="{ImportFragments} from fragment-web"
	props="<%= importDisplayContext.getProps() %>"
/>