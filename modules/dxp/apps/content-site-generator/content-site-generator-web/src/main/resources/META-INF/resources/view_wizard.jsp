<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewGenerationsDisplayContext viewGenerationsDisplayContext = (ViewGenerationsDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);
%>

<react:component
	module="{ContentSiteGeneratorWizard} from content-site-generator-web"
	props="<%= viewGenerationsDisplayContext.getWizardProps() %>"
/>