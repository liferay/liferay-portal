<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %>

<%@ page import="com.liferay.fragment.collection.filter.category.display.context.FragmentCollectionFilterCategoryDisplayContext" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %>

<%
FragmentCollectionFilterCategoryDisplayContext fragmentCollectionFilterCategoryDisplayContext = (FragmentCollectionFilterCategoryDisplayContext)request.getAttribute(FragmentCollectionFilterCategoryDisplayContext.class.getName());
%>