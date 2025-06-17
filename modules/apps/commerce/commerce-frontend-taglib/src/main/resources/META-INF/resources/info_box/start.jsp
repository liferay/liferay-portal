<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/info_box/init.jsp" %>

<%
String linkId = HtmlUtil.escape(PortalUtil.generateRandomKey(request, "info-box") + "_action-link");
%>

<div class="info-box<%= Validator.isNotNull(elementClasses) ? StringPool.SPACE + elementClasses : StringPool.BLANK %>">
	<header class="align-items-center d-flex header justify-content-between pb-2">
		<c:if test="<%= Validator.isNotNull(title) %>">
			<div class="h5 mb-0 title"><%= HtmlUtil.escape(title) %></div>
		</c:if>

		<c:if test="<%= Validator.isNotNull(actionLabel) %>">

			<%
			String href = Validator.isNotNull(actionUrl) ? actionUrl : "#";
			%>

			<c:if test="<%= Validator.isNotNull(actionContext) %>">

				<%
				href = "#";
				%>

				<liferay-frontend:component
					context='<%=
						HashMapBuilder.<String, Object>put(
							"title", title
						).put(
							"url", actionUrl
						).putAll(
							actionContext
						).put(
							"linkId", linkId
						).build()
					%>'
					module="{ModalActionContextHandler} from commerce-frontend-taglib"
				/>
			</c:if>

			<clay:link
				href="<%= href %>"
				id="<%= linkId %>"
				label="<%= HtmlUtil.escape(actionLabel) %>"
			/>
		</c:if>
	</header>

	<div class="description">