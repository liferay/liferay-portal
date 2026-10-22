<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
SignDSRequestDisplayContext signDSRequestDisplayContext = (SignDSRequestDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(signDSRequestDisplayContext.getBackURL());
%>

<liferay-util:html-top
	outputKey="com.liferay.digital.signature.web#/sign_digital_signature/sign_ds_request.jsp"
>
	<aui:link hashedFile="<%= true %>" href="digital-signature-web/css/main.css" rel="stylesheet" type="text/css" />
</liferay-util:html-top>

<div class="<%= signDSRequestDisplayContext.getCssClass() %>" id="<portlet:namespace />signContainer">
	<liferay-ui:message key="loading-your-document-for-signing" />
</div>

<liferay-frontend:component
	context="<%= signDSRequestDisplayContext.getProps() %>"
	module="{mountSigning} from digital-signature-web"
/>