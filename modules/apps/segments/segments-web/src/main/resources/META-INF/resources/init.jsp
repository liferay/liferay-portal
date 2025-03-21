<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.portal.kernel.dao.search.SearchContainer" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.User" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.segments.exception.NoSuchEntryException" %><%@
page import="com.liferay.segments.exception.RequiredSegmentsEntryException" %><%@
page import="com.liferay.segments.exception.SegmentsEntryCriteriaException" %><%@
page import="com.liferay.segments.exception.SegmentsEntryKeyException" %><%@
page import="com.liferay.segments.exception.SegmentsEntryNameException" %><%@
page import="com.liferay.segments.source.provider.SegmentsSourceDetailsProvider" %><%@
page import="com.liferay.segments.web.internal.display.context.EditSegmentsEntryDisplayContext" %><%@
page import="com.liferay.segments.web.internal.display.context.PreviewSegmentsEntryUsersDisplayContext" %><%@
page import="com.liferay.segments.web.internal.display.context.SegmentsCompanyConfigurationDisplayContext" %><%@
page import="com.liferay.segments.web.internal.display.context.SegmentsDisplayContext" %><%@
page import="com.liferay.segments.web.internal.display.context.SegmentsManagementToolbarDisplayContext" %><%@
page import="com.liferay.segments.web.internal.util.SegmentsCompanyConfigurationActionDropdownItemsProvider" %><%@
page import="com.liferay.segments.web.internal.util.SegmentsEntryActionDropdownItemsProvider" %><%@
page import="com.liferay.segments.web.internal.util.SegmentsSourceDetailsProviderUtil" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />