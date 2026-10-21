<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/permissions/init.jsp" %>

<div aria-labelledby="<portlet:namespace />permissions" class="options-group" role="group">
	<clay:sheet-section>
		<span class="sheet-subtitle" id="<portlet:namespace />permissions">
			<liferay-ui:message key="permissions" />
		</span>

		<%
		ExportImportServiceConfiguration exportImportServiceConfiguration = ConfigurationProviderUtil.getCompanyConfiguration(ExportImportServiceConfiguration.class, company.getCompanyId());
		%>

		<liferay-staging:checkbox
			checked="<%= MapUtil.getBoolean(parameterMap, PortletDataHandlerKeys.PERMISSIONS, exportImportServiceConfiguration.publishPermissionsByDefault()) %>"
			disabled="<%= disableInputs %>"
			label="<%= inputTitle %>"
			name="<%= PortletDataHandlerKeys.PERMISSIONS %>"
			warning="<%= inputWarning %>"
		/>
	</clay:sheet-section>
</div>