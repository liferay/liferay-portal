<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/layout/view/init.jsp" %>

<%
String ppid = ParamUtil.getString(request, "p_p_id");
%>

<liferay-ui:success key="layoutPublished" message="the-page-was-published-successfully" />

<c:choose>
	<c:when test="<%= (themeDisplay.isStatePopUp() || themeDisplay.isWidget()) && Validator.isNotNull(ppid) %>">

		<%
		String templateContent = LayoutTemplateLocalServiceUtil.getContent("pop_up", true, theme.getThemeId());

		if (Validator.isNotNull(templateContent)) {
			String templateId = theme.getThemeId() + LayoutTemplateConstants.STANDARD_SEPARATOR + "pop_up";

			RuntimePageUtil.processTemplate(request, response, ppid, templateId, templateContent, LayoutTemplateLocalServiceUtil.getLangType("pop_up", true, theme.getThemeId()));
		}
		%>

	</c:when>
	<c:when test="<%= layoutTypePortlet.hasStateMax() && Validator.isNotNull(ppid) %>">
		<liferay-layout:render-state-max-layout-structure />
	</c:when>
	<c:when test="<%= layout.getMasterLayoutPlid() > 0 %>">
		<div id="master-layout-wrapper">
			<liferay-layout:render-fragment-layout
				showPreview="<%= true %>"
			/>
		</div>
	</c:when>
	<c:otherwise>
		<div class="layout-content portlet-layout" id="main-content" role="main">
			<liferay-layout:render-fragment-layout
				showPreview="<%= true %>"
			/>
		</div>
	</c:otherwise>
</c:choose>

<liferay-layout:layout-common
	displaySessionMessages="<%= true %>"
/>