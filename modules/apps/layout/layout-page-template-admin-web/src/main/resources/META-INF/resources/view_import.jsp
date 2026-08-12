<%--
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(ParamUtil.getString(request, "backURL", String.valueOf(renderResponse.createRenderURL())));
portletDisplay.setURLBackTitle(portletDisplay.getPortletDisplayName());

renderResponse.setTitle(LanguageUtil.get(request, "import"));

LiferayPortletURL importURL = (LiferayPortletURL)ResourceURLBuilder.createResourceURL(
	renderResponse
).setParameter(
	"layoutPageTemplateCollectionId", ParamUtil.getString(request, "layoutPageTemplateCollectionId")
).setResourceID(
	"/layout_page_template_admin/import"
).buildResourceURL();

importURL.setCopyCurrentRenderParameters(false);
%>

<react:component
	module="{ImportPageTemplates} from layout-page-template-admin-web"
	props='<%=
		HashMapBuilder.<String, Object>put(
			"backURL", ParamUtil.getString(request, "backURL")
		).put(
			"importURL", importURL.toString()
		).build()
	%>'
/>