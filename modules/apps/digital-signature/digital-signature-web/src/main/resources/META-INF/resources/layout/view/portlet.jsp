<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/layout/view/init.jsp" %>

<%
String velocityTemplateContent = LayoutTemplateLocalServiceUtil.getContent("pop_up", true, theme.getThemeId());

if (Validator.isNotNull(velocityTemplateContent)) {
	RuntimePageUtil.processTemplate(request, response, ParamUtil.getString(request, "p_p_id"), theme.getThemeId() + LayoutTemplateConstants.STANDARD_SEPARATOR + "pop_up", velocityTemplateContent);
}
%>

<liferay-layout:layout-common />