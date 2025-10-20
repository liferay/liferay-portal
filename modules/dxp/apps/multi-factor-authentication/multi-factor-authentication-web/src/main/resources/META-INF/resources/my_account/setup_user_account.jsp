<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
User selectedUser = PortalUtil.getSelectedUser(request);

SetupMFAChecker setupMFAChecker = (SetupMFAChecker)request.getAttribute(SetupMFAChecker.class.getName());
%>

<portlet:actionURL name="/my_account/setup_user_account" var="actionURL">
	<portlet:param name="mvcRenderCommandName" value="/users_admin/edit_user" />
</portlet:actionURL>

<aui:form action="<%= actionURL %>" cssClass="portlet-users-admin-edit-user" data-senna-off="true" method="post" name="fm">
	<aui:input name="p_u_i_d" type="hidden" value="<%= selectedUser.getUserId() %>" />
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
	<aui:input name="setupMFACheckerServiceId" type="hidden" value="<%= GetterUtil.getLong(request.getAttribute(MFAWebKeys.SETUP_MFA_CHECKER_SERVICE_ID)) %>" />

	<div class="sheet sheet-lg">
		<div class="sheet-header">
			<h1 class="sheet-title"><%= GetterUtil.getString(request.getAttribute(MFAWebKeys.MFA_USER_ACCOUNT_LABEL)) %></h1>
		</div>

		<liferay-ui:error key="setupUserAccountFailed" message="user-account-setup-failed" />

		<%
		setupMFAChecker.includeSetup(request, PipingServletResponseFactory.createPipingServletResponse(pageContext), selectedUser.getUserId());
		%>

	</div>
</aui:form>