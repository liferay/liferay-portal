<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CPDefinitionVirtualSettingDisplayContext cpDefinitionVirtualSettingDisplayContext = (CPDefinitionVirtualSettingDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CPDefinitionVirtualSetting cpDefinitionVirtualSetting = cpDefinitionVirtualSettingDisplayContext.getCPDefinitionVirtualSetting();

FileEntry sampleFileEntry = cpDefinitionVirtualSettingDisplayContext.getSampleFileEntry();

long sampleFileEntryId = BeanParamUtil.getLong(cpDefinitionVirtualSetting, request, "sampleFileEntryId", CPDefinitionVirtualSetting::getSampleFileEntryId);

String textCssClass = "text-default ";

boolean useSampleFileEntry = false;

if (sampleFileEntryId > 0) {
	textCssClass += "hide";

	useSampleFileEntry = true;
}
%>

<%@ include file="/sample.jspf" %>