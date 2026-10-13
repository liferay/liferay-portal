<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewLaunchDisplayContext viewLaunchDisplayContext = (ViewLaunchDisplayContext)request.getAttribute(LaunchWebKeys.VIEW_LAUNCH_DISPLAY_CONTEXT);
%>

<clay:container-fluid
	cssClass="mt-4"
>
	<react:component
		module="{LaunchDetailsForm} from launch-web"
		props='<%=
			HashMapBuilder.<String, Object>put(
				"launchSetId", viewLaunchDisplayContext.getLaunchSetId()
			).put(
				"portletNamespace", liferayPortletResponse.getNamespace()
			).build()
		%>'
	/>
</clay:container-fluid>