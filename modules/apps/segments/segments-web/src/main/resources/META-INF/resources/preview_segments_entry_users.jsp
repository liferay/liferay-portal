<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
PreviewSegmentsEntryUsersDisplayContext previewSegmentsEntryUsersDisplayContext = (PreviewSegmentsEntryUsersDisplayContext)request.getAttribute(PreviewSegmentsEntryUsersDisplayContext.class.getName());

SearchContainer<User> userSearchContainer = previewSegmentsEntryUsersDisplayContext.getSearchContainer();
%>

<clay:container-fluid>
	<c:choose>
		<c:when test="<%= userSearchContainer.getTotal() > 0 %>">
			<liferay-ui:search-container
				searchContainer="<%= userSearchContainer %>"
			>
				<liferay-ui:search-container-row
					className="com.liferay.portal.kernel.model.User"
					escapedModel="<%= true %>"
					keyPropertyName="userId"
					modelVar="user2"
					rowIdProperty="screenName"
				>
					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand table-cell-minw-200 table-title"
						name="name"
						orderable="<%= true %>"
						property="fullName"
					/>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand table-cell-minw-200"
						name="email-address"
						orderable="<%= true %>"
						property="emailAddress"
					/>
				</liferay-ui:search-container-row>

				<liferay-ui:search-iterator
					markupView="lexicon"
				/>
			</liferay-ui:search-container>
		</c:when>
		<c:otherwise>
			<clay:alert
				cssClass="c-mt-5"
				displayType="info"
				message="<%= userSearchContainer.getEmptyResultsMessage() %>"
			/>
		</c:otherwise>
	</c:choose>
</clay:container-fluid>