<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
long segmentsEntryId = ParamUtil.getLong(request, "segmentsEntryId");
%>

<clay:container-fluid
	size="sm"
>
	<liferay-ui:search-container
		emptyResultsMessage="no-users-have-been-assigned-to-this-segment"
		iteratorURL="<%= currentURLObj %>"
		total="<%= SegmentsEntryDisplayUtil.getSegmentsEntryUsersCount(segmentsEntryId) %>"
		var="segmentsEntryUsersSearchContainer"
	>
		<liferay-ui:search-container-results
			results="<%= SegmentsEntryDisplayUtil.getSegmentsEntryUsers(segmentsEntryId, segmentsEntryUsersSearchContainer.getStart(), segmentsEntryUsersSearchContainer.getEnd()) %>"
		/>

		<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.model.User"
			escapedModel="<%= true %>"
			keyPropertyName="userId"
			modelVar="user2"
		>
			<liferay-ui:search-container-column-text>
				<liferay-user:user-portrait
					userId="<%= user2.getUserId() %>"
				/>
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-text
				colspan="<%= 2 %>"
				cssClass="justify-content-center"
				value="<%= user2.getFullName() %>"
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="descriptive"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</clay:container-fluid>