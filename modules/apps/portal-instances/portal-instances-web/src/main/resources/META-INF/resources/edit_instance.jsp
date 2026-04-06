<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

Company selCompany = (Company)request.getAttribute(WebKeys.SEL_COMPANY);

long companyId = BeanParamUtil.getLong(selCompany, request, "companyId");

VirtualHost virtualHost = VirtualHostLocalServiceUtil.fetchCompanyDefaultVirtualHost(companyId);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);

renderResponse.setTitle(HtmlUtil.escape(selCompany.getWebId()));
%>

<portlet:actionURL name="/portal_instances/edit_instance" var="editInstanceURL" />

<clay:container-fluid>
	<liferay-frontend:edit-form
		action="<%= editInstanceURL %>"
		method="post"
		name="fm"
	>
		<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
		<aui:input name="companyId" type="hidden" value="<%= companyId %>" />

		<liferay-ui:error exception="<%= CompanyMaxUsersException.class %>" message="please-enter-a-valid-max-users" />
		<liferay-ui:error exception="<%= CompanyMxException.class %>" message="please-enter-a-valid-mail-domain" />
		<liferay-ui:error exception="<%= CompanyVirtualHostException.class %>" message="please-enter-a-valid-virtual-host" />
		<liferay-ui:error exception="<%= CompanyWebIdException.class %>" message="please-enter-a-valid-web-id" />

		<aui:model-context bean="<%= selCompany %>" model="<%= Company.class %>" />

		<aui:input name="id" type="resource" value="<%= String.valueOf(companyId) %>" />

		<aui:input name="web-id" type="resource" value="<%= selCompany.getWebId() %>" />

		<aui:input bean="<%= virtualHost %>" fieldParam="virtualHostname" label="virtual-host" model="<%= VirtualHost.class %>" name="hostname" />

		<aui:input label="mail-domain" name="mx" />

		<aui:input name="maxUsers" type="number"/>

		<c:if test="<%= selCompany.getCompanyId() != PortalInstancePool.getDefaultCompanyId() %>">
			<aui:input inlineLabel="right" labelCssClass="simple-toggle-switch" name="active" type="toggle-switch" value="<%= selCompany.isActive() %>" />
		</c:if>

		<liferay-frontend:edit-form-footer>
			<liferay-frontend:edit-form-buttons
				redirect="<%= HtmlUtil.escape(redirect) %>"
			/>
		</liferay-frontend:edit-form-footer>
	</liferay-frontend:edit-form>
</clay:container-fluid>