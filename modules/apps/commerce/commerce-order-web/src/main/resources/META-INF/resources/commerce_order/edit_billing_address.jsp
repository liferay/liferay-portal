<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String cmd = ParamUtil.getString(request, Constants.CMD);

CommerceOrderEditDisplayContext commerceOrderEditDisplayContext = (CommerceOrderEditDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CommerceAddress billingAddress = null;

CommerceOrder commerceOrder = commerceOrderEditDisplayContext.getCommerceOrder();

if ((commerceOrder != null) && Validator.isNull(cmd)) {
	billingAddress = commerceOrder.getBillingAddress();
}
%>

<portlet:actionURL name="/commerce_order/edit_commerce_order" var="editCommerceOrderBillingAddressActionURL" />

<div class="container-fluid container-fluid-max-xl p-4">
	<aui:form action="<%= editCommerceOrderBillingAddressActionURL %>" method="post" name="fm">
		<aui:input name="<%= Constants.CMD %>" type="hidden" value='<%= (billingAddress == null) ? "addBillingAddress" : "updateBillingAddress" %>' />
		<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
		<aui:input name="requestProcessed" type="hidden" value='<%= SessionMessages.contains(renderRequest, "requestProcessed") %>' />
		<aui:input name="commerceOrderId" type="hidden" value="<%= commerceOrder.getCommerceOrderId() %>" />

		<aui:model-context bean="<%= billingAddress %>" model="<%= CommerceAddress.class %>" />

		<aui:input name="name" wrapperCssClass="form-group-item" />

		<aui:input name="phoneNumber" wrapperCssClass="form-group-item" />

		<aui:input name="street1" wrapperCssClass="form-group-item" />

		<aui:input name="street2" wrapperCssClass="form-group-item" />

		<aui:input name="street3" wrapperCssClass="form-group-item" />

		<aui:select label="country" name="countryId" wrapperCssClass="form-group-item" />

		<aui:input name="zip" wrapperCssClass="form-group-item" />

		<aui:input name="city" wrapperCssClass="form-group-item" />

		<aui:select label="region" name="regionId" wrapperCssClass="form-group-item" />
	</aui:form>

	<aui:script>
		new Liferay.DynamicSelect([
			{
				select: '<portlet:namespace />countryId',
				selectData: function (callback) {
					function injectCountryPlaceholder(list) {
						var callbackList = [
							{
								countryId: '0',
								nameCurrentValue:
									'- <liferay-ui:message key="select-country" />',
							},
						];

						list.forEach((listElement) => {
							callbackList.push(listElement);
						});

						callback(callbackList);
					}

					Liferay.Service(
						'/commerce.commercecountrymanagerimpl/get-billing-countries',
						{
							active: true,
							billingAllowed: true,
							companyId: '<%= company.getCompanyId() %>',
						},
						injectCountryPlaceholder
					);
				},
				selectDesc: 'nameCurrentValue',
				selectId: 'countryId',
				selectNullable: <%= false %>,
				selectSort: '<%= true %>',
				selectVal:
					'<%= BeanParamUtil.getLong(billingAddress, request, "countryId") %>',
			},
			{
				select: '<portlet:namespace />regionId',
				selectData: function (callback, selectKey) {
					function injectRegionPlaceholder(list) {
						var callbackList = [
							{
								regionId: '0',
								name: '- <liferay-ui:message key="select-region" />',
								nameCurrentValue:
									'- <liferay-ui:message key="select-region" />',
							},
						];

						list.forEach((listElement) => {
							callbackList.push(listElement);
						});

						callback(callbackList);
					}

					Liferay.Service(
						'/region/get-regions',
						{
							active: true,
							countryId: Number(selectKey),
						},
						injectRegionPlaceholder
					);
				},
				selectDesc: 'name',
				selectId: 'regionId',
				selectNullable: <%= false %>,
				selectVal:
					'<%= BeanParamUtil.getLong(billingAddress, request, "regionId") %>',
			},
		]);
	</aui:script>
</div>