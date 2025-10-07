<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
FDSSampleDisplayContext fdsSampleDisplayContext = (FDSSampleDisplayContext)request.getAttribute(FDSSampleWebKeys.FDS_SAMPLE_DISPLAY_CONTEXT);
%>

<frontend-data-set:headless-display
	additionalProps='<%=
		HashMapBuilder.<String, Object>put(
			"greeting", "Hello"
		).build()
	%>'
	apiURL="<%= fdsSampleDisplayContext.getAPIURL() %>"
	customViewsEnabled="<%= true %>"
	emptyState="<%= fdsSampleDisplayContext.getEmptyState() %>"
	formId="fm"
	id="<%= FDSSampleFDSNames.ADVANCED %>"
	itemsPerPage="<%= 10 %>"
	propsTransformer="{AdvancedPropsTransformer} from frontend-data-set-sample-web"
	selectedItemsKey="id"
	selectionType="multiple"
	showSelectAll="<%= true %>"
	style="fluid"
/>