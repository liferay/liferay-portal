<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
EditDesignLibraryDisplayContext editDesignLibraryDisplayContext = new EditDesignLibraryDisplayContext(request, liferayPortletResponse);

editDesignLibraryDisplayContext.setPortletDisplay(portletDisplay, renderResponse);
%>

<react:component
	module="{DesignLibrarySettings} from design-library-web"
	props="<%= editDesignLibraryDisplayContext.getProps() %>"
/>