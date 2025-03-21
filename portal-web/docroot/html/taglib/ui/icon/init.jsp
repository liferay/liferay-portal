<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>

<%@ page contentType="text/html; charset=UTF-8" %>

<%@ page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.IntegerWrapper" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.taglib.util.TagResourceBundleUtil" %>

<%@ page import="java.util.HashMap" %><%@
page import="java.util.Map" %><%@
page import="java.util.ResourceBundle" %>

<%
ResourceBundle resourceBundle = TagResourceBundleUtil.getResourceBundle(pageContext);

IntegerWrapper iconListIconCount = (IntegerWrapper)request.getAttribute("liferay-ui:icon-list:icon-count");

if (iconListIconCount != null) {
	iconListIconCount.increment();
}

boolean iconListShowWhenSingleIcon = GetterUtil.getBoolean((String)request.getAttribute("liferay-ui:icon-list:showWhenSingleIcon"));
Boolean iconListSingleIcon = (Boolean)request.getAttribute("liferay-ui:icon-list:single-icon");

IntegerWrapper iconMenuIconCount = (IntegerWrapper)request.getAttribute("liferay-ui:icon-menu:icon-count");

if (iconMenuIconCount != null) {
	iconMenuIconCount.increment();
}

boolean iconMenuShowWhenSingleIcon = GetterUtil.getBoolean((String)request.getAttribute("liferay-ui:icon-menu:showWhenSingleIcon"));
Boolean iconMenuSingleIcon = (Boolean)request.getAttribute("liferay-ui:icon-menu:single-icon");

String ariaRole = (String)request.getAttribute("liferay-ui:icon:ariaRole");
boolean auiImage = GetterUtil.getBoolean((String)request.getAttribute("liferay-ui:icon:auiImage"));
String cssClass = GetterUtil.getString((String)request.getAttribute("liferay-ui:icon:cssClass"));
Map<String, Object> data = (Map<String, Object>)request.getAttribute("liferay-ui:icon:data");
String details = GetterUtil.getString((String)request.getAttribute("liferay-ui:icon:details"));
String icon = (String)request.getAttribute("liferay-ui:icon:icon");
String iconCssClass = (String)request.getAttribute("liferay-ui:icon:iconCssClass");
String id = (String)request.getAttribute("liferay-ui:icon:id");
String image = (String)request.getAttribute("liferay-ui:icon:image");
boolean forcePost = GetterUtil.getBoolean((String)request.getAttribute("liferay-ui:icon:forcePost"));
String message = (String)request.getAttribute("liferay-ui:icon:message");
boolean label = GetterUtil.getBoolean((String)request.getAttribute("liferay-ui:icon:label"));
String lang = GetterUtil.getString((String)request.getAttribute("liferay-ui:icon:lang"));
String linkCssClass = GetterUtil.getString((String)request.getAttribute("liferay-ui:icon:linkCssClass"));
boolean localizeMessage = GetterUtil.getBoolean((String)request.getAttribute("liferay-ui:icon:localizeMessage"));
String onClick = GetterUtil.getString((String)request.getAttribute("liferay-ui:icon:onClick"));
String src = (String)request.getAttribute("liferay-ui:icon:src");
String srcHover = (String)request.getAttribute("liferay-ui:icon:srcHover");
String target = GetterUtil.getString((String)request.getAttribute("liferay-ui:icon:target"));
boolean toolTip = GetterUtil.getBoolean((String)request.getAttribute("liferay-ui:icon:toolTip"));
String url = GetterUtil.getString((String)request.getAttribute("liferay-ui:icon:url"));
boolean useDialog = GetterUtil.getBoolean((String)request.getAttribute("liferay-ui:icon:useDialog"));

if (forcePost || useDialog) {
	if (data == null) {
		data = new HashMap<String, Object>();
	}

	data.put("senna-off", "true");
}

if (toolTip) {
	cssClass += " lfr-portal-tooltip";
}

if (iconMenuIconCount != null) {
	linkCssClass += " dropdown-item";
}

linkCssClass += " lfr-icon-item taglib-icon";
%>

<%!
private static final String _AUI_PATH = "../aui/";
%>