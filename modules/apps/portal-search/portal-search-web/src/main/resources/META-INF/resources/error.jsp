<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.portal.kernel.security.auth.PrincipalException" %><%@
page import="com.liferay.portal.kernel.servlet.SessionErrors" %>

<div class="container-fluid container-fluid-max-xl container-form-lg">
	<c:choose>
		<c:when test="<%= SessionErrors.contains(request, PrincipalException.getNestedClasses()) %>">
			<liferay-ui:error exception="<%= PrincipalException.class %>" message="you-do-not-have-permission-to-view-this-page" />
		</c:when>
		<c:otherwise>
			<liferay-ui:message key="an-unexpected-error-occurred" />
		</c:otherwise>
	</c:choose>
</div>