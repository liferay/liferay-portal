<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
DepotEntry depotEntry = (DepotEntry)request.getAttribute(DesignLibraryWebKeys.DESIGN_LIBRARY_ENTRY);

EditDesignLibraryDisplayContext editDesignLibraryDisplayContext = new EditDesignLibraryDisplayContext(depotEntry, request, liferayPortletResponse);

editDesignLibraryDisplayContext.setPortletDisplay(portletDisplay, renderResponse);
%>

<react:component
	module="{DesignLibrarySettings} from design-library-web"
	props="<%= editDesignLibraryDisplayContext.getProps() %>"
/>