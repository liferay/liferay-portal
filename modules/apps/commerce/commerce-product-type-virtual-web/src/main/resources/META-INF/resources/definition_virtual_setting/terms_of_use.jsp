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

JournalArticle journalArticle = cpDefinitionVirtualSettingDisplayContext.getJournalArticle();

long termsOfUseJournalArticleResourcePrimKey = BeanParamUtil.getLong(cpDefinitionVirtualSetting, request, "termsOfUseJournalArticleResourcePrimKey", CPDefinitionVirtualSetting::getTermsOfUseJournalArticleResourcePrimKey);

boolean useTermsOfUseJournal = false;

if (termsOfUseJournalArticleResourcePrimKey > 0) {
	useTermsOfUseJournal = true;
}
%>

<%@ include file="/terms_of_use.jspf" %>