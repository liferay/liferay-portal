<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/captcha" prefix="liferay-captcha" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.captcha.util.CaptchaUtil" %><%@
page import="com.liferay.document.library.kernel.document.conversion.DocumentConversionUtil" %><%@
page import="com.liferay.document.library.kernel.model.DLFileEntry" %><%@
page import="com.liferay.document.library.kernel.model.DLFileVersion" %><%@
page import="com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil" %><%@
page import="com.liferay.document.library.kernel.util.AudioConverter" %><%@
page import="com.liferay.document.library.kernel.util.VideoConverter" %><%@
page import="com.liferay.expando.kernel.model.ExpandoBridge" %><%@
page import="com.liferay.expando.kernel.model.ExpandoColumnConstants" %><%@
page import="com.liferay.petra.string.CharPool" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.convert.ConvertProcess" %><%@
page import="com.liferay.portal.convert.ConvertProcessUtil" %><%@
page import="com.liferay.portal.convert.documentlibrary.FileSystemStoreRootDirException" %><%@
page import="com.liferay.portal.kernel.backgroundtask.BackgroundTask" %><%@
page import="com.liferay.portal.kernel.backgroundtask.BackgroundTaskManagerUtil" %><%@
page import="com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants" %><%@
page import="com.liferay.portal.kernel.captcha.CaptchaConfigurationException" %><%@
page import="com.liferay.portal.kernel.captcha.CaptchaException" %><%@
page import="com.liferay.portal.kernel.captcha.CaptchaTextException" %><%@
page import="com.liferay.portal.kernel.dao.search.SearchContainer" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.*" %><%@
page import="com.liferay.portal.kernel.model.impl.*" %><%@
page import="com.liferay.portal.kernel.patcher.PatcherValues" %><%@
page import="com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder" %><%@
page import="com.liferay.portal.kernel.scripting.ScriptingException" %><%@
page import="com.liferay.portal.kernel.service.*" %><%@
page import="com.liferay.portal.kernel.servlet.SessionMessages" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.JavaConstants" %><%@
page import="com.liferay.portal.kernel.util.ListUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.Portal" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.PrefsPropsUtil" %><%@
page import="com.liferay.portal.kernel.util.PropsKeys" %><%@
page import="com.liferay.portal.kernel.util.PropsUtil" %><%@
page import="com.liferay.portal.kernel.util.ReleaseInfo" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.TextFormatter" %><%@
page import="com.liferay.portal.kernel.util.Time" %><%@
page import="com.liferay.portal.kernel.util.UnicodeProperties" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.workflow.WorkflowConstants" %><%@
page import="com.liferay.portal.log4j.Log4JUtil" %><%@
page import="com.liferay.portal.model.impl.*" %><%@
page import="com.liferay.portal.util.ShutdownUtil" %><%@
page import="com.liferay.server.admin.web.internal.constants.ImageMagickResourceLimitConstants" %><%@
page import="com.liferay.server.admin.web.internal.constants.ServerAdminNavigationEntryConstants" %><%@
page import="com.liferay.server.admin.web.internal.display.context.LogLevelsManagementToolbarDisplayContext" %><%@
page import="com.liferay.server.admin.web.internal.display.context.ServerDisplayContext" %><%@
page import="com.liferay.server.admin.web.internal.display.context.ViewPortalPropertiesDisplayContext" %><%@
page import="com.liferay.server.admin.web.internal.display.context.ViewPortalPropertiesManagementToolbarDisplayContext" %><%@
page import="com.liferay.server.admin.web.internal.display.context.ViewSystemPropertiesDisplayContext" %><%@
page import="com.liferay.server.admin.web.internal.image.ImageMagickUtil" %><%@
page import="com.liferay.server.admin.web.internal.scripting.util.ServerScriptingUtil" %><%@
page import="com.liferay.taglib.servlet.PipingServletResponseFactory" %>

<%@ page import="jakarta.portlet.PortletPreferences" %><%@
page import="jakarta.portlet.PortletRequest" %><%@
page import="jakarta.portlet.PortletURL" %>

<%@ page import="java.text.NumberFormat" %>

<%@ page import="java.util.ArrayList" %><%@
page import="java.util.Collection" %><%@
page import="java.util.Date" %><%@
page import="java.util.List" %><%@
page import="java.util.Map" %><%@
page import="java.util.Objects" %><%@
page import="java.util.Properties" %><%@
page import="java.util.TreeMap" %><%@
page import="java.util.function.Function" %>

<%@ page import="org.apache.logging.log4j.Level" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
String tabs1 = ParamUtil.getString(request, "tabs1", "resources");
String tabs2 = ParamUtil.getString(request, "tabs2");

AudioConverter audioConverter = (AudioConverter)request.getAttribute(AudioConverter.class.getName());
VideoConverter videoConverter = (VideoConverter)request.getAttribute(VideoConverter.class.getName());
%>

<%@ include file="/init-ext.jsp" %>

<%
PortletRequest portletRequest = (PortletRequest)request.getAttribute(JavaConstants.JAVAX_PORTLET_REQUEST);

if (portletRequest != null) {
	CaptchaUtil.enforceCaptcha(portletRequest);
}
else {
	CaptchaUtil.enforceCaptcha(request);
}
%>

<%!
private static final String[] _ALL_PRIORITIES = {String.valueOf(Level.OFF), String.valueOf(Level.FATAL), String.valueOf(Level.ERROR), String.valueOf(Level.WARN), String.valueOf(Level.INFO), String.valueOf(Level.DEBUG), String.valueOf(Level.TRACE), String.valueOf(Level.ALL)};
%>