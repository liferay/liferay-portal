<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/html/common/init.jsp" %>

<%@ page import="com.liferay.admin.kernel.util.PortalProductMenuApplicationType" %><%@
page import="com.liferay.exportimport.kernel.lar.DefaultConfigurationPortletDataHandler" %><%@
page import="com.liferay.layout.utility.page.kernel.LayoutUtilityPageEntryViewRenderer" %><%@
page import="com.liferay.layout.utility.page.kernel.LayoutUtilityPageEntryViewRendererRegistryUtil" %><%@
page import="com.liferay.layout.utility.page.kernel.constants.LayoutUtilityPageEntryConstants" %><%@
page import="com.liferay.portal.kernel.cluster.ClusterExecutorUtil" %><%@
page import="com.liferay.portal.kernel.cluster.ClusterNode" %><%@
page import="com.liferay.portal.kernel.dao.db.DBManagerUtil" %><%@
page import="com.liferay.portal.kernel.dao.db.DBType" %><%@
page import="com.liferay.portal.kernel.exception.LayoutPermissionException" %><%@
page import="com.liferay.portal.kernel.exception.PortletActiveException" %><%@
page import="com.liferay.portal.kernel.exception.RequiredLayoutException" %><%@
page import="com.liferay.portal.kernel.exception.RequiredRoleException" %><%@
page import="com.liferay.portal.kernel.exception.UserActiveException" %><%@
page import="com.liferay.portal.kernel.exception.UserEmailAddressException" %><%@
page import="com.liferay.portal.kernel.exception.UserLockoutException" %><%@
page import="com.liferay.portal.kernel.exception.UserPasswordException" %><%@
page import="com.liferay.portal.kernel.exception.UserReminderQueryException" %><%@
page import="com.liferay.portal.kernel.license.util.LicenseManagerUtil" %><%@
page import="com.liferay.portal.kernel.portlet.PortletConfigurationLayoutUtil" %><%@
page import="com.liferay.portal.kernel.service.LayoutLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.servlet.HttpHeaders" %><%@
page import="com.liferay.portal.kernel.servlet.filters.compoundsessionid.CompoundSessionIdSplitterUtil" %><%@
page import="com.liferay.portal.kernel.util.ClassUtil" %><%@
page import="com.liferay.portal.setup.SetupWizardUtil" %><%@
page import="com.liferay.portal.util.LicenseUtil" %><%@
page import="com.liferay.portal.util.MaintenanceUtil" %><%@
page import="com.liferay.portlet.configuration.kernel.util.PortletConfigurationApplicationType" %>