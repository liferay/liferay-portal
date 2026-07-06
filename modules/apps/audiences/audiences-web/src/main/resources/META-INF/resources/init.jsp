<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/frontend-data-set" prefix="frontend-data-set" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.audiences.exception.AudiencesEntryJSONException" %><%@
page import="com.liferay.audiences.exception.AudiencesEntryNameException" %><%@
page import="com.liferay.audiences.exception.DuplicateAudiencesEntryExternalReferenceCodeException" %><%@
page import="com.liferay.audiences.exception.NoSuchAudiencesEntryException" %><%@
page import="com.liferay.audiences.web.internal.constants.AudiencesFDSNames" %><%@
page import="com.liferay.audiences.web.internal.display.context.AudiencesDisplayContext" %><%@
page import="com.liferay.audiences.web.internal.display.context.EditAudiencesEntryDisplayContext" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />