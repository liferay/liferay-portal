<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<liferay-portlet:resourceURL copyCurrentRenderParameters="<%= false %>" var="baseResourceURL" />

<div>
	<div class="mb-3">
		<clay:link
			aria-label='<%= LanguageUtil.format(request, "x-opens-new-window", LanguageUtil.get(request, "click-here-to-learn-more-about-what-settings-liferay-recommends-to-check-before-a-dxp-deployment"), false) %>'
			href="https://www.liferay.com/resources/product-info/Liferay+DXP+Go+Live+Checklist"
			iconAfter="shortcut"
			label="click-here-to-learn-more-about-what-settings-liferay-recommends-to-check-before-a-dxp-deployment"
			target="_blank"
		/>
	</div>

	<react:component
		module="{ProductionReadinessDashboard} from server-admin-web"
		props='<%=
			HashMapBuilder.<String, Object>put(
				"baseResourceURL", String.valueOf(baseResourceURL)
			).build()
		%>'
	/>
</div>