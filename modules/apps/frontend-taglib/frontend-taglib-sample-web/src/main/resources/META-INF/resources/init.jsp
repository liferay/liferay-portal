<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.frontend.taglib.sample.web.internal.constants.SampleWebKeys" %><%@
page import="com.liferay.frontend.taglib.sample.web.internal.display.context.SampleDisplayContext" %><%@
page import="com.liferay.frontend.taglib.sample.web.internal.display.context.SearchIteratorDisplayContext" %><%@
page import="com.liferay.frontend.taglib.sample.web.internal.display.context.SearchPaginatorDisplayContext" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %>

<liferay-theme:defineObjects />