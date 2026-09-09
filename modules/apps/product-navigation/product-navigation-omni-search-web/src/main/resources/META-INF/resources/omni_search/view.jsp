<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
OmniSearchDisplayContext omniSearchDisplayContext = new OmniSearchDisplayContext(request);
%>

<react:component
	module="{CmsToolbarOmniSearch} from product-navigation-omni-search-web"
	props='<%=
		HashMapBuilder.<String, Object>put(
			"applicationsMenuPortletId", omniSearchDisplayContext.getApplicationsMenuPortletId()
		).put(
			"resultsURL", omniSearchDisplayContext.getResultsURL()
		).build()
	%>'
/>