<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
EditAccountEntryAccountUserDisplayContext editAccountEntryAccountUserDisplayContext = (EditAccountEntryAccountUserDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);
%>

<aui:model-context bean="<%= editAccountEntryAccountUserDisplayContext.getSelectedAccountUser() %>" model="<%= User.class %>" />

<liferay-frontend:edit-form
	action="<%= editAccountEntryAccountUserDisplayContext.getEditAccountUserActionURL() %>"
>
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
	<aui:input name="backURL" type="hidden" value="<%= editAccountEntryAccountUserDisplayContext.getBackURL() %>" />
	<aui:input name="accountEntryId" type="hidden" value="<%= editAccountEntryAccountUserDisplayContext.getAccountEntryId() %>" />
	<aui:input name="accountUserId" type="hidden" value="<%= editAccountEntryAccountUserDisplayContext.getAccountUserId() %>" />

	<clay:sheet-header>
		<h2 class="sheet-title"><%= HtmlUtil.escape(editAccountEntryAccountUserDisplayContext.getTitle()) %></h2>
	</clay:sheet-header>

	<liferay-frontend:edit-form-body>
		<liferay-user:user-name-fields
			contact="<%= editAccountEntryAccountUserDisplayContext.getSelectedAccountUserContact() %>"
			user="<%= editAccountEntryAccountUserDisplayContext.getSelectedAccountUser() %>"
		/>
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<liferay-frontend:edit-form-buttons
			redirect="<%= editAccountEntryAccountUserDisplayContext.getBackURL() %>"
		/>
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>