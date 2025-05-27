<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ page import="com.liferay.alloy.mvc.AlloyController" %><%@
page import="com.liferay.alloy.mvc.AlloyException" %><%@
page import="com.liferay.alloy.mvc.AlloySearchResult" %><%@
page import="com.liferay.alloy.mvc.AlloyServiceInvoker" %><%@
page import="com.liferay.alloy.mvc.jsonwebservice.JSONWebServiceMethod" %><%@
page import="com.liferay.compat.portal.kernel.dao.orm.ProjectionFactoryUtil" %><%@
page import="com.liferay.compat.portal.kernel.servlet.HttpHeaders" %><%@
page import="com.liferay.compat.portal.kernel.util.ListUtil" %><%@
page import="com.liferay.compat.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.osb.patcher.model.PatcherAccount" %><%@
page import="com.liferay.osb.patcher.model.PatcherBuild" %><%@
page import="com.liferay.osb.patcher.model.PatcherFix" %><%@
page import="com.liferay.osb.patcher.model.PatcherFixComponent" %><%@
page import="com.liferay.osb.patcher.model.PatcherFixPack" %><%@
page import="com.liferay.osb.patcher.model.PatcherProductVersion" %><%@
page import="com.liferay.osb.patcher.model.PatcherProjectVersion" %><%@
page import="com.liferay.osb.patcher.model.impl.PatcherBuildImpl" %><%@
page import="com.liferay.osb.patcher.model.impl.PatcherBuildModelImpl" %><%@
page import="com.liferay.osb.patcher.model.impl.PatcherFixModelImpl" %><%@
page import="com.liferay.osb.patcher.model.impl.PatcherFixPackModelImpl" %><%@
page import="com.liferay.osb.patcher.model.impl.PatcherProjectVersionModelImpl" %><%@
page import="com.liferay.osb.patcher.service.PatcherAccountLocalServiceUtil" %><%@
page import="com.liferay.osb.patcher.service.PatcherBuildLocalServiceUtil" %><%@
page import="com.liferay.osb.patcher.service.PatcherBuildRelLocalServiceUtil" %><%@
page import="com.liferay.osb.patcher.service.PatcherFixComponentLocalServiceUtil" %><%@
page import="com.liferay.osb.patcher.service.PatcherFixLocalServiceUtil" %><%@
page import="com.liferay.osb.patcher.service.PatcherFixPackLocalServiceUtil" %><%@
page import="com.liferay.osb.patcher.service.PatcherFixRelLocalServiceUtil" %><%@
page import="com.liferay.osb.patcher.service.PatcherProductVersionLocalServiceUtil" %><%@
page import="com.liferay.osb.patcher.service.PatcherProjectVersionLocalServiceUtil" %><%@
page import="com.liferay.osb.patcher.util.*" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.dao.orm.DynamicQuery" %><%@
page import="com.liferay.portal.kernel.dao.orm.Projection" %><%@
page import="com.liferay.portal.kernel.dao.orm.Property" %><%@
page import="com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil" %><%@
page import="com.liferay.portal.kernel.dao.orm.QueryUtil" %><%@
page import="com.liferay.portal.kernel.dao.search.DisplayTerms" %><%@
page import="com.liferay.portal.kernel.json.JSONArray" %><%@
page import="com.liferay.portal.kernel.json.JSONFactoryUtil" %><%@
page import="com.liferay.portal.kernel.json.JSONObject" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.log.Log" %><%@
page import="com.liferay.portal.kernel.log.LogFactoryUtil" %><%@
page import="com.liferay.portal.kernel.messaging.MessageListener" %><%@
page import="com.liferay.portal.kernel.model.Layout" %><%@
page import="com.liferay.portal.kernel.model.User" %><%@
page import="com.liferay.portal.kernel.scheduler.CronTrigger" %><%@
page import="com.liferay.portal.kernel.scheduler.Trigger" %><%@
page import="com.liferay.portal.kernel.search.Document" %><%@
page import="com.liferay.portal.kernel.search.Field" %><%@
page import="com.liferay.portal.kernel.search.Indexer" %><%@
page import="com.liferay.portal.kernel.search.Sort" %><%@
page import="com.liferay.portal.kernel.security.permission.PermissionChecker" %><%@
page import="com.liferay.portal.kernel.util.BigDecimalUtil" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.KeyValuePair" %><%@
page import="com.liferay.portal.kernel.util.KeyValuePairComparator" %><%@
page import="com.liferay.portal.kernel.util.OrderByComparator" %><%@
page import="com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.ServiceBeanMethodInvocationFactoryUtil" %><%@
page import="com.liferay.portal.kernel.util.SetUtil" %><%@
page import="com.liferay.portal.kernel.util.StringBundler" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.servlet.BrowserSnifferUtil" %><%@
page import="com.liferay.portlet.PortletURLFactoryUtil" %>

<%@ page import="java.io.Serializable" %>

<%@ page import="java.lang.reflect.Method" %>

<%@ page import="java.util.ArrayList" %><%@
page import="java.util.Collections" %><%@
page import="java.util.Date" %><%@
page import="java.util.HashMap" %><%@
page import="java.util.HashSet" %><%@
page import="java.util.List" %><%@
page import="java.util.Map" %><%@
page import="java.util.Set" %><%@
page import="java.util.regex.Matcher" %><%@
page import="java.util.regex.Pattern" %>

<%@ page import="javax.portlet.PortletRequest" %><%@
page import="javax.portlet.PortletURL" %>