<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
FDSSampleDisplayContext fdsSampleDisplayContext = (FDSSampleDisplayContext)request.getAttribute(FDSSampleWebKeys.FDS_SAMPLE_DISPLAY_CONTEXT);
%>

<p class="mt-3">
	This tests that using groups <code>{type: "group"}</code> in the items
	actions dropdown still works the same as without groups. This uses the same
	properties as the "Advanced" sample except it passes in
	<code>{additionalProps: {enableItemsActionsGroups: true}}</code> for the
	propsTransformer to modify the <code>itemsActions</code> into groups.
</p>

<frontend-data-set:headless-display
	additionalProps='<%=
		HashMapBuilder.<String, Object>put(
			"enableItemsActionsGroups", true
		).put(
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