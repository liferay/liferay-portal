<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/frontend-data-set" prefix="frontend-data-set" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.site.cms.site.initializer.internal.constants.CMSSiteInitializerFDSNames" %><%@
page import="com.liferay.site.cms.site.initializer.internal.display.context.EditCategoryDisplayContext" %><%@
page import="com.liferay.site.cms.site.initializer.internal.display.context.EditVocabularyDisplayContext" %><%@
page import="com.liferay.site.cms.site.initializer.internal.display.context.ViewAllSectionDisplayContext" %><%@
page import="com.liferay.site.cms.site.initializer.internal.display.context.ViewAllSpacesDisplayContext" %><%@
page import="com.liferay.site.cms.site.initializer.internal.display.context.ViewCategoriesDisplayContext" %><%@
page import="com.liferay.site.cms.site.initializer.internal.display.context.ViewCategoryUsagesDisplayContext" %><%@
page import="com.liferay.site.cms.site.initializer.internal.display.context.ViewContentsSectionDisplayContext" %><%@
page import="com.liferay.site.cms.site.initializer.internal.display.context.ViewDashboardDisplayContext" %><%@
page import="com.liferay.site.cms.site.initializer.internal.display.context.ViewFilesSectionDisplayContext" %><%@
page import="com.liferay.site.cms.site.initializer.internal.display.context.ViewFolderSectionDisplayContext" %><%@
page import="com.liferay.site.cms.site.initializer.internal.display.context.ViewRecycleBinSectionDisplayContext" %><%@
page import="com.liferay.site.cms.site.initializer.internal.display.context.ViewSharedWithMeSectionDisplayContext" %><%@
page import="com.liferay.site.cms.site.initializer.internal.display.context.ViewSpaceContentsSummarySectionDisplayContext" %><%@
page import="com.liferay.site.cms.site.initializer.internal.display.context.ViewSpaceFilesSummarySectionDisplayContext" %><%@
page import="com.liferay.site.cms.site.initializer.internal.display.context.ViewSpaceMembersSummarySectionDisplayContext" %><%@
page import="com.liferay.site.cms.site.initializer.internal.display.context.ViewSpaceSitesSummarySectionDisplayContext" %><%@
page import="com.liferay.site.cms.site.initializer.internal.display.context.ViewStructureUsagesDisplayContext" %><%@
page import="com.liferay.site.cms.site.initializer.internal.display.context.ViewStructuresDisplayContext" %><%@
page import="com.liferay.site.cms.site.initializer.internal.display.context.ViewTagUsagesDisplayContext" %><%@
page import="com.liferay.site.cms.site.initializer.internal.display.context.ViewTagsDisplayContext" %><%@
page import="com.liferay.site.cms.site.initializer.internal.display.context.ViewVersionHistoryDisplayContext" %><%@
page import="com.liferay.site.cms.site.initializer.internal.display.context.ViewVocabulariesDisplayContext" %>

<liferay-theme:defineObjects />

<liferay-util:html-top>
	<aui:link href='<%= PortalUtil.getStaticResourceURL(request, PortalUtil.getPathProxy() + application.getContextPath() + "/css/main.css") %>' rel="stylesheet" type="text/css" />
</liferay-util:html-top>