<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/import/init.jsp" %>

<%
String backURL = String.valueOf(renderResponse.createRenderURL());

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(ParamUtil.getString(request, "backURL", backURL));
portletDisplay.setURLBackTitle(portletDisplay.getPortletDisplayName());
renderResponse.setTitle(LanguageUtil.get(request, "error-details"));
%>

<div>
	<span aria-hidden="true" class="loading-animation loading-animation-sm mt-4"></span>

	<react:component
		module="{ViewImportErrorDetail} from exportimport-web"
		props='<%=
			HashMapBuilder.<String, Object>put(
				"apiURL", importErrorsDisplayContext.getReportEntryAPIURL(ParamUtil.getString(request, "errorId"))
			).put(
				"backURL", portletDisplay.getURLBack()
			).build()
		%>'
	/>
</div>