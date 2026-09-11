<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CPCompareContentMiniDisplayContext cpCompareContentMiniDisplayContext = (CPCompareContentMiniDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);
%>

<c:if test="<%= cpCompareContentMiniDisplayContext != null %>">

	<%
	CPDataSourceResult cpDataSourceResult = cpCompareContentMiniDisplayContext.getCPDataSourceResult();
	%>

	<c:choose>
		<c:when test="<%= !cpCompareContentMiniDisplayContext.hasCommerceChannel() %>">
			<div class="alert alert-info mx-auto">
				<liferay-ui:message key="this-site-does-not-have-a-channel" />
			</div>
		</c:when>
		<c:when test="<%= cpCompareContentMiniDisplayContext.isSelectionStyleADT() %>">
			<liferay-ddm:template-renderer
				className="<%= CPCompareContentMiniPortlet.class.getName() %>"
				contextObjects='<%=
					HashMapBuilder.<String, Object>put(
						"cpCompareContentMiniDisplayContext", cpCompareContentMiniDisplayContext
					).build()
				%>'
				displayStyle="<%= cpCompareContentMiniDisplayContext.getDisplayStyle() %>"
				displayStyleGroupId="<%= cpCompareContentMiniDisplayContext.getDisplayStyleGroupId() %>"
				entries="<%= cpDataSourceResult.getCPCatalogEntries() %>"
			/>
		</c:when>
		<c:when test="<%= cpCompareContentMiniDisplayContext.isSelectionStyleCustomRenderer() %>">
			<liferay-commerce-product:product-list-renderer
				CPDataSourceResult="<%= cpCompareContentMiniDisplayContext.getCPDataSourceResult() %>"
				entryKeys="<%= cpCompareContentMiniDisplayContext.getCPContentListEntryRendererKeys() %>"
				key="<%= cpCompareContentMiniDisplayContext.getCPContentListRendererKey() %>"
			/>
		</c:when>
		<c:otherwise>
		</c:otherwise>
	</c:choose>
</c:if>