<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://liferay.com/tld/asset" prefix="liferay-asset" %><%@
taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.asset.kernel.model.AssetRenderer" %><%@
page import="com.liferay.journal.model.JournalArticle" %><%@
page import="com.liferay.journal.terms.of.use.internal.constants.JournalArticleTermsOfUseWebConstants" %><%@
page import="com.liferay.journal.terms.of.use.internal.display.context.JournalArticleTermsOfUseDisplayContext" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<%
JournalArticleTermsOfUseDisplayContext journalArticleTermsOfUseDisplayContext = (JournalArticleTermsOfUseDisplayContext)request.getAttribute(JournalArticleTermsOfUseWebConstants.JOURNAL_ARTICLE_TERMS_OF_USE_DISPLAY_CONTEXT);
%>