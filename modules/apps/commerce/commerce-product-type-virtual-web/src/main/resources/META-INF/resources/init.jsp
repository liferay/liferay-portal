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
taglib uri="http://liferay.com/tld/commerce-ui" prefix="commerce-ui" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/frontend-data-set" prefix="frontend-data-set" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.commerce.model.CommerceOrder" %><%@
page import="com.liferay.commerce.product.catalog.CPCatalogEntry" %><%@
page import="com.liferay.commerce.product.catalog.CPSku" %><%@
page import="com.liferay.commerce.product.constants.CPPortletKeys" %><%@
page import="com.liferay.commerce.product.content.constants.CPContentWebKeys" %><%@
page import="com.liferay.commerce.product.content.helper.CPContentHelper" %><%@
page import="com.liferay.commerce.product.model.CPDefinition" %><%@
page import="com.liferay.commerce.product.model.CPInstance" %><%@
page import="com.liferay.commerce.product.type.virtual.constants.VirtualCPTypeConstants" %><%@
page import="com.liferay.commerce.product.type.virtual.constants.VirtualCPTypeWebKeys" %><%@
page import="com.liferay.commerce.product.type.virtual.exception.CPDefinitionVirtualSettingSampleException" %><%@
page import="com.liferay.commerce.product.type.virtual.exception.CPDefinitionVirtualSettingSampleFileEntryIdException" %><%@
page import="com.liferay.commerce.product.type.virtual.exception.CPDefinitionVirtualSettingSampleURLException" %><%@
page import="com.liferay.commerce.product.type.virtual.exception.CPDefinitionVirtualSettingTermsOfUseArticleResourcePKException" %><%@
page import="com.liferay.commerce.product.type.virtual.exception.CPDefinitionVirtualSettingTermsOfUseContentException" %><%@
page import="com.liferay.commerce.product.type.virtual.exception.CPDefinitionVirtualSettingTermsOfUseException" %><%@
page import="com.liferay.commerce.product.type.virtual.exception.NoSuchCPDefinitionVirtualSettingException" %><%@
page import="com.liferay.commerce.product.type.virtual.model.CPDVirtualSettingFileEntry" %><%@
page import="com.liferay.commerce.product.type.virtual.model.CPDefinitionVirtualSetting" %><%@
page import="com.liferay.commerce.product.type.virtual.order.exception.CommerceVirtualOrderItemException" %><%@
page import="com.liferay.commerce.product.type.virtual.order.exception.CommerceVirtualOrderItemFileEntryIdException" %><%@
page import="com.liferay.commerce.product.type.virtual.order.exception.CommerceVirtualOrderItemUrlException" %><%@
page import="com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItem" %><%@
page import="com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItemFileEntry" %><%@
page import="com.liferay.commerce.product.type.virtual.util.VirtualCPTypeHelper" %><%@
page import="com.liferay.commerce.product.type.virtual.web.internal.constants.CPDefinitionVirtualSettingFDSNames" %><%@
page import="com.liferay.commerce.product.type.virtual.web.internal.display.context.CPDefinitionVirtualSettingDisplayContext" %><%@
page import="com.liferay.commerce.product.type.virtual.web.internal.display.context.CommerceVirtualOrderItemEditDisplayContext" %><%@
page import="com.liferay.commerce.product.type.virtual.web.internal.servlet.taglib.ui.constants.CPDefinitionVirtualSettingFormNavigatorConstants" %><%@
page import="com.liferay.journal.model.JournalArticle" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.bean.BeanParamUtil" %><%@
page import="com.liferay.portal.kernel.bean.BeanPropertiesUtil" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayPortletRequest" %><%@
page import="com.liferay.portal.kernel.repository.model.FileEntry" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.Time" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<%
String lifecycle = (String)request.getAttribute(LiferayPortletRequest.LIFECYCLE_PHASE);

String catalogURL = String.valueOf(PortalUtil.getControlPanelPortletURL(request, CPPortletKeys.CP_DEFINITIONS, lifecycle));
%>