<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CPInstanceCommercePriceEntryDisplayContext cpInstanceCommercePriceEntryDisplayContext = (CPInstanceCommercePriceEntryDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CommercePriceEntry commercePriceEntry = cpInstanceCommercePriceEntryDisplayContext.getCommercePriceEntry();

long commercePriceEntryId = commercePriceEntry.getCommercePriceEntryId();

CommercePriceList commercePriceList = commercePriceEntry.getCommercePriceList();

CommerceCurrency commerceCurrency = commercePriceList.getCommerceCurrency();

boolean priceOnApplication = BeanParamUtil.getBoolean(commercePriceEntry, request, "priceOnApplication", false);
%>

<portlet:actionURL name="/cp_definitions/edit_cp_instance_commerce_price_entry" var="editCommercePriceEntryActionURL" />

<aui:form action="<%= editCommercePriceEntryActionURL %>" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
	<aui:input name="commercePriceEntryId" type="hidden" value="<%= commercePriceEntryId %>" />
	<aui:input name="cpInstanceId" type="hidden" value="<%= cpInstanceCommercePriceEntryDisplayContext.getCPInstanceId() %>" />

	<liferay-ui:error exception="<%= CommercePriceEntryPriceException.class %>" message="please-enter-a-valid-price" />

	<aui:model-context bean="<%= commercePriceEntry %>" model="<%= CommercePriceEntry.class %>" />

	<div class="row">
		<div class="col-12">
			<commerce-ui:panel
				title='<%= LanguageUtil.get(request, "details") %>'
			>
				<div class="row">
					<div class="col-12">
						<aui:input checked="<%= priceOnApplication %>" helpMessage="do-not-set-a-base-price-for-this-product" inlineLabel="right" label="price-on-application" name="priceOnApplication" type="toggle-switch" />
					</div>
				</div>

				<aui:select disabled="<%= true %>" label="unit-of-measure" name="unitOfMeasureKey">

					<%
					String unitOfMeasureKey = commercePriceEntry.getUnitOfMeasureKey();

					for (CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure : cpInstanceCommercePriceEntryDisplayContext.getCPInstanceUnitOfMeasures()) {
					%>

					<aui:option label="<%= cpInstanceUnitOfMeasure.getKey() %>" selected="<%= unitOfMeasureKey.equals(cpInstanceUnitOfMeasure.getKey()) %>" value="<%= cpInstanceUnitOfMeasure.getKey() %>" />

					<%
					}
					%>

				</aui:select>

				<aui:fieldset collapsible="<%= false %>" cssClass='<%= "price-entry-price-settings " + (priceOnApplication ? "disabled" : StringPool.BLANK) %>' id='<%= liferayPortletResponse.getNamespace() + "price-entry-price-settings" %>'>
					<liferay-ui:error exception="<%= CommercePriceListMaxPriceValueException.class %>">
						<liferay-ui:message arguments="<%= CommercePriceConstants.PRICE_VALUE_MAX %>" key="price-max-value-is-x" />
					</liferay-ui:error>

					<liferay-ui:error exception="<%= CommercePriceListMinPriceValueException.class %>">
						<liferay-ui:message arguments="<%= CommercePriceConstants.PRICE_VALUE_MIN %>" key="price-min-value-is-x" />
					</liferay-ui:error>

					<%
					boolean discountDiscovery = BeanParamUtil.getBoolean(commercePriceEntry, request, "discountDiscovery", true);
					%>

					<div class="row">
						<div class="col-12">
							<aui:input checked="<%= !discountDiscovery %>" disabled="<%= priceOnApplication %>" helpMessage="override-discount-help" inlineLabel="right" label="override-discount" name="overrideDiscount" type="toggle-switch" />
						</div>

						<div class="col-12">
							<div class="<%= discountDiscovery ? "hide" : StringPool.BLANK %>" id="<portlet:namespace />discountLevels">
								<label class="control-label" for="<portlet:namespace />discountLevel1"><liferay-ui:message key="discount-levels" /></label>

								<div class="row">
									<div class="col-3">
										<aui:input disabled="<%= discountDiscovery || priceOnApplication %>" ignoreRequestValue="<%= true %>" inlineField="<%= true %>" label="l1" name="discountLevel1" type="currency" value="<%= cpInstanceCommercePriceEntryDisplayContext.getFormattedDiscount(commerceCurrency, commercePriceEntry.getDiscountLevel1()) %>" wrapperCssClass="discount-label-wrapper">
											<aui:validator name="min"><%= CommercePriceConstants.PRICE_VALUE_MIN %></aui:validator>
											<aui:validator name="max"><%= CommercePriceConstants.PRICE_VALUE_MAX %></aui:validator>
											<aui:validator name="number" />
										</aui:input>
									</div>

									<div class="col-3">
										<aui:input disabled="<%= discountDiscovery || priceOnApplication %>" ignoreRequestValue="<%= true %>" inlineField="<%= true %>" label="l2" name="discountLevel2" type="currency" value="<%= cpInstanceCommercePriceEntryDisplayContext.getFormattedDiscount(commerceCurrency, commercePriceEntry.getDiscountLevel2()) %>" wrapperCssClass="discount-label-wrapper">
											<aui:validator name="min"><%= CommercePriceConstants.PRICE_VALUE_MIN %></aui:validator>
											<aui:validator name="max"><%= CommercePriceConstants.PRICE_VALUE_MAX %></aui:validator>
											<aui:validator name="number" />
										</aui:input>
									</div>

									<div class="col-3">
										<aui:input disabled="<%= discountDiscovery || priceOnApplication %>" ignoreRequestValue="<%= true %>" inlineField="<%= true %>" label="l3" name="discountLevel3" type="currency" value="<%= cpInstanceCommercePriceEntryDisplayContext.getFormattedDiscount(commerceCurrency, commercePriceEntry.getDiscountLevel3()) %>" wrapperCssClass="discount-label-wrapper">
											<aui:validator name="min"><%= CommercePriceConstants.PRICE_VALUE_MIN %></aui:validator>
											<aui:validator name="max"><%= CommercePriceConstants.PRICE_VALUE_MAX %></aui:validator>
											<aui:validator name="number" />
										</aui:input>
									</div>

									<div class="col-3">
										<aui:input disabled="<%= discountDiscovery || priceOnApplication %>" ignoreRequestValue="<%= true %>" inlineField="<%= true %>" label="l4" name="discountLevel4" type="currency" value="<%= cpInstanceCommercePriceEntryDisplayContext.getFormattedDiscount(commerceCurrency, commercePriceEntry.getDiscountLevel4()) %>" wrapperCssClass="discount-label-wrapper">
											<aui:validator name="min"><%= CommercePriceConstants.PRICE_VALUE_MIN %></aui:validator>
											<aui:validator name="max"><%= CommercePriceConstants.PRICE_VALUE_MAX %></aui:validator>
											<aui:validator name="number" />
										</aui:input>
									</div>
								</div>
							</div>
						</div>
					</div>

					<div class="row">
						<div class="col-6">
							<aui:input cssClass="base-price" disabled="<%= true %>" label="base-price" name="base-price" type="text" value="<%= cpInstanceCommercePriceEntryDisplayContext.getBasePrice() %>" />
						</div>

						<%
						String priceInputLabel = "price-list-price";

						if (CommercePriceListConstants.TYPE_PROMOTION.equals(commercePriceList.getType())) {
							priceInputLabel = "promotion-price";
						}
						%>

						<div class="col-6">
							<aui:input disabled="<%= priceOnApplication %>" label="<%= priceInputLabel %>" name="price" suffix="<%= HtmlUtil.escape(commerceCurrency.getCode()) %>" type="currency" value="<%= cpInstanceCommercePriceEntryDisplayContext.getFormattedPrice(commerceCurrency) %>">
								<aui:validator name="min"><%= CommercePriceConstants.PRICE_VALUE_MIN %></aui:validator>
								<aui:validator name="max"><%= CommercePriceConstants.PRICE_VALUE_MAX %></aui:validator>
								<aui:validator name="number" />
							</aui:input>
						</div>
					</div>
				</aui:fieldset>

				<div class="row">
					<div class="col-6">
						<aui:input formName="fm" label="publish-date" name="displayDate" />
					</div>

					<%
					boolean neverExpire = ParamUtil.getBoolean(request, "neverExpire", true);

					if ((commercePriceEntry != null) && (commercePriceEntry.getExpirationDate() != null)) {
						neverExpire = false;
					}
					%>

					<div class="col-6">
						<liferay-ui:error exception="<%= CommercePriceListExpirationDateException.class %>" message="please-enter-a-valid-expiration-date" />

						<aui:input dateTogglerCheckboxLabel="never-expire" disabled="<%= neverExpire %>" formName="fm" name="expirationDate" />
					</div>
				</div>
			</commerce-ui:panel>

			<commerce-ui:panel
				title='<%= LanguageUtil.get(request, "tier-price") %>'
			>
				<div class="row">
					<div class="col-12">
				<div class="tier-price-entries">
					<aui:fieldset collapsible="<%= false %>" cssClass="price-entry-price-settings" label="settings">
						<div class="row">
							<div class="col-12">
								<aui:input checked="<%= commercePriceEntry.isBulkPricing() %>" label="bulk-pricing" name="bulkPricing" type="radio" value="<%= true %>" />
							</div>

							<div class="col-12">
								<aui:input checked="<%= !commercePriceEntry.isBulkPricing() %>" label="tiered-pricing" name="bulkPricing" type="radio" value="<%= false %>" />
							</div>
						</div>
					</aui:fieldset>

					<%@ include file="/commerce_price_lists/cp_instance/cp_instance_commerce_tier_price_entries.jspf" %>
				</div>
			</div>
			</commerce-ui:panel>
		</div>

		<div class="col-12">
			<c:if test="<%= CustomAttributesUtil.hasCustomAttributes(company.getCompanyId(), CommercePriceEntry.class.getName(), commercePriceEntryId, null) %>">
				<commerce-ui:panel
					title='<%= LanguageUtil.get(request, "custom-attribute") %>'
				>
					<liferay-expando:custom-attribute-list
						className="<%= CommercePriceEntry.class.getName() %>"
						classPK="<%= (commercePriceEntry != null) ? commercePriceEntry.getCommercePriceEntryId() : 0 %>"
						editable="<%= true %>"
						label="<%= true %>"
					/>
				</commerce-ui:panel>
			</c:if>
		</div>
	</div>

	<aui:button-row cssClass="price-entry-button-row">
		<aui:button cssClass="btn-lg" type="submit" />
	</aui:button-row>
</aui:form>

<liferay-frontend:component
	module="{PriceEntry} from commerce-pricing-web"
/>