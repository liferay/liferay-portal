<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
FDSSampleDisplayContext fdsSampleDisplayContext = (FDSSampleDisplayContext)request.getAttribute(FDSSampleWebKeys.FDS_SAMPLE_DISPLAY_CONTEXT);
%>

<p>
	This data set declares filters and shows them until a client extension
	connects to it and declares that it owns the filtering. From that moment
	the filters dropdown and the filter chips are gone, because the filters
	this data set declares no longer reach the request: the client extension
	owns the whole filter expression, and offers its own filter UI. They stay
	in the state, so the client extension can read them and decide which ones
	to obey, and they come back when it disconnects.
</p>

<p>
	Add the <code>liferay-sample-custom-element-8</code> client extension to
	this page to see it.
</p>

<frontend-data-set:headless-display
	apiURL="<%= fdsSampleDisplayContext.getAPIURL() %>"
	emptyState="<%= fdsSampleDisplayContext.getEmptyState() %>"
	id="<%= FDSSampleFDSNames.DELEGATED_FILTERS %>"
	itemsPerPage="<%= 10 %>"
	style="fluid"
/>