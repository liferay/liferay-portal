<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String omniSearchLabel = LanguageUtil.get(request, "omni-search") + " (Ctrl+K)";

OmniSearchDisplayContext omniSearchDisplayContext = new OmniSearchDisplayContext(request);
%>

<li class="control-menu-nav-item control-menu-nav-item-separator omni-search-control-menu-nav-item">
	<clay:button
		aria-haspopup="dialog"
		aria-label="<%= omniSearchLabel %>"
		cssClass="control-menu-nav-link lfr-portal-tooltip"
		data-qa-id="omniSearch"
		displayType="unstyled"
		icon="search"
		small="<%= true %>"
		title="<%= omniSearchLabel %>"
	/>

	<react:component
		module="{OmniSearch} from product-navigation-omni-search-web"
		props='<%=
			com.liferay.portal.kernel.util.HashMapBuilder.<String, Object>put(
				"resultsURL", omniSearchDisplayContext.getResultsURL()
			).build()
		%>'
	/>
</li>