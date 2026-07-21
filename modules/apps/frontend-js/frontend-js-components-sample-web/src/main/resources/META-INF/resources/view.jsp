<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<div class="frontend-js-components-sample-web">
	<liferay-ui:tabs
		names="Chat Shell, Feature Indicator, Management Toolbar, Test Walkable, Translation Manager, Walkable"
		refresh="<%= false %>"
	>
		<liferay-ui:section>
			<liferay-util:include page="/partials/chat_panel.jsp" servletContext="<%= application %>" />
		</liferay-ui:section>

		<liferay-ui:section>
			<liferay-util:include page="/partials/feature_indicator.jsp" servletContext="<%= application %>" />
		</liferay-ui:section>

		<liferay-ui:section>
			<liferay-util:include page="/partials/management_toolbar.jsp" servletContext="<%= application %>" />
		</liferay-ui:section>

		<liferay-ui:section>
			<liferay-util:include page="/partials/test_walkable.jsp" servletContext="<%= application %>" />
		</liferay-ui:section>

		<liferay-ui:section>
			<liferay-util:include page="/partials/translation_manager.jsp" servletContext="<%= application %>" />
		</liferay-ui:section>

		<liferay-ui:section>
			<liferay-util:include page="/partials/walkable.jsp" servletContext="<%= application %>" />
		</liferay-ui:section>
	</liferay-ui:tabs>
</div>