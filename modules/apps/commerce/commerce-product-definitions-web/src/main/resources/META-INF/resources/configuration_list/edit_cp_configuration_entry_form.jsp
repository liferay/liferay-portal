<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CPConfigurationListDisplayContext cpConfigurationListDisplayContext = (CPConfigurationListDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CPConfigurationEntry cpConfigurationEntry = cpConfigurationListDisplayContext.getCPConfigurationEntry();
%>

<div>
	<liferay-frontend:fieldset
		collapsible="<%= true %>"
		cssClass="mb-3 panel-unstyled"
		label="base-settings"
	>
		<div class="row">
			<div class="col-6">
				<aui:input checked='<%= BeanParamUtil.getBoolean(cpConfigurationEntry, request, "purchasable", true) %>' data-qa-id="purchasableInput" inlineLabel="right" name="purchasable" type="toggle-switch" />
			</div>
		</div>
	</liferay-frontend:fieldset>

	<liferay-frontend:fieldset
		collapsible="<%= true %>"
		cssClass="mb-3 panel-unstyled"
		label="inventory"
	>
		<liferay-ui:error exception="<%= CPConfigurationEntryAllowedOrderQuantitiesException.class %>" message="please-enter-valid-allowed-order-quantities" />

		<div class="row">
			<div class="col-6">
				<aui:input checked='<%= BeanParamUtil.getBoolean(cpConfigurationEntry, request, "displayAvailability") %>' data-qa-id="displayAvailabilityInput" inlineLabel="right" name="displayAvailability" type="toggle-switch" />
			</div>

			<div class="col-6">
				<aui:input checked='<%= BeanParamUtil.getBoolean(cpConfigurationEntry, request, "backOrders", true) %>' data-qa-id="backOrdersInput" inlineLabel="right" label="allow-back-orders" name="backOrders" type="toggle-switch" />
			</div>

			<div class="col-6">
				<aui:input checked='<%= BeanParamUtil.getBoolean(cpConfigurationEntry, request, "displayStockQuantity") %>' data-qa-id="displayStockQuantityInput" inlineLabel="right" name="displayStockQuantity" type="toggle-switch" />
			</div>
		</div>

		<div class="row">
			<div class="col-6">
				<aui:select data-qa-id="CPDefinitionInventoryEngineInput" label="inventory-engine" name="CPDefinitionInventoryEngine">

					<%
					for (CPDefinitionInventoryEngine cpDefinitionInventoryEngine : cpConfigurationListDisplayContext.getCPDefinitionInventoryEngines()) {
						String cpDefinitionInventoryEngineKey = cpDefinitionInventoryEngine.getKey();
					%>

					<aui:option label="<%= HtmlUtil.escape(cpDefinitionInventoryEngine.getLabel(locale)) %>" selected='<%= cpDefinitionInventoryEngineKey.equals(BeanParamUtil.getString(cpConfigurationEntry, request, "CPDefinitionInventoryEngine")) %>' value="<%= HtmlUtil.escape(cpDefinitionInventoryEngineKey) %>" />

					<%
					}
					%>

				</aui:select>
			</div>

			<div class="col-6">
				<aui:select data-qa-id="lowStockActivityInput" label="low-stock-action" name="lowStockActivity">
					<aui:option selected="" value="" />

					<%
					for (CommerceLowStockActivity commerceLowStockActivity : cpConfigurationListDisplayContext.getCommerceLowStockActivities()) {
						String commerceLowStockActivityKey = commerceLowStockActivity.getKey();
					%>

					<aui:option label="<%= HtmlUtil.escape(commerceLowStockActivity.getLabel(locale)) %>" selected='<%= commerceLowStockActivityKey.equals(BeanParamUtil.getString(cpConfigurationEntry, request, "lowStockActivity")) %>' value="<%= HtmlUtil.escape(commerceLowStockActivityKey) %>" />

					<%
					}
					%>

				</aui:select>
			</div>

			<div class="col-6">
				<aui:select data-qa-id="commerceAvailabilityEstimateIdInput" label="availability-estimate" name="commerceAvailabilityEstimateId" showEmptyOption="<%= true %>">

					<%
					for (CommerceAvailabilityEstimate commerceAvailabilityEstimate : cpConfigurationListDisplayContext.getCommerceAvailabilityEstimates()) {
					%>

					<aui:option label="<%= HtmlUtil.escape(commerceAvailabilityEstimate.getTitle(languageId)) %>" selected='<%= BeanParamUtil.getLong(cpConfigurationEntry, request, "commerceAvailabilityEstimateId") == commerceAvailabilityEstimate.getCommerceAvailabilityEstimateId() %>' value="<%= commerceAvailabilityEstimate.getCommerceAvailabilityEstimateId() %>" />

					<%
					}
					%>

				</aui:select>
			</div>

			<div class="col-6">
				<aui:input data-qa-id="minStockQuantityInput" ignoreRequestValue="<%= true %>" label="low-stock-threshold" name="minStockQuantity" type="text" value='<%= BeanParamUtil.getDouble(cpConfigurationEntry, request, "minStockQuantity", BigDecimal.ONE.doubleValue()) %>'>
					<aui:validator errorMessage='<%= LanguageUtil.format(request, "please-enter-a-value-greater-than-or-equal-to-x", 0) %>' name="custom">
						function(val) {
						if (Number(val) >= 0) {
						return true;
						}
						return false;
						}
					</aui:validator>
				</aui:input>
			</div>

			<div class="col-6">
				<aui:input data-qa-id="minOrderQuantityInput" ignoreRequestValue="<%= true %>" name="minOrderQuantity" type="text" value='<%= BeanParamUtil.getDouble(cpConfigurationEntry, request, "minOrderQuantity", CPDefinitionInventoryConstants.DEFAULT_MIN_ORDER_QUANTITY.doubleValue()) %>'>
					<aui:validator name="required" />

					<aui:validator errorMessage='<%= LanguageUtil.format(request, "please-enter-a-value-greater-than-or-equal-to-x", 0) %>' name="custom">
						function(val) {
							if (Number(val) >= 0) {
								return true;
							}

							return false;
						}
					</aui:validator>
				</aui:input>
			</div>

			<div class="col-6">
				<aui:input data-qa-id="maxOrderQuantityInput" ignoreRequestValue="<%= true %>" name="maxOrderQuantity" type="text" value='<%= BeanParamUtil.getDouble(cpConfigurationEntry, request, "maxOrderQuantity", CPDefinitionInventoryConstants.DEFAULT_MAX_ORDER_QUANTITY.doubleValue()) %>'>
					<aui:validator name="required" />

					<aui:validator errorMessage='<%= LanguageUtil.format(request, "please-enter-a-value-greater-than-x", 0) %>' name="custom">
						function(val) {
							if (Number(val) > 0) {
								return true;
							}

							return false;
						}
					</aui:validator>
				</aui:input>
			</div>

			<div class="col-6">
				<aui:input data-qa-id="multipleOrderQuantityInput" ignoreRequestValue="<%= true %>" name="multipleOrderQuantity" type="text" value='<%= BeanParamUtil.getDouble(cpConfigurationEntry, request, "multipleOrderQuantity", CPDefinitionInventoryConstants.DEFAULT_MULTIPLE_ORDER_QUANTITY.doubleValue()) %>'>
					<aui:validator name="required" />

					<aui:validator errorMessage='<%= LanguageUtil.format(request, "please-enter-a-value-greater-than-x", 0) %>' name="custom">
						function(val) {
							if (Number(val) > 0) {
								return true;
							}

							return false;
						}
					</aui:validator>
				</aui:input>
			</div>

			<div class="col-6">

				<%
				String allowedOrderQuantitiesHelpMessage = LanguageUtil.format(request, "separate-values-with-a-space-following-the-x-format", "###,##0.00", false);
				%>

				<aui:input data-qa-id="allowedOrderQuantitiesInput" helpMessage="<%= allowedOrderQuantitiesHelpMessage %>" name="allowedOrderQuantities" value='<%= BeanParamUtil.getString(cpConfigurationEntry, request, "allowedOrderQuantities") %>'>
					<aui:validator errorMessage="<%= allowedOrderQuantitiesHelpMessage %>" name="custom">
						function(val) {
							const pattern = /^(\d{1,3}(,\d{3})*(\.\d{1,2})?)(\s\d{1,3}(,\d{3})*(\.\d{1,2})?)*$/;

							return pattern.test(val);
						}
					</aui:validator>
				</aui:input>
			</div>
		</div>
	</liferay-frontend:fieldset>

	<liferay-frontend:fieldset
		collapsible="<%= true %>"
		cssClass="mb-3 panel-unstyled"
		label="shipping"
	>

		<%
		boolean shippable = BeanParamUtil.getBoolean(cpConfigurationEntry, request, "shippable", true);
		%>

		<div class="row">
			<div class="col-6">
				<aui:input checked="<%= shippable %>" data-qa-id="shippableInput" disabled="<%= StringUtil.equalsIgnoreCase(cpConfigurationListDisplayContext.getProductTypeName(), VirtualCPTypeConstants.NAME) %>" inlineLabel="right" name="shippable" type="toggle-switch" />
			</div>

			<div class="col-6 <%= shippable ? StringPool.BLANK : "hide" %> show-if-shippable">
				<aui:input checked='<%= BeanParamUtil.getBoolean(cpConfigurationEntry, request, "freeShipping") %>' data-qa-id="freeShippingInput" inlineLabel="right" name="freeShipping" type="toggle-switch" />
			</div>

			<div class="col-6 <%= shippable ? StringPool.BLANK : "hide" %> show-if-shippable">
				<aui:input checked='<%= BeanParamUtil.getBoolean(cpConfigurationEntry, request, "shipSeparately") %>' data-qa-id="shipSeparatelyInput" inlineLabel="right" name="shipSeparately" type="toggle-switch" />
			</div>
		</div>

		<div class="<%= shippable ? StringPool.BLANK : "hide" %> row show-if-shippable">
			<div class="col-6">
				<aui:input data-qa-id="widthInput" name="width" suffix="<%= HtmlUtil.escape(cpConfigurationListDisplayContext.getCPMeasurementUnitName(CPMeasurementUnitConstants.TYPE_DIMENSION)) %>" value='<%= BeanParamUtil.getString(cpConfigurationEntry, request, "width") %>' />
			</div>

			<div class="col-6">
				<aui:input data-qa-id="heightInput" name="height" suffix="<%= HtmlUtil.escape(cpConfigurationListDisplayContext.getCPMeasurementUnitName(CPMeasurementUnitConstants.TYPE_DIMENSION)) %>" value='<%= BeanParamUtil.getString(cpConfigurationEntry, request, "height") %>' />
			</div>

			<div class="col-6">
				<aui:input data-qa-id="depthInput" name="depth" suffix="<%= HtmlUtil.escape(cpConfigurationListDisplayContext.getCPMeasurementUnitName(CPMeasurementUnitConstants.TYPE_DIMENSION)) %>" value='<%= BeanParamUtil.getString(cpConfigurationEntry, request, "depth") %>' />
			</div>

			<div class="col-6">
				<aui:input data-qa-id="weightInput" name="weight" suffix="<%= HtmlUtil.escape(cpConfigurationListDisplayContext.getCPMeasurementUnitName(CPMeasurementUnitConstants.TYPE_WEIGHT)) %>" value='<%= BeanParamUtil.getString(cpConfigurationEntry, request, "weight") %>' />
			</div>
		</div>
	</liferay-frontend:fieldset>

	<liferay-frontend:fieldset
		collapsible="<%= true %>"
		cssClass="mb-3 panel-unstyled"
		label="tax-category"
	>
		<div class="row">
			<div class="col-12">
				<aui:select data-qa-id="CPTaxCategoryIdInput" label="tax-category" name="CPTaxCategoryId" showEmptyOption="<%= true %>">

					<%
					for (CPTaxCategory cpTaxCategory : cpConfigurationListDisplayContext.getCPTaxCategories()) {
					%>

					<aui:option label="<%= HtmlUtil.escape(cpTaxCategory.getName(locale)) %>" selected='<%= BeanParamUtil.getLong(cpConfigurationEntry, request, "CPTaxCategoryId") == cpTaxCategory.getCPTaxCategoryId() %>' value="<%= cpTaxCategory.getCPTaxCategoryId() %>" />

					<%
					}
					%>

				</aui:select>
			</div>

			<div class="col-12">
				<aui:input checked='<%= BeanParamUtil.getBoolean(cpConfigurationEntry, request, "taxExempt") %>' data-qa-id="taxExemptInput" inlineLabel="right" name="taxExempt" type="toggle-switch" />
			</div>
		</div>
	</liferay-frontend:fieldset>
</div>