<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/util/init.jsp" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><%@
taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %><%@
taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.portal.kernel.bean.ConstantsBeanFactoryUtil" %><%@
page import="com.liferay.portal.kernel.language.UnicodeLanguageUtil" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<c:set value="<%= ConstantsBeanFactoryUtil.getConstantsBean(Field.class) %>" var="Field" />
<c:set value="<%= ConstantsBeanFactoryUtil.getConstantsBean(PatcherConstants.class) %>" var="PatcherConstants" />

<c:set value="<%= ConstantsBeanFactoryUtil.getConstantsBean(PatcherBuildConstants.class) %>" var="PatcherBuildConstants" />
<c:set value="<%= new PatcherBuildConstants() %>" var="PatcherBuildConstantsMethods" />

<c:set value="<%= ConstantsBeanFactoryUtil.getConstantsBean(PatcherFixConstants.class) %>" var="PatcherFixConstants" />
<c:set value="<%= new PatcherFixConstants() %>" var="PatcherFixConstantsMethods" />

<c:set value="<%= ConstantsBeanFactoryUtil.getConstantsBean(PatcherFixPackConstants.class) %>" var="PatcherFixPackConstants" />

<c:set value="<%= ConstantsBeanFactoryUtil.getConstantsBean(PatcherProductVersionConstants.class) %>" var="PatcherProductVersionConstants" />
<c:set value="<%= new PatcherProductVersionConstants() %>" var="PatcherProductVersionConstantsMethods" />

<c:set value="<%= ConstantsBeanFactoryUtil.getConstantsBean(PatcherPortletKeys.class) %>" var="PatcherPortletKeys" />
<c:set value="<%= ConstantsBeanFactoryUtil.getConstantsBean(PortletPropsValues.class) %>" var="PortletPropsValues" />
<c:set value="<%= ConstantsBeanFactoryUtil.getConstantsBean(StringPool.class) %>" var="StringPool" />

<c:set value="<%= ConstantsBeanFactoryUtil.getConstantsBean(WorkflowConstants.class) %>" var="WorkflowConstants" />
<c:set value="<%= new WorkflowConstants() %>" var="WorkflowConstantsMethods" />

<c:set value="<%= new PatcherPermission() %>" var="PatcherPermission" />

<c:set value="<%= new JenkinsUtil() %>" var="JenkinsUtil" />
<c:set value="<%= new PatcherBuildRelUtil() %>" var="PatcherBuildRelUtil" />
<c:set value="<%= new PatcherBuildUtil() %>" var="PatcherBuildUtil" />
<c:set value="<%= new PatcherFixComponentUtil() %>" var="PatcherFixComponentUtil" />
<c:set value="<%= new PatcherFixPackUtil() %>" var="PatcherFixPackUtil" />
<c:set value="<%= new PatcherFixRelUtil() %>" var="PatcherFixRelUtil" />
<c:set value="<%= new PatcherFixUtil() %>" var="PatcherFixUtil" />
<c:set value="<%= new PatcherProductVersionUtil() %>" var="PatcherProductVersionUtil" />
<c:set value="<%= new PatcherProjectVersionUtil() %>" var="PatcherProjectVersionUtil" />
<c:set value="<%= new PatcherUtil() %>" var="PatcherUtil" />

<c:set value="<%= new PatcherBuildLocalServiceUtil() %>" var="PatcherBuildLocalServiceUtil" />
<c:set value="<%= new PatcherBuildRelLocalServiceUtil() %>" var="PatcherBuildRelLocalServiceUtil" />
<c:set value="<%= new PatcherFixComponentLocalServiceUtil() %>" var="PatcherFixComponentLocalServiceUtil" />
<c:set value="<%= new PatcherFixLocalServiceUtil() %>" var="PatcherFixLocalServiceUtil" />
<c:set value="<%= new PatcherFixPackLocalServiceUtil() %>" var="PatcherFixPackLocalServiceUtil" />
<c:set value="<%= new PatcherFixRelLocalServiceUtil() %>" var="PatcherFixRelLocalServiceUtil" />
<c:set value="<%= new PatcherProductVersionLocalServiceUtil() %>" var="PatcherProductVersionLocalServiceUtil" />
<c:set value="<%= new PatcherProjectVersionLocalServiceUtil() %>" var="PatcherProjectVersionLocalServiceUtil" />

<c:set value="<%= new GetterUtil() %>" var="GetterUtil" />
<c:set value="<%= new HtmlUtil() %>" var="HtmlUtil" />
<c:set value="<%= new ListUtil() %>" var="ListUtil" />
<c:set value="<%= new StringUtil() %>" var="StringUtil" />

<%!
public class AlloyLanguageUtil extends LanguageUtil {
	public AlloyLanguageUtil(PageContext pageContext) {
		_pageContext = pageContext;
	}

	public String format(String pattern) {
		return format(pattern, new Object[0]);
	}

	public String format(String pattern, Object... arguments) {
		return LanguageUtil.format(_pageContext, pattern, arguments);
	}

	public String formatUnicode(String pattern) {
		return formatUnicode(pattern, new Object[0]);
	}

	public String formatUnicode(String pattern, Object... arguments) {
		return UnicodeLanguageUtil.format(_pageContext, pattern, arguments);
	}

	private PageContext _pageContext;

}
%>

<c:set value="<%= new AlloyLanguageUtil(pageContext) %>" var="AlloyLanguageUtil" />