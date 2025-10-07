<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/html/common/init.jsp" %>

<%@ page import="com.liferay.portal.kernel.cookies.constants.CookiesConstants" %><%@
page import="com.liferay.portal.kernel.frontend.esm.FrontendESMUtil" %><%@
page import="com.liferay.portal.kernel.frontend.spa.FrontendSPAUtil" %><%@
page import="com.liferay.portal.kernel.model.Portlet" %><%@
page import="com.liferay.portal.kernel.model.portlet.PortletDependency" %><%@
page import="com.liferay.portal.kernel.portlet.render.PortletRenderUtil" %><%@
page import="com.liferay.portal.kernel.session.timeout.SessionTimeoutUtil" %><%@
page import="com.liferay.portal.util.LayoutTypeAccessPolicyTracker" %><%@
page import="com.liferay.portlet.PortletTreeSet" %><%@
page import="com.liferay.portlet.internal.RenderStateUtil" %><%@
page import="com.liferay.taglib.aui.ScriptTag" %>

<%@ page import="java.util.Iterator" %>