<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
SearchFacet searchFacet = (SearchFacet)request.getAttribute("facet_configuration.jsp-searchFacet");
%>

<aui:fieldset>
	<aui:input label="display-facet" name='<%= searchFacet.getClassName() + "displayFacet" %>' type="checkbox" value="<%= !searchFacet.isStatic() %>" />

	<div class="facet-configuration-options" id="<portlet:namespace /><%= AUIUtil.normalizeId(searchFacet.getClassName()) %>Options">
		<aui:input label="weight[priority]" name='<%= searchFacet.getClassName() + "weight" %>' value="<%= searchFacet.getWeight() %>" />

		<%
		searchFacet.includeConfiguration(request, PipingServletResponseFactory.createPipingServletResponse(pageContext));
		%>

	</div>
</aui:fieldset>

<aui:script>
	Liferay.Util.toggleBoxes(
		'<portlet:namespace /><%= AUIUtil.normalizeId(searchFacet.getClassName()) %>displayFacet',
		'<portlet:namespace /><%= AUIUtil.normalizeId(searchFacet.getClassName()) %>Options'
	);
</aui:script>