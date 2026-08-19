<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
SearchFacet searchFacet = (SearchFacet)request.getAttribute("facet_configuration.jsp-searchFacet");

JSONObject dataJSONObject = searchFacet.getData();

JSONArray rangesJSONArray = dataJSONObject.getJSONArray("ranges");
%>

<aui:fieldset id='<%= liferayPortletResponse.getNamespace() + "rangesId" %>'>

	<%
	int[] rangesIndexes = new int[rangesJSONArray.length()];

	for (int i = 0; i < rangesJSONArray.length(); i++) {
		rangesIndexes[i] = i;

		JSONObject jsonObject = rangesJSONArray.getJSONObject(i);
	%>

		<div class="lfr-form-row lfr-form-row-inline">
			<div class="row-fields">
				<aui:input label="label" name='<%= searchFacet.getClassName() + "label_" + i %>' value='<%= jsonObject.getString("label") %>' />

				<aui:input label="range" name='<%= searchFacet.getClassName() + "range_" + i %>' value='<%= jsonObject.getString("range") %>' />
			</div>
		</div>

	<%
	}
	%>

	<aui:input name='<%= searchFacet.getClassName() + "rangesIndexes" %>' type="hidden" value="<%= StringUtil.merge(rangesIndexes) %>" />
</aui:fieldset>

<aui:script type="module">
	import {autoFields} from '<%= FrontendESMUtil.buildURL(themeDisplay, "frontend-js-web", "auto_fields") %>';

	autoFields({
		contentBox: 'fieldset#<portlet:namespace />rangesId',
		fieldIndexes:
			'<portlet:namespace /><%= searchFacet.getClassName() %>rangesIndexes',
		namespace: '<portlet:namespace />',
	});
</aui:script>