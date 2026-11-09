<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%@ taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>

<%@ page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.bean.BeanParamUtil" %><%@
page import="com.liferay.portal.kernel.bean.BeanPropertiesUtil" %><%@
page import="com.liferay.portal.kernel.dao.search.ResultRow" %><%@
page import="com.liferay.portal.kernel.exception.NoSuchRoleException" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.util.DateUtil" %><%@
page import="com.liferay.portal.kernel.util.FastDateFormatFactoryUtil" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.kernel.uuid.PortalUUIDUtil" %><%@
page import="com.liferay.portal.kernel.workflow.RequiredWorkflowDefinitionException" %><%@
page import="com.liferay.portal.kernel.workflow.WorkflowConstants" %><%@
page import="com.liferay.portal.kernel.workflow.WorkflowDefinition" %><%@
page import="com.liferay.portal.kernel.workflow.WorkflowDefinitionFileException" %><%@
page import="com.liferay.portal.kernel.workflow.WorkflowDefinitionTitleException" %><%@
page import="com.liferay.portal.kernel.workflow.WorkflowException" %><%@
page import="com.liferay.portal.workflow.exception.IncompleteWorkflowInstancesException" %><%@
page import="com.liferay.portal.workflow.web.internal.dao.search.WorkflowDefinitionResultRowSplitter" %><%@
page import="com.liferay.portal.workflow.web.internal.display.context.WorkflowDefinitionDisplayContext" %><%@
page import="com.liferay.portal.workflow.web.internal.display.context.WorkflowDefinitionManagementToolbarDisplayContext" %>

<%@ page import="java.text.Format" %>

<liferay-frontend:defineObjects />

<%
WorkflowDefinitionDisplayContext workflowDefinitionDisplayContext = (WorkflowDefinitionDisplayContext)renderRequest.getAttribute(WorkflowWebKeys.WORKFLOW_DEFINITION_DISPLAY_CONTEXT);

Format displayDateFormat = null;

if (DateUtil.isFormatAmPm(locale)) {
	displayDateFormat = FastDateFormatFactoryUtil.getSimpleDateFormat(LanguageUtil.get(request, "mmm-d-yyyy-hh-mm-a"), locale, timeZone);
}
else {
	displayDateFormat = FastDateFormatFactoryUtil.getSimpleDateFormat(LanguageUtil.get(request, "mmm-d-yyyy-hh-mm"), locale, timeZone);
}
%>

<%@ include file="/definition/init-ext.jsp" %>

<aui:script use="liferay-workflow-web">
	window.<portlet:namespace />confirmDeleteDefinition = function (deleteURL) {
		var message =
			'<%= LanguageUtil.get(request, "a-deleted-workflow-cannot-be-recovered") %>';
		var title = '<%= LanguageUtil.get(request, "delete-workflow-question") %>';

		Liferay.WorkflowWeb.openConfirmDeleteDialog(title, message, deleteURL);

		return false;
	};
</aui:script>