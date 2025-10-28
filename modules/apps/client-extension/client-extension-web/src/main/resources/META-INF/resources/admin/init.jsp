<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%@ taglib uri="http://liferay.com/tld/learn" prefix="liferay-learn" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %>

<%@ page import="com.liferay.client.extension.exception.ClientExtensionEntryNameException" %><%@
page import="com.liferay.client.extension.exception.ClientExtensionEntryTypeSettingsException" %><%@
page import="com.liferay.client.extension.type.CommerceCheckoutStepCET" %><%@
page import="com.liferay.client.extension.type.CustomElementCET" %><%@
page import="com.liferay.client.extension.type.EditorConfigContributorCET" %><%@
page import="com.liferay.client.extension.type.FDSCellRendererCET" %><%@
page import="com.liferay.client.extension.type.FDSFilterCET" %><%@
page import="com.liferay.client.extension.type.GlobalCSSCET" %><%@
page import="com.liferay.client.extension.type.GlobalJSCET" %><%@
page import="com.liferay.client.extension.type.IFrameCET" %><%@
page import="com.liferay.client.extension.type.JSImportMapsEntryCET" %><%@
page import="com.liferay.client.extension.type.StaticContentCET" %><%@
page import="com.liferay.client.extension.type.ThemeCSSCET" %><%@
page import="com.liferay.client.extension.type.ThemeFaviconCET" %><%@
page import="com.liferay.client.extension.type.ThemeSpritemapCET" %><%@
page import="com.liferay.client.extension.type.annotation.CETProperty" %><%@
page import="com.liferay.client.extension.web.internal.constants.ClientExtensionAdminFDSNames" %><%@
page import="com.liferay.client.extension.web.internal.constants.ClientExtensionAdminWebKeys" %><%@
page import="com.liferay.client.extension.web.internal.display.context.ClientExtensionAdminDisplayContext" %><%@
page import="com.liferay.client.extension.web.internal.display.context.EditClientExtensionEntryDisplayContext" %><%@
page import="com.liferay.client.extension.web.internal.display.context.ViewClientExtensionEntryDisplayContext" %><%@
page import="com.liferay.learn.LearnMessageUtil" %><%@
page import="com.liferay.portal.kernel.exception.PortalException" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %>

<%@ page import="java.lang.reflect.Method" %>

<%@ page import="java.util.Collection" %>