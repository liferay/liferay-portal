<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="http://liferay.com/tld/react" prefix="react" %>

<%@ page import="com.liferay.seo.studio.web.internal.display.context.EditPageSpeedConfigurationDisplayContext" %>

<%
EditPageSpeedConfigurationDisplayContext editPageSpeedConfigurationDisplayContext = (EditPageSpeedConfigurationDisplayContext)request.getAttribute(EditPageSpeedConfigurationDisplayContext.class.getName());
%>

<div class="seo-studio-pagespeed-configuration">
	<react:component
		module="{PageSpeedConfiguration} from seo-studio-web"
		props="<%= editPageSpeedConfigurationDisplayContext.getViewProps() %>"
	/>
</div>