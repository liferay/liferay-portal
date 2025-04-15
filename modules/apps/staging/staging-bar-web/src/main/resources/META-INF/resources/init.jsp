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
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/security" prefix="liferay-security" %><%@
taglib uri="http://liferay.com/tld/staging" prefix="liferay-staging" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.exportimport.kernel.background.task.BackgroundTaskExecutorNames" %><%@
page import="com.liferay.exportimport.kernel.exception.RemoteExportException" %><%@
page import="com.liferay.exportimport.kernel.staging.LayoutStagingUtil" %><%@
page import="com.liferay.exportimport.kernel.staging.StagingUtil" %><%@
page import="com.liferay.frontend.taglib.clay.servlet.taglib.util.JSPNavigationItemList" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.dao.orm.QueryUtil" %><%@
page import="com.liferay.portal.kernel.dao.search.ResultRow" %><%@
page import="com.liferay.portal.kernel.exception.LayoutBranchNameException" %><%@
page import="com.liferay.portal.kernel.exception.LayoutSetBranchNameException" %><%@
page import="com.liferay.portal.kernel.exception.PortalException" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.Group" %><%@
page import="com.liferay.portal.kernel.model.Layout" %><%@
page import="com.liferay.portal.kernel.model.LayoutBranch" %><%@
page import="com.liferay.portal.kernel.model.LayoutRevision" %><%@
page import="com.liferay.portal.kernel.model.LayoutRevisionConstants" %><%@
page import="com.liferay.portal.kernel.model.LayoutSetBranch" %><%@
page import="com.liferay.portal.kernel.model.LayoutSetBranchConstants" %><%@
page import="com.liferay.portal.kernel.model.User" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder" %><%@
page import="com.liferay.portal.kernel.security.auth.AuthException" %><%@
page import="com.liferay.portal.kernel.security.permission.ActionKeys" %><%@
page import="com.liferay.portal.kernel.service.LayoutBranchLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.LayoutLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.LayoutRevisionLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.LayoutSetBranchLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.UserLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.permission.GroupPermissionUtil" %><%@
page import="com.liferay.portal.kernel.service.permission.LayoutBranchPermissionUtil" %><%@
page import="com.liferay.portal.kernel.service.permission.LayoutPermissionUtil" %><%@
page import="com.liferay.portal.kernel.service.permission.LayoutSetBranchPermissionUtil" %><%@
page import="com.liferay.portal.kernel.servlet.SessionErrors" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.HttpComponentsUtil" %><%@
page import="com.liferay.portal.kernel.util.ListUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.PortletKeys" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.UnicodeProperties" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.kernel.util.comparator.LayoutRevisionCreateDateComparator" %><%@
page import="com.liferay.portal.kernel.workflow.WorkflowConstants" %><%@
page import="com.liferay.portal.kernel.workflow.WorkflowTask" %><%@
page import="com.liferay.portal.util.PropsValues" %><%@
page import="com.liferay.staging.bar.web.internal.display.context.LayoutBranchDisplayContext" %><%@
page import="com.liferay.staging.bar.web.internal.display.context.LayoutSetBranchDisplayContext" %><%@
page import="com.liferay.staging.bar.web.internal.display.context.StagingBarDisplayContext" %><%@
page import="com.liferay.staging.constants.StagingProcessesWebKeys" %>

<%@ page import="jakarta.portlet.PortletMode" %><%@
page import="jakarta.portlet.PortletRequest" %>

<%@ page import="java.util.ArrayList" %><%@
page import="java.util.List" %><%@
page import="java.util.Objects" %>

<liferay-frontend:defineObjects />

<liferay-staging:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
group = (Group)renderRequest.getAttribute(WebKeys.GROUP);
layout = (Layout)renderRequest.getAttribute(WebKeys.LAYOUT);
privateLayout = GetterUtil.getBoolean((String)renderRequest.getAttribute(WebKeys.PRIVATE_LAYOUT));

LayoutBranchDisplayContext layoutBranchDisplayContext = new LayoutBranchDisplayContext(request);
LayoutSetBranchDisplayContext layoutSetBranchDisplayContext = new LayoutSetBranchDisplayContext(request);
StagingBarDisplayContext stagingBarDisplayContext = new StagingBarDisplayContext(liferayPortletRequest, liferayPortletResponse, layout);
%>

<%!
private long _getLastImportLayoutRevisionId(Group group, Layout layout, User user) {
	long lastImportLayoutRevisionId = 0;

	try {
		Layout liveLayout = null;

		if (group.isStagedRemotely() &&
			StagingUtil.hasRemoteLayout(user.getUserId(), group.getGroupId(), layout.getPlid())) {

			liveLayout = StagingUtil.getRemoteLayout(user.getUserId(), group.getGroupId(), layout.getPlid());
		}
		else if (group.isStagingGroup()) {
			liveLayout = LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(layout.getUuid(), group.getLiveGroupId(), layout.isPrivateLayout());
		}

		if (liveLayout != null) {
			UnicodeProperties typeSettingsProperties = liveLayout.getTypeSettingsProperties();

			lastImportLayoutRevisionId = GetterUtil.getLong(typeSettingsProperties.getProperty("last-import-layout-revision-id"));
		}
	}
	catch (Exception exception) {
	}

	return lastImportLayoutRevisionId;
}

private String _getStatusMessage(LayoutRevision layoutRevision, long liveLayoutRevisionId) {
	String statusMessage = null;

	if (layoutRevision.isHead()) {
		statusMessage = "ready-for-publish-process";
	}

	if (layoutRevision.getLayoutRevisionId() == liveLayoutRevisionId) {
		statusMessage = "in-live";
	}

	return statusMessage;
}
%>

<%@ include file="/init-ext.jsp" %>