<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/frontend-data-set" prefix="frontend-data-set" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.frontend.data.set.sample.web.internal.constants.FDSSampleFDSNames" %><%@
page import="com.liferay.frontend.data.set.sample.web.internal.constants.FDSSampleWebKeys" %><%@
page import="com.liferay.frontend.data.set.sample.web.internal.display.context.ControlledFDSDisplayContext" %><%@
page import="com.liferay.frontend.data.set.sample.web.internal.display.context.EmptyFDSDisplayContext" %><%@
page import="com.liferay.frontend.data.set.sample.web.internal.display.context.FDSSampleDisplayContext" %><%@
page import="com.liferay.frontend.data.set.sample.web.internal.display.context.ReactFDSDisplayContext" %><%@
page import="com.liferay.frontend.taglib.clay.servlet.taglib.util.JSPNavigationItemList" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %>

<liferay-theme:defineObjects />

<portlet:defineObjects />