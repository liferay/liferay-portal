<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
AccountEntryDisplay accountEntryDisplay = (AccountEntryDisplay)request.getAttribute(AccountWebKeys.ACCOUNT_ENTRY_DISPLAY);
%>

<liferay-util:buffer
	var="removeUserIcon"
>
	<a class="float-right remove-user-link" href="javascript:void(0);">
		<liferay-ui:icon
			icon="times-circle"
			markupView="lexicon"
			message="remove"
		/>
	</a>
</liferay-util:buffer>

<clay:sheet-section
	id='<%= liferayPortletResponse.getNamespace() + "personAccountUserContainer" %>'
>
	<clay:content-row
		containerElement="h3"
		cssClass="sheet-subtitle"
	>
		<clay:content-col
			containerElement="span"
			expand="<%= true %>"
		>
			<span class="heading-text"><liferay-ui:message key="user" /></span>
		</clay:content-col>

		<clay:content-col
			containerElement="span"
		>
			<span class="heading-end">
				<liferay-ui:icon
					id="selectUserButton"
					label="<%= true %>"
					linkCssClass="btn btn-secondary btn-sm"
					message="select"
					url="javascript:void(0);"
				/>
			</span>
		</clay:content-col>
	</clay:content-row>

	<%
	User personAccountEntryUser = accountEntryDisplay.getPersonAccountEntryUser();
	%>

	<aui:input name="personAccountEntryUserId" type="hidden" value="<%= String.valueOf(personAccountEntryUser != null ? personAccountEntryUser.getUserId() : 0) %>" />

	<liferay-ui:search-container
		compactEmptyResultsMessage="<%= true %>"
		emptyResultsMessage="assign-a-user-to-this-person-account"
		headerNames="name,email-address,job-title,null"
		id="personAccountEntryUserSearchContainer"
		total="<%= 1 %>"
	>
		<liferay-ui:search-container-results
			results="<%= ListUtil.filter(Collections.singletonList(personAccountEntryUser), Objects::nonNull) %>"
		/>

		<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.model.User"
			escapedModel="<%= true %>"
			keyPropertyName="userId"
		>
			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="name"
				property="fullName"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="email-address"
				property="emailAddress"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="job-title"
				property="jobTitle"
			/>

			<liferay-ui:search-container-column-text>
				<%= removeUserIcon %>
			</liferay-ui:search-container-column-text>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
			paginate="<%= false %>"
		/>
	</liferay-ui:search-container>
</clay:sheet-section>

<portlet:renderURL var="selectUserURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
	<portlet:param name="mvcPath" value="/account_entries_admin/select_account_users.jsp" />
	<portlet:param name="redirect" value="<%= currentURL %>" />
	<portlet:param name="accountEntryId" value="<%= String.valueOf(accountEntryDisplay.getAccountEntryId()) %>" />
	<portlet:param name="eventName" value='<%= liferayPortletResponse.getNamespace() + "selectPersonAccountEntryUser" %>' />
	<portlet:param name="navigation" value="all-users" />
	<portlet:param name="showCreateButton" value="<%= Boolean.TRUE.toString() %>" />
	<portlet:param name="showFilter" value="<%= Boolean.FALSE.toString() %>" />
	<portlet:param name="singleSelect" value="<%= Boolean.TRUE.toString() %>" />
</portlet:renderURL>

<liferay-frontend:component
	componentId="PersonAccountEntryEventHandler"
	context='<%=
		HashMapBuilder.<String, Object>put(
			"container", "#personAccountUserContainer"
		).put(
			"removeUserIconMarkup", removeUserIcon
		).put(
			"removeUserLinkSelector", ".remove-user-link"
		).put(
			"searchContainer", "personAccountEntryUserSearchContainer"
		).put(
			"selectUserButton", "#selectUserButton"
		).put(
			"selectUserEventName", "selectPersonAccountEntryUser"
		).put(
			"selectUserURL", selectUserURL.toString()
		).put(
			"userIdInput", "#personAccountEntryUserId"
		).build()
	%>'
	module="{PersonAccountEntryEventHandler} from account-admin-web"
/>