<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<liferay-ui:search-container
	id="assigneesSearch"
	searchContainer='<%= (SearchContainer)request.getAttribute("edit_role_assignments.jsp-searchContainer") %>'
	var="segmentsEntrySearchContainer"
>
	<liferay-ui:search-container-row
		className="com.liferay.segments.model.SegmentsEntry"
		keyPropertyName="segmentsEntryId"
		modelVar="segmentsEntry"
	>
		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand table-title"
			name="name"
			value="<%= HtmlUtil.escape(segmentsEntry.getName(locale)) %>"
		/>

		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand-smallest table-cell-minw-150"
			name="active"
			translate="<%= true %>"
			value='<%= segmentsEntry.getActive() ? "yes" : "no" %>'
		/>

		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand-smallest table-cell-minw-150"
			name="source"
			value="<%= segmentsEntry.getSource() %>"
		/>

		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand-smallest table-cell-minw-150"
			name="scope"
			value="<%= HtmlUtil.escape(SegmentsEntryDisplayUtil.getGroupDescriptiveName(segmentsEntry, locale)) %>"
		/>

		<liferay-ui:search-container-column-date
			cssClass="table-cell-expand-smallest table-cell-minw-150 table-cell-ws-nowrap"
			name="create-date"
			value="<%= segmentsEntry.getCreateDate() %>"
		/>

		<c:choose>
			<c:when test='<%= Objects.equals(ParamUtil.getString(request, "tabs3"), "current") %>'>
				<portlet:renderURL var="viewMembersURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
					<portlet:param name="mvcPath" value="/view_segments_entry_users.jsp" />
					<portlet:param name="segmentsEntryId" value="<%= String.valueOf(segmentsEntry.getSegmentsEntryId()) %>" />
				</portlet:renderURL>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-smallest table-cell-minw-150"
					name="members"
				>
					<liferay-ui:icon
						label="<%= true %>"
						message="<%= String.valueOf(SegmentsEntryDisplayUtil.getSegmentsEntryUsersCount(segmentsEntry.getSegmentsEntryId())) %>"
						onClick='<%= liferayPortletResponse.getNamespace() + "openViewMembersDialog(event);" %>'
						url="<%= viewMembersURL %>"
					/>
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-text>
					<liferay-ui:icon-menu
						direction="left-side"
						icon="<%= StringPool.BLANK %>"
						markupView="lexicon"
						message="<%= StringPool.BLANK %>"
						showWhenSingleIcon="<%= true %>"
					>
						<liferay-ui:icon
							message="view-members"
							onClick='<%= liferayPortletResponse.getNamespace() + "openViewMembersDialog(event);" %>'
							url="<%= viewMembersURL %>"
						/>
					</liferay-ui:icon-menu>
				</liferay-ui:search-container-column-text>
			</c:when>
			<c:otherwise>
				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-smallest table-cell-minw-150"
					name="members"
					value="<%= String.valueOf(SegmentsEntryDisplayUtil.getSegmentsEntryUsersCount(segmentsEntry.getSegmentsEntryId())) %>"
				/>
			</c:otherwise>
		</c:choose>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		markupView="lexicon"
	/>
</liferay-ui:search-container>

<aui:script>
	function <portlet:namespace />openViewMembersDialog(event) {
		event.preventDefault();

		Liferay.Util.openModal({
			containerProps: {},
			iframeBodyCssClass: '',
			title: '<liferay-ui:message key="members" />',
			url: event.currentTarget.href,
		});
	}
</aui:script>