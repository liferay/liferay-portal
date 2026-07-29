<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/frontend-data-set" prefix="frontend-data-set" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.depot.model.DepotEntry" %><%@
page import="com.liferay.design.library.web.internal.constants.DesignLibraryAdminFDSNames" %><%@
page import="com.liferay.design.library.web.internal.constants.DesignLibraryWebKeys" %><%@
page import="com.liferay.design.library.web.internal.display.context.EditDesignLibraryDisplayContext" %><%@
page import="com.liferay.design.library.web.internal.display.context.ViewDesignLibraryAdminDisplayContext" %><%@
page import="com.liferay.design.library.web.internal.display.context.ViewDesignLibraryResourcesDisplayContext" %>

<liferay-theme:defineObjects />

<portlet:defineObjects />