<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CPDefinitionConfigurationDisplayContext cpDefinitionConfigurationDisplayContext = (CPDefinitionConfigurationDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CPDAvailabilityEstimate cpdAvailabilityEstimate = cpDefinitionConfigurationDisplayContext.getCPDAvailabilityEstimate();
CPDefinition cpDefinition = cpDefinitionConfigurationDisplayContext.getCPDefinition();
long cpDefinitionId = cpDefinitionConfigurationDisplayContext.getCPDefinitionId();
CPDefinitionInventory cpDefinitionInventory = cpDefinitionConfigurationDisplayContext.getCPDefinitionInventory();
List<CPTaxCategory> cpTaxCategories = cpDefinitionConfigurationDisplayContext.getCPTaxCategories();

boolean shippable = BeanParamUtil.getBoolean(cpDefinition, request, "shippable", true);
%>

<portlet:actionURL name="/cp_definitions/edit_cp_definition" var="editProductDefinitionConfigurationActionURL" />

<aui:form action="<%= editProductDefinitionConfigurationActionURL %>" cssClass="pt-4" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="updateConfiguration" />
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
	<aui:input name="cpDefinitionInventoryId" type="hidden" value="<%= (cpDefinitionInventory == null) ? StringPool.BLANK : cpDefinitionInventory.getCPDefinitionInventoryId() %>" />
	<aui:input name="cpdAvailabilityEstimateId" type="hidden" value="<%= (cpdAvailabilityEstimate == null) ? StringPool.BLANK : cpdAvailabilityEstimate.getCPDAvailabilityEstimateId() %>" />
	<aui:input name="cpDefinitionId" type="hidden" value="<%= cpDefinitionId %>" />
	<aui:input name="workflowAction" type="hidden" value="<%= WorkflowConstants.ACTION_SAVE_DRAFT %>" />

	<aui:model-context bean="<%= cpDefinition %>" model="<%= CPDefinition.class %>" />

	<div class="row">
		<div class="col-8">
			<commerce-ui:panel
				title='<%= LanguageUtil.get(request, "tax-category") %>'
			>
				<div class="row">
					<div class="col-6">
						<aui:select label="tax-category" name="cpTaxCategoryId" showEmptyOption="<%= true %>">

							<%
							for (CPTaxCategory cpTaxCategory : cpTaxCategories) {
							%>

								<aui:option label="<%= HtmlUtil.escape(cpTaxCategory.getName(locale)) %>" selected="<%= (cpDefinition != null) && (cpDefinition.getCPTaxCategoryId() == cpTaxCategory.getCPTaxCategoryId()) %>" value="<%= cpTaxCategory.getCPTaxCategoryId() %>" />

							<%
							}
							%>

						</aui:select>
					</div>

					<div class="col-6">
						<aui:input checked="<%= (cpDefinition == null) ? false : cpDefinition.isTaxExempt() %>" name="taxExempt" type="toggle-switch" />
					</div>
				</div>
			</commerce-ui:panel>

			<commerce-ui:panel
				title='<%= LanguageUtil.get(request, "inventory") %>'
			>
				<div class="row">
					<aui:model-context bean="<%= cpDefinitionInventory %>" model="<%= CPDefinitionInventory.class %>" />

					<liferay-ui:error exception="<%= CPConfigurationEntryQuantityException.class %>" message="please-enter-a-valid-quantity" />
					<liferay-ui:error exception="<%= CPDefinitionInventoryAllowedOrderQuantitiesException.class %>" message="please-enter-valid-allowed-order-quantities" />
					<liferay-ui:error exception="<%= CPDefinitionInventoryQuantityException.class %>" message="please-enter-a-valid-quantity" />

					<div class="col-6">
						<aui:select label="inventory-engine" name="CPDefinitionInventoryEngine">

							<%
							for (CPDefinitionInventoryEngine cpDefinitionInventoryEngine : cpDefinitionConfigurationDisplayContext.getCPDefinitionInventoryEngines()) {
								String cpDefinitionInventoryEngineName = cpDefinitionInventoryEngine.getKey();
							%>

								<aui:option label="<%= HtmlUtil.escape(cpDefinitionInventoryEngine.getLabel(locale)) %>" selected="<%= (cpDefinitionInventory != null) && cpDefinitionInventoryEngineName.equals(cpDefinitionInventory.getCPDefinitionInventoryEngine()) %>" value="<%= HtmlUtil.escape(cpDefinitionInventoryEngineName) %>" />

							<%
							}
							%>

						</aui:select>

						<aui:select label="availability-estimate" name="commerceAvailabilityEstimateId" showEmptyOption="<%= true %>">

							<%
							for (CommerceAvailabilityEstimate commerceAvailabilityEstimate : cpDefinitionConfigurationDisplayContext.getCommerceAvailabilityEstimates()) {
							%>

								<aui:option label="<%= HtmlUtil.escape(commerceAvailabilityEstimate.getTitle(languageId)) %>" selected="<%= (cpdAvailabilityEstimate != null) && (commerceAvailabilityEstimate.getCommerceAvailabilityEstimateId() == cpdAvailabilityEstimate.getCommerceAvailabilityEstimateId()) %>" value="<%= commerceAvailabilityEstimate.getCommerceAvailabilityEstimateId() %>" />

							<%
							}
							%>

						</aui:select>

						<aui:input checked="<%= (cpDefinitionInventory == null) ? false : cpDefinitionInventory.getDisplayAvailability() %>" inlineField="<%= true %>" name="displayAvailability" type="toggle-switch" />

						<aui:input checked="<%= (cpDefinitionInventory == null) ? false : cpDefinitionInventory.isDisplayStockQuantity() %>" inlineField="<%= true %>" name="displayStockQuantity" type="toggle-switch" />

						<%
						BigDecimal minOrderQuantity = CPDefinitionInventoryConstants.DEFAULT_MIN_ORDER_QUANTITY;

						if (cpDefinitionInventory != null) {
							minOrderQuantity = cpDefinitionInventory.getMinOrderQuantity();
						}
						%>

						<aui:input ignoreRequestValue="<%= true %>" min="0.000001" name="minOrderQuantity" required="<%= true %>" step="0.000001" type="number" value="<%= minOrderQuantity.doubleValue() %>">
							<aui:validator name="min">0.000001</aui:validator>
							<aui:validator name="number" />
						</aui:input>

						<%
						String allowedOrderQuantitiesHelpMessage = LanguageUtil.format(request, "separate-values-with-a-space-following-the-x-format", "###,##0.00", false);
						%>

						<aui:input helpMessage="<%= allowedOrderQuantitiesHelpMessage %>" name="allowedOrderQuantities">
							<aui:validator errorMessage="<%= allowedOrderQuantitiesHelpMessage %>" name="custom">
								function(val) {
									const pattern = /^(\d{1,3}(,\d{3})*(\.\d{1,2})?)(\s\d{1,3}(,\d{3})*(\.\d{1,2})?)*$/;

									return pattern.test(val);
								}
							</aui:validator>
						</aui:input>
					</div>

					<div class="col-6">
						<aui:select label="low-stock-action" name="lowStockActivity">
							<aui:option selected="<%= cpDefinitionInventory == null %>" value="" />

							<%
							for (CommerceLowStockActivity commerceLowStockActivity : cpDefinitionConfigurationDisplayContext.getCommerceLowStockActivities()) {
								String commerceLowStockActivityName = commerceLowStockActivity.getKey();
							%>

								<aui:option label="<%= HtmlUtil.escape(commerceLowStockActivity.getLabel(locale)) %>" selected="<%= (cpDefinitionInventory != null) && commerceLowStockActivityName.equals(cpDefinitionInventory.getLowStockActivity()) %>" value="<%= HtmlUtil.escape(commerceLowStockActivityName) %>" />

							<%
							}
							%>

						</aui:select>

						<liferay-ui:error exception="<%= NumberFormatException.class %>" message="there-was-an-error-processing-one-or-more-of-the-quantities-entered" />

						<%
						BigDecimal minStockQuantity = BigDecimal.ZERO;

						if (cpDefinitionInventory != null) {
							minStockQuantity = cpDefinitionInventory.getMinStockQuantity();
						}
						%>

						<aui:input data-qa-id="minStockQuantityInput" ignoreRequestValue="<%= true %>" label="low-stock-threshold" min="0" name="minStockQuantity" type="number" value="<%= minStockQuantity.doubleValue() %>">
							<aui:validator name="min">0</aui:validator>
							<aui:validator name="number" />
						</aui:input>

						<aui:input checked="<%= (cpDefinitionInventory == null) ? false : cpDefinitionInventory.getBackOrders() %>" label="allow-back-orders" name="backOrders" type="toggle-switch" />

						<%
						BigDecimal maxOrderQuantity = CPDefinitionInventoryConstants.DEFAULT_MAX_ORDER_QUANTITY;

						if (cpDefinitionInventory != null) {
							maxOrderQuantity = cpDefinitionInventory.getMaxOrderQuantity();
						}
						%>

						<aui:input ignoreRequestValue="<%= true %>" min="0.000001" name="maxOrderQuantity" required="<%= true %>" type="number" value="<%= maxOrderQuantity.doubleValue() %>">
							<aui:validator name="min">0.000001</aui:validator>
							<aui:validator name="number" />
						</aui:input>

						<%
						BigDecimal multipleOrderQuantity = CPDefinitionInventoryConstants.DEFAULT_MULTIPLE_ORDER_QUANTITY;

						if (cpDefinitionInventory != null) {
							multipleOrderQuantity = cpDefinitionInventory.getMultipleOrderQuantity();
						}
						%>

						<aui:input ignoreRequestValue="<%= true %>" min="0.000001" name="multipleOrderQuantity" required="<%= true %>" step="0.000001" type="number" value="<%= multipleOrderQuantity.doubleValue() %>">
							<aui:validator name="min">0.000001</aui:validator>
							<aui:validator name="number" />
						</aui:input>
					</div>
				</div>
			</commerce-ui:panel>
		</div>

		<div class="col-4">
			<commerce-ui:panel
				title='<%= LanguageUtil.get(request, "base-settings") %>'
			>
				<aui:input checked="<%= cpDefinitionConfigurationDisplayContext.isPurchasable() %>" data-qa-id="purchasableInput" inlineField="<%= true %>" name="purchasable" type="toggle-switch" />
			</commerce-ui:panel>

			<commerce-ui:panel
				title='<%= LanguageUtil.get(request, "shipping") %>'
			>
				<aui:model-context bean="<%= cpDefinition %>" model="<%= CPDefinition.class %>" />

				<aui:input checked="<%= shippable %>" disabled="<%= (cpDefinition != null) && StringUtil.equalsIgnoreCase(cpDefinition.getProductTypeName(), VirtualCPTypeConstants.NAME) %>" name="shippable" type="toggle-switch" value="<%= shippable %>" />

				<div class="<%= shippable ? StringPool.BLANK : "hide" %>" id="<portlet:namespace />shippableOptions">
					<aui:input checked='<%= BeanParamUtil.getBoolean(cpDefinition, request, "freeShipping", false) %>' inlineField="<%= true %>" name="freeShipping" type="toggle-switch" />

					<aui:input checked='<%= BeanParamUtil.getBoolean(cpDefinition, request, "shipSeparately", false) %>' inlineField="<%= true %>" label="ship-separately" name="shipSeparately" type="toggle-switch" />

					<aui:input name="shippingExtraPrice" suffix="<%= HtmlUtil.escape(cpDefinitionConfigurationDisplayContext.getCommerceCurrencyCode()) %>" />

					<aui:input name="width" suffix="<%= HtmlUtil.escape(cpDefinitionConfigurationDisplayContext.getCPMeasurementUnitName(CPMeasurementUnitConstants.TYPE_DIMENSION)) %>" />

					<aui:input name="height" suffix="<%= HtmlUtil.escape(cpDefinitionConfigurationDisplayContext.getCPMeasurementUnitName(CPMeasurementUnitConstants.TYPE_DIMENSION)) %>" />

					<aui:input name="depth" suffix="<%= HtmlUtil.escape(cpDefinitionConfigurationDisplayContext.getCPMeasurementUnitName(CPMeasurementUnitConstants.TYPE_DIMENSION)) %>" />

					<aui:input name="weight" suffix="<%= HtmlUtil.escape(cpDefinitionConfigurationDisplayContext.getCPMeasurementUnitName(CPMeasurementUnitConstants.TYPE_WEIGHT)) %>" />
				</div>
			</commerce-ui:panel>
		</div>
	</div>
</aui:form>

<aui:script>
	Liferay.Util.toggleBoxes(
		'<portlet:namespace />shippable',
		'<portlet:namespace />shippableOptions'
	);
</aui:script>