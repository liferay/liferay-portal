<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
long commerceOrderId = ParamUtil.getLong(request, "commerceOrderId");
%>

<div class="container-fluid container-fluid-max-xl p-4">
	<c:if test='<%= GetterUtil.getBoolean(request.getAttribute("showManualValidationForm")) %>'>
		<portlet:actionURL name="/commerce_order/add_commerce_order_account_validation" var="addAccountValidationActionURL" />

		<portlet:renderURL var="redirectURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
			<portlet:param name="mvcRenderCommandName" value="/commerce_order/view_commerce_order_account_validations" />
			<portlet:param name="commerceOrderId" value="<%= String.valueOf(commerceOrderId) %>" />
		</portlet:renderURL>

		<commerce-ui:panel
			title='<%= LanguageUtil.get(request, "manual-validation") %>'
		>
			<aui:form action="<%= addAccountValidationActionURL %>" method="post" name="fm">
				<aui:input name="commerceOrderId" type="hidden" value="<%= commerceOrderId %>" />
				<aui:input name="redirect" type="hidden" value="<%= redirectURL %>" />

				<aui:input label="manual-validation-reason" name="validationMessage" required="<%= true %>" type="textarea" />

				<div class="d-flex justify-content-end">
					<aui:button type="submit" />
				</div>
			</aui:form>
		</commerce-ui:panel>
	</c:if>

	<commerce-ui:panel
		title='<%= LanguageUtil.get(request, "history") %>'
	>
		<frontend-data-set:headless-display
			apiURL='<%= (String)request.getAttribute("accountValidationsAPIURL") %>'
			fdsFilters='<%= (List<FDSFilter>)request.getAttribute("accountValidationsFDSFilters") %>'
			id="<%= CommerceOrderFDSNames.ACCOUNT_VALIDATIONS %>"
			itemsPerPage="<%= 10 %>"
			showManagementBar="<%= true %>"
			showSearch="<%= false %>"
			style="fluid"
		/>
	</commerce-ui:panel>
</div>