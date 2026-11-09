<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CPInstanceCommerceTierPriceEntryDisplayContext cpInstanceCommerceTierPriceEntryDisplayContext = (CPInstanceCommerceTierPriceEntryDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CommerceTierPriceEntry commerceTierPriceEntry = cpInstanceCommerceTierPriceEntryDisplayContext.getCommerceTierPriceEntry();
CommercePriceEntry commercePriceEntry = cpInstanceCommerceTierPriceEntryDisplayContext.getCommercePriceEntry();
CPDefinition cpDefinition = cpInstanceCommerceTierPriceEntryDisplayContext.getCPDefinition();
CPInstance cpInstance = cpInstanceCommerceTierPriceEntryDisplayContext.getCPInstance();
%>

<commerce-ui:modal-content
	title="<%= cpInstanceCommerceTierPriceEntryDisplayContext.getContextTitle() %>"
>
	<portlet:actionURL name="/cp_definitions/edit_cp_instance_commerce_tier_price_entry" var="editCommerceTierPriceEntryActionURL" />

	<aui:form action="<%= editCommerceTierPriceEntryActionURL %>" cssClass="container-fluid-1280" method="post" name="fm">
		<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
		<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
		<aui:input name="commercePriceEntryId" type="hidden" value="<%= cpInstanceCommerceTierPriceEntryDisplayContext.getCommercePriceEntryId() %>" />
		<aui:input name="commerceTierPriceEntryId" type="hidden" value="<%= cpInstanceCommerceTierPriceEntryDisplayContext.getCommerceTierPriceEntryId() %>" />
		<aui:input name="cpDefinitionId" type="hidden" value="<%= cpDefinition.getCPDefinitionId() %>" />
		<aui:input name="cpInstanceId" type="hidden" value="<%= cpInstance.getCPInstanceId() %>" />

		<liferay-ui:error exception="<%= CommerceTierPriceEntryMinQuantityException.class %>" message="the-specified-quantity-is-not-allowed" />
		<liferay-ui:error exception="<%= CommerceTierPriceEntryPriceException.class %>" message="please-enter-a-valid-price" />
		<liferay-ui:error exception="<%= CommerceTierPriceEntryQuantityException.class %>" message="please-enter-a-valid-quantity" />
		<liferay-ui:error exception="<%= DuplicateCommerceTierPriceEntryException.class %>" message="there-is-already-a-tier-price-entry-with-the-same-minimum-quantity" />

		<div class="row">
			<div class="col-12">

				<%
				BigDecimal minQuantity = BigDecimal.ZERO;

				if ((commerceTierPriceEntry != null) && (commerceTierPriceEntry.getMinQuantity() != null)) {
					minQuantity = commerceTierPriceEntry.getMinQuantity();
				}

				CommercePriceList commercePriceList = commercePriceEntry.getCommercePriceList();

				CommerceCurrency commerceCurrency = commercePriceList.getCommerceCurrency();

				boolean discountDiscovery = BeanParamUtil.getBoolean(commerceTierPriceEntry, request, "discountDiscovery", true);

				boolean neverExpire = ParamUtil.getBoolean(request, "neverExpire", true);

				if ((commerceTierPriceEntry != null) && (commerceTierPriceEntry.getExpirationDate() != null)) {
					neverExpire = false;
				}
				%>

				<aui:input label='<%= LanguageUtil.get(request, "quantity") %>' name="minQuantity" required="<%= true %>" value="<%= BigDecimalUtil.stripTrailingZeros(minQuantity) %>">
					<aui:validator name="min"><%= 0 %></aui:validator>
					<aui:validator name="number" />
				</aui:input>

				<aui:model-context bean="<%= commerceTierPriceEntry %>" model="<%= CommerceTierPriceEntry.class %>" />

				<aui:input label="tier-price" name="price" required="<%= true %>" suffix="<%= HtmlUtil.escape(commerceCurrency.getCode()) %>" type="currency" value="<%= cpInstanceCommerceTierPriceEntryDisplayContext.getFormattedPrice(commerceCurrency) %>">
					<aui:validator name="max"><%= CommercePriceConstants.PRICE_VALUE_MAX %></aui:validator>
					<aui:validator name="number" />
				</aui:input>

				<c:if test="<%= commercePriceEntry.isBulkPricing() %>">
					<aui:input helpMessage="override-discount-help" ignoreRequestValue="<%= true %>" inlineLabel="right" label='<%= LanguageUtil.get(request, "override-discount") %>' labelCssClass="simple-toggle-switch" name="overrideDiscount" type="toggle-switch" value="<%= !discountDiscovery %>" />

					<div class="<%= discountDiscovery ? "hide" : StringPool.BLANK %>" id="<portlet:namespace />discountLevels">
						<label class="control-label" for="<portlet:namespace />discountLevel1"><liferay-ui:message key="discount-levels" /></label>

						<div class="d-flex">
							<aui:input disabled="<%= discountDiscovery %>" ignoreRequestValue="<%= true %>" inlineField="<%= true %>" label="l1" name="discountLevel1" type="text" value="<%= (commerceTierPriceEntry == null) ? StringPool.BLANK : cpInstanceCommerceTierPriceEntryDisplayContext.getFormattedDiscount(commerceCurrency, commerceTierPriceEntry.getDiscountLevel1()) %>" wrapperCssClass="discount-label-wrapper">
								<aui:validator name="min"><%= CommercePriceConstants.PRICE_VALUE_MIN %></aui:validator>
								<aui:validator name="max"><%= CommercePriceConstants.PRICE_VALUE_MAX %></aui:validator>
								<aui:validator name="number" />
							</aui:input>

							<aui:input disabled="<%= discountDiscovery %>" ignoreRequestValue="<%= true %>" inlineField="<%= true %>" label="l2" name="discountLevel2" type="text" value="<%= (commerceTierPriceEntry == null) ? StringPool.BLANK : cpInstanceCommerceTierPriceEntryDisplayContext.getFormattedDiscount(commerceCurrency, commerceTierPriceEntry.getDiscountLevel2()) %>" wrapperCssClass="discount-label-wrapper">
								<aui:validator name="min"><%= CommercePriceConstants.PRICE_VALUE_MIN %></aui:validator>
								<aui:validator name="max"><%= CommercePriceConstants.PRICE_VALUE_MAX %></aui:validator>
								<aui:validator name="number" />
							</aui:input>

							<aui:input disabled="<%= discountDiscovery %>" ignoreRequestValue="<%= true %>" inlineField="<%= true %>" label="l3" name="discountLevel3" type="text" value="<%= (commerceTierPriceEntry == null) ? StringPool.BLANK : cpInstanceCommerceTierPriceEntryDisplayContext.getFormattedDiscount(commerceCurrency, commerceTierPriceEntry.getDiscountLevel3()) %>" wrapperCssClass="discount-label-wrapper">
								<aui:validator name="min"><%= CommercePriceConstants.PRICE_VALUE_MIN %></aui:validator>
								<aui:validator name="max"><%= CommercePriceConstants.PRICE_VALUE_MAX %></aui:validator>
								<aui:validator name="number" />
							</aui:input>

							<aui:input disabled="<%= discountDiscovery %>" ignoreRequestValue="<%= true %>" inlineField="<%= true %>" label="l4" name="discountLevel4" type="text" value="<%= (commerceTierPriceEntry == null) ? StringPool.BLANK : cpInstanceCommerceTierPriceEntryDisplayContext.getFormattedDiscount(commerceCurrency, commerceTierPriceEntry.getDiscountLevel4()) %>" wrapperCssClass="discount-label-wrapper">
								<aui:validator name="min"><%= CommercePriceConstants.PRICE_VALUE_MIN %></aui:validator>
								<aui:validator name="max"><%= CommercePriceConstants.PRICE_VALUE_MAX %></aui:validator>
								<aui:validator name="number" />
							</aui:input>
						</div>
					</div>
				</c:if>

				<liferay-ui:error exception="<%= CommercePriceListExpirationDateException.class %>" message="please-enter-a-valid-expiration-date" />

				<aui:input formName="fm" label="publish-date" name="displayDate" />
				<aui:input dateTogglerCheckboxLabel="never-expire" disabled="<%= neverExpire %>" formName="fm" name="expirationDate" />

				<c:if test="<%= commercePriceEntry.isBulkPricing() %>">
					<aui:script use="aui-base">
						A.one('#<portlet:namespace />overrideDiscount').on('change', function (event) {
							if (this.attr('checked')) {
								A.one('#<portlet:namespace />discountLevels').show();

								A.one('#<portlet:namespace />discountLevel1').attr('disabled', false);
								A.one('#<portlet:namespace />discountLevel1').removeClass('disabled');

								A.one('#<portlet:namespace />discountLevel2').attr('disabled', false);
								A.one('#<portlet:namespace />discountLevel2').removeClass('disabled');

								A.one('#<portlet:namespace />discountLevel3').attr('disabled', false);
								A.one('#<portlet:namespace />discountLevel3').removeClass('disabled');

								A.one('#<portlet:namespace />discountLevel4').attr('disabled', false);
								A.one('#<portlet:namespace />discountLevel4').removeClass('disabled');
							}
							else {
								A.one('#<portlet:namespace />discountLevels').hide();

								A.one('#<portlet:namespace />discountLevel1').attr('disabled', true);
								A.one('#<portlet:namespace />discountLevel1').addClass('disabled');

								A.one('#<portlet:namespace />discountLevel2').attr('disabled', true);
								A.one('#<portlet:namespace />discountLevel2').addClass('disabled');

								A.one('#<portlet:namespace />discountLevel3').attr('disabled', true);
								A.one('#<portlet:namespace />discountLevel3').addClass('disabled');

								A.one('#<portlet:namespace />discountLevel4').attr('disabled', true);
								A.one('#<portlet:namespace />discountLevel4').addClass('disabled');
							}
						});
					</aui:script>
				</c:if>
			</div>
		</div>
	</aui:form>
</commerce-ui:modal-content>