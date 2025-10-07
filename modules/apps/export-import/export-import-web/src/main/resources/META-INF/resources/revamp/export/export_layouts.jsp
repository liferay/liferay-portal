<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/revamp/init.jsp" %>

<liferay-staging:defineObjects />

<%
String backURL = ParamUtil.getString(request, "backURL");

if (liveGroup == null) {
	liveGroup = group;
	liveGroupId = groupId;
}

String displayStyle = ParamUtil.getString(request, "displayStyle");

PortletURL portletURL = PortletURLBuilder.createRenderURL(
	renderResponse
).setMVCRenderCommandName(
	"/export_import/view_export_layouts"
).setParameter(
	"displayStyle", displayStyle
).setParameter(
	"groupId", groupId
).setParameter(
	"liveGroupId", liveGroupId
).setParameter(
	"privateLayout", privateLayout
).buildPortletURL();

if (Validator.isBlank(backURL)) {
	backURL = portletURL.toString();
}

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(backURL);
%>

<clay:container-fluid
	cssClass="container-form-lg"
>
	<div class="sheet">
		<span aria-hidden="true" class="loading-animation mb-9 mt-8"></span>
	</div>

	<react:component
		module="{EditExport} from exportimport-web"
	/>
</clay:container-fluid>