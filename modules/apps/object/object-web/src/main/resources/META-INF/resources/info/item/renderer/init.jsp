<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<%@ page import="com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider" %><%@
page import="com.liferay.info.item.ClassPKInfoItemIdentifier" %><%@
page import="com.liferay.info.item.InfoItemReference" %><%@
page import="com.liferay.object.constants.ObjectWebKeys" %><%@
page import="com.liferay.object.model.ObjectEntry" %><%@
page import="com.liferay.object.rest.dto.v1_0.Link" %><%@
page import="com.liferay.portal.kernel.util.Validator" %>

<%@ page import="java.io.Serializable" %>

<%@ page import="java.util.Map" %>

<liferay-theme:defineObjects />

<%
AssetDisplayPageFriendlyURLProvider assetDisplayPageFriendlyURLProvider = (AssetDisplayPageFriendlyURLProvider)request.getAttribute(AssetDisplayPageFriendlyURLProvider.class.getName());
%>