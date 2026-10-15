<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String driverClassName = ParamUtil.getString(request, "driverClassName");
String driverUrl = ParamUtil.getString(request, "driverUrl");
String driverUserName = ParamUtil.getString(request, "driverUserName");

String driverPassword = ParamUtil.getString(request, "driverPassword");

if (Validator.isNull(driverPassword)) {
	long sourceId = ParamUtil.getLong(request, "sourceId");

	Source source = SourceLocalServiceUtil.fetchSource(sourceId);

	if ((source != null) && SourcePermissionChecker.contains(permissionChecker, source, ActionKeys.UPDATE)) {
		driverPassword = SecretResolverUtil.resolve(source.getCompanyId(), StringBundler.concat(Source.class.getSimpleName(), StringPool.SLASH, source.getCompanyId(), StringPool.SLASH, source.getSourceId(), "/driverPassword"), source.getDriverPassword());
	}
}

boolean validJDBCConnection = true;

try {
	ReportsEngineConsoleUtil.validateJDBCConnection(driverClassName, driverUrl, driverUserName, driverPassword);
}
catch (SourceJDBCConnectionException sjdbcce) {
	validJDBCConnection = false;
}
%>

<c:choose>
	<c:when test="<%= validJDBCConnection %>">
		<div class="portlet-msg-success">
			<liferay-ui:message key="you-have-successfully-connected-to-the-database" />
		</div>
	</c:when>
	<c:otherwise>
		<div class="portlet-msg-error">
			<liferay-ui:message key="could-not-connect-to-the-database.-please-verify-that-the-settings-are-correct" />
		</div>
	</c:otherwise>
</c:choose>