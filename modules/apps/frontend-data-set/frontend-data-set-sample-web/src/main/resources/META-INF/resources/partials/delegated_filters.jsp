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
	This data set declares filters but shows no filter UI, because
	<code>DelegatedFiltersPropsTransformer</code> sets
	<code>showFilters</code> to <code>false</code>. The filters it declares
	stay in its state, and filtering is left to whoever drives the data set
	from outside.
</p>

<frontend-data-set:headless-display
	apiURL="<%= fdsSampleDisplayContext.getAPIURL() %>"
	emptyState="<%= fdsSampleDisplayContext.getEmptyState() %>"
	id="<%= FDSSampleFDSNames.DELEGATED_FILTERS %>"
	itemsPerPage="<%= 10 %>"
	propsTransformer="{DelegatedFiltersPropsTransformer} from frontend-data-set-sample-web"
	style="fluid"
/>