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
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.frontend.data.set.admin.web.internal.constants.FDSAdminWebKeys" %><%@
page import="com.liferay.frontend.data.set.admin.web.internal.display.context.FDSAdminDisplayContext" %><%@
page import="com.liferay.frontend.data.set.admin.web.internal.display.context.FDSAdminItemSelectorDisplayContext" %><%@
page import="com.liferay.frontend.taglib.clay.servlet.taglib.util.JSPNavigationItemList" %><%@
page import="com.liferay.learn.LearnMessageUtil" %><%@
page import="com.liferay.object.exception.NoSuchObjectEntryException" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.security.auth.PrincipalException" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %>

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
FDSAdminDisplayContext fdsAdminDisplayContext = (FDSAdminDisplayContext)request.getAttribute(FDSAdminWebKeys.FDS_ADMIN_DISPLAY_CONTEXT);
FDSAdminItemSelectorDisplayContext fdsAdminItemSelectorDisplayContext = (FDSAdminItemSelectorDisplayContext)request.getAttribute(FDSAdminWebKeys.FDS_ADMIN_ITEM_SELECTOR_DISPLAY_CONTEXT);
%>