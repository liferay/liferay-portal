<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
EditAudiencesEntryDisplayContext editAudiencesEntryDisplayContext = (EditAudiencesEntryDisplayContext)request.getAttribute(EditAudiencesEntryDisplayContext.class.getName());

renderResponse.setTitle(editAudiencesEntryDisplayContext.getTitle());
%>

<liferay-util:html-top>
	<aui:style type="text/css">
		.control-menu,
		.side-navigation-container {
			display: none;
		}
	</aui:style>
</liferay-util:html-top>

<div id="<%= liferayPortletResponse.getNamespace() %>-audience-builder-root">
	<div class="inline-item my-5 p-5 w-100">
		<span aria-hidden="true" class="loading-animation"></span>
	</div>

	<react:component
		module="{AudienceBuilder} from audiences-web"
		props="<%= editAudiencesEntryDisplayContext.getData() %>"
	/>
</div>