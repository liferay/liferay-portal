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
taglib uri="http://liferay.com/tld/document-library" prefix="liferay-document-library" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.fragment.constants.FragmentActionKeys" %><%@
page import="com.liferay.fragment.contributor.FragmentCollectionContributor" %><%@
page import="com.liferay.fragment.exception.DuplicateFragmentCollectionException" %><%@
page import="com.liferay.fragment.exception.DuplicateFragmentCollectionKeyException" %><%@
page import="com.liferay.fragment.exception.DuplicateFragmentEntryKeyException" %><%@
page import="com.liferay.fragment.exception.FragmentCollectionNameException" %><%@
page import="com.liferay.fragment.exception.InvalidFileException" %><%@
page import="com.liferay.fragment.exception.NoSuchEntryException" %><%@
page import="com.liferay.fragment.exception.RequiredFragmentEntryException" %><%@
page import="com.liferay.fragment.importer.FragmentsImporterResultEntry" %><%@
page import="com.liferay.fragment.model.FragmentCollection" %><%@
page import="com.liferay.fragment.model.FragmentComposition" %><%@
page import="com.liferay.fragment.model.FragmentEntry" %><%@
page import="com.liferay.fragment.renderer.FragmentRendererController" %><%@
page import="com.liferay.fragment.service.FragmentCollectionLocalServiceUtil" %><%@
page import="com.liferay.fragment.web.internal.constants.FragmentWebKeys" %><%@
page import="com.liferay.fragment.web.internal.display.context.ConfigurationDisplayContext" %><%@
page import="com.liferay.fragment.web.internal.display.context.ContributedFragmentManagementToolbarDisplayContext" %><%@
page import="com.liferay.fragment.web.internal.display.context.EditFragmentEntryDisplayContext" %><%@
page import="com.liferay.fragment.web.internal.display.context.FragmentCollectionResourcesDisplayContext" %><%@
page import="com.liferay.fragment.web.internal.display.context.FragmentCollectionsDisplayContext" %><%@
page import="com.liferay.fragment.web.internal.display.context.FragmentCollectionsManagementToolbarDisplayContext" %><%@
page import="com.liferay.fragment.web.internal.display.context.FragmentDisplayContext" %><%@
page import="com.liferay.fragment.web.internal.display.context.FragmentEntryLinkDisplayContext" %><%@
page import="com.liferay.fragment.web.internal.display.context.FragmentEntryUsageManagementToolbarDisplayContext" %><%@
page import="com.liferay.fragment.web.internal.display.context.FragmentManagementToolbarDisplayContext" %><%@
page import="com.liferay.fragment.web.internal.display.context.FragmentManagementToolbarDisplayContextFactory" %><%@
page import="com.liferay.fragment.web.internal.display.context.FragmentServiceConfigurationDisplayContext" %><%@
page import="com.liferay.fragment.web.internal.display.context.GroupFragmentEntryLinkDisplayContext" %><%@
page import="com.liferay.fragment.web.internal.display.context.GroupFragmentEntryUsageManagementToolbarDisplayContext" %><%@
page import="com.liferay.fragment.web.internal.display.context.ImportDisplayContext" %><%@
page import="com.liferay.fragment.web.internal.display.context.RenderFragmentEntryDisplayContext" %><%@
page import="com.liferay.fragment.web.internal.frontend.taglib.clay.servlet.taglib.ContributedFragmentCompositionVerticalCard" %><%@
page import="com.liferay.fragment.web.internal.frontend.taglib.clay.servlet.taglib.ContributedFragmentEntryVerticalCard" %><%@
page import="com.liferay.fragment.web.internal.frontend.taglib.clay.servlet.taglib.FragmentEntryVerticalCardFactory" %><%@
page import="com.liferay.fragment.web.internal.security.permission.resource.FragmentPermission" %><%@
page import="com.liferay.fragment.web.internal.servlet.taglib.util.FragmentCollectionActionDropdownItemsProvider" %><%@
page import="com.liferay.frontend.taglib.servlet.taglib.util.EmptyResultMessageKeys" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.Group" %><%@
page import="com.liferay.portal.kernel.service.GroupLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.ListUtil" %><%@
page import="com.liferay.portal.kernel.util.MapUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %>

<%@ page import="java.util.List" %><%@
page import="java.util.Map" %><%@
page import="java.util.Objects" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
FragmentDisplayContext fragmentDisplayContext = new FragmentDisplayContext(request, renderRequest, renderResponse);
%>

<%@ include file="/init-ext.jsp" %>