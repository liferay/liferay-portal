<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/html/taglib/init.jsp" %>

<%
SearchContainer<?> searchContainer = (SearchContainer<?>)request.getAttribute("liferay-ui:search:searchContainer");

String id = (String)request.getAttribute("liferay-ui:search:id");

if (Validator.isNull(id) && (searchContainer != null)) {
	id = searchContainer.getId(request, namespace);

	id = id.concat("PageIterator");
}

String markupView = (String)request.getAttribute("liferay-ui:search:markupView");
int resultEnd = GetterUtil.getInteger((String)request.getAttribute("liferay-ui:search:resultEnd"));
String type = (String)request.getAttribute("liferay-ui:search:type");

PortletURL iteratorURL = searchContainer.getIteratorURL();

if (iteratorURL != null) {
	iteratorURL.setParameter("resetCur", Boolean.FALSE.toString());
}
%>

<c:if test="<%= searchContainer.getTotal() > 0 %>">
	<liferay-ui:page-iterator
		cur="<%= searchContainer.getCur() %>"
		curParam="<%= searchContainer.getCurParam() %>"
		delta="<%= searchContainer.getDelta() %>"
		deltaConfigurable="<%= searchContainer.isDeltaConfigurable() %>"
		deltaParam="<%= searchContainer.getDeltaParam() %>"
		forcePost="<%= searchContainer.isForcePost() %>"
		id="<%= id %>"
		markupView="<%= markupView %>"
		maxPages="<%= PropsValues.SEARCH_CONTAINER_PAGE_ITERATOR_MAX_PAGES %>"
		portletURL="<%= iteratorURL %>"
		resultEnd="<%= resultEnd %>"
		total="<%= searchContainer.getTotal() %>"
		type="<%= type %>"
	/>
</c:if>