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
taglib uri="http://liferay.com/tld/expando" prefix="liferay-expando" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/frontend-data-set" prefix="frontend-data-set" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/security" prefix="liferay-security" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.commerce.product.constants.CPConstants" %><%@
page import="com.liferay.commerce.product.constants.CPPortletKeys" %><%@
page import="com.liferay.commerce.product.constants.CPWebKeys" %><%@
page import="com.liferay.commerce.product.exception.CPOptionCategoryKeyException" %><%@
page import="com.liferay.commerce.product.exception.CPOptionKeyException" %><%@
page import="com.liferay.commerce.product.exception.CPOptionValueKeyException" %><%@
page import="com.liferay.commerce.product.exception.CPSpecificationOptionKeyException" %><%@
page import="com.liferay.commerce.product.exception.DuplicateCPOptionExternalReferenceCodeException" %><%@
page import="com.liferay.commerce.product.exception.NoSuchCPOptionCategoryException" %><%@
page import="com.liferay.commerce.product.exception.NoSuchCPOptionException" %><%@
page import="com.liferay.commerce.product.exception.NoSuchCPOptionValueException" %><%@
page import="com.liferay.commerce.product.model.CPOption" %><%@
page import="com.liferay.commerce.product.model.CPOptionCategory" %><%@
page import="com.liferay.commerce.product.model.CPOptionValue" %><%@
page import="com.liferay.commerce.product.model.CPSpecificationOption" %><%@
page import="com.liferay.commerce.product.option.CommerceOptionType" %><%@
page import="com.liferay.commerce.product.options.web.internal.constants.CommerceOptionFDSNames" %><%@
page import="com.liferay.commerce.product.options.web.internal.constants.CommerceSpecificationOptionFDSNames" %><%@
page import="com.liferay.commerce.product.options.web.internal.display.context.CPOptionCategoryDisplayContext" %><%@
page import="com.liferay.commerce.product.options.web.internal.display.context.CPOptionCategoryManagementToolbarDisplayContext" %><%@
page import="com.liferay.commerce.product.options.web.internal.display.context.CPOptionDisplayContext" %><%@
page import="com.liferay.commerce.product.options.web.internal.display.context.CPOptionValueDisplayContext" %><%@
page import="com.liferay.commerce.product.options.web.internal.display.context.CPSpecificationOptionDisplayContext" %><%@
page import="com.liferay.commerce.product.options.web.internal.display.context.CPSpecificationOptionManagementToolbarDisplayContext" %><%@
page import="com.liferay.commerce.product.options.web.internal.security.permission.resource.CPOptionCategoryPermission" %><%@
page import="com.liferay.commerce.product.options.web.internal.security.permission.resource.CPSpecificationOptionPermission" %><%@
page import="com.liferay.commerce.product.options.web.internal.servlet.taglib.ui.constants.CPOptionCategoryFormNavigatorConstants" %><%@
page import="com.liferay.commerce.product.options.web.internal.servlet.taglib.ui.constants.CPSpecificationOptionFormNavigatorConstants" %><%@
page import="com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.bean.BeanParamUtil" %><%@
page import="com.liferay.portal.kernel.dao.search.ResultRow" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder" %><%@
page import="com.liferay.portal.kernel.security.permission.ActionKeys" %><%@
page import="com.liferay.portal.kernel.service.permission.PortletPermissionUtil" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.LocaleUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.taglib.util.CustomAttributesUtil" %>

<%@ page import="jakarta.portlet.PortletURL" %>

<%@ page import="java.util.ArrayList" %><%@
page import="java.util.Calendar" %><%@
page import="java.util.Collections" %><%@
page import="java.util.HashSet" %><%@
page import="java.util.List" %><%@
page import="java.util.Locale" %><%@
page import="java.util.Set" %><%@
page import="java.util.TimeZone" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />