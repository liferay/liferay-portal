<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommerceDiscountDisplayContext commerceDiscountDisplayContext = (CommerceDiscountDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CommerceDiscount commerceDiscount = commerceDiscountDisplayContext.getCommerceDiscount();
long commerceDiscountId = commerceDiscountDisplayContext.getCommerceDiscountId();

boolean neverExpire = ParamUtil.getBoolean(request, "neverExpire", true);

if ((commerceDiscount != null) && (commerceDiscount.getExpirationDate() != null)) {
	neverExpire = false;
}

boolean usePercentage = ParamUtil.getBoolean(request, "usePercentage");

String target = ParamUtil.getString(request, "target");

if (Validator.isBlank(target)) {
	target = commerceDiscount.getTarget();
}

String colCssClass = "col-12 col-md-6";
String amountSuffix = HtmlUtil.escape(commerceDiscountDisplayContext.getDefaultCommerceCurrencyCode());

if (usePercentage) {
	colCssClass = "col-12 col-md-4";
	amountSuffix = StringPool.PERCENT;
}

boolean hasPermission = commerceDiscountDisplayContext.hasPermission(ActionKeys.UPDATE);
%>

<portlet:actionURL name="/commerce_discount/edit_commerce_discount" var="editCommerceDiscountActionURL" />

<aui:form action="<%= editCommerceDiscountActionURL %>" cssClass="pt-4" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= (commerceDiscount == null) ? Constants.ADD : Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
	<aui:input name="commerceDiscountId" type="hidden" value="<%= commerceDiscountId %>" />
	<aui:input name="externalReferenceCode" type="hidden" value="<%= commerceDiscount.getExternalReferenceCode() %>" />
	<aui:input name="workflowAction" type="hidden" value="<%= String.valueOf(WorkflowConstants.ACTION_SAVE_DRAFT) %>" />

	<aui:model-context bean="<%= commerceDiscount %>" model="<%= CommerceDiscount.class %>" />

	<liferay-ui:error exception="<%= CommerceDiscountAmountException.class %>" message="please-enter-a-valid-amount" />

	<liferay-ui:error exception="<%= CommerceDiscountMaxPriceValueException.class %>">
		<liferay-ui:message arguments="<%= CommercePriceConstants.PRICE_VALUE_MAX %>" key="price-max-value-is-x" />
	</liferay-ui:error>

	<liferay-ui:error exception="<%= CommerceDiscountMinPriceValueException.class %>">
		<liferay-ui:message arguments="<%= CommercePriceConstants.PRICE_VALUE_MIN %>" key="price-min-value-is-x" />
	</liferay-ui:error>

	<div class="row">
		<div class="col-12 col-xl-8">
			<commerce-ui:panel
				bodyClasses="flex-fill"
				collapsed="<%= false %>"
				collapsible="<%= false %>"
				title='<%= LanguageUtil.get(request, "details") %>'
			>
				<div class="row">
					<div class="col">
						<aui:input label="name" name="title" />
					</div>

					<div class="col-auto">
						<aui:input label='<%= HtmlUtil.escape("active") %>' name="active" type="toggle-switch" value="<%= commerceDiscount.isActive() %>" />
					</div>
				</div>

				<div class="row">
					<div class="col-6">
						<aui:select label="type" name="usePercentage" onChange='<%= liferayPortletResponse.getNamespace() + "selectType();" %>' required="<%= true %>">

							<%
							for (String commerceDiscountType : CommerceDiscountConstants.TYPES) {
							%>

								<aui:option label="<%= commerceDiscountType %>" selected="<%= commerceDiscountDisplayContext.getUsePercentage(commerceDiscountType) == commerceDiscount.isUsePercentage() %>" value="<%= commerceDiscountDisplayContext.getUsePercentage(commerceDiscountType) %>" />

							<%
							}
							%>

						</aui:select>
					</div>

					<div class="col-6">
						<aui:select label="apply-to" name="target" onChange='<%= liferayPortletResponse.getNamespace() + "selectTarget();" %>' required="<%= true %>">

							<%
							for (CommerceDiscountTarget commerceDiscountTarget : commerceDiscountDisplayContext.getCommerceDiscountTargets()) {
							%>

								<aui:option label="<%= commerceDiscountTarget.getLabel(locale) %>" selected="<%= Objects.equals(commerceDiscount.getTarget(), commerceDiscountTarget.getKey()) %>" value="<%= commerceDiscountTarget.getKey() %>" />

							<%
							}
							%>

						</aui:select>
					</div>
				</div>

				<div class="row">
					<div class="<%= colCssClass %>">
						<aui:input ignoreRequestValue="<%= true %>" name="amount" suffix="<%= amountSuffix %>" type="currency" value="<%= commerceDiscountDisplayContext.getCommerceDiscountAmount() %>">
							<aui:validator name="min"><%= CommercePriceConstants.PRICE_VALUE_MIN %></aui:validator>
							<aui:validator name="max"><%= CommercePriceConstants.PRICE_VALUE_MAX %></aui:validator>
							<aui:validator name="number" />
						</aui:input>
					</div>

					<c:if test="<%= usePercentage %>">
						<div class="<%= colCssClass %>">
							<aui:input ignoreRequestValue="<%= true %>" name="maximumDiscountAmount" suffix="<%= HtmlUtil.escape(commerceDiscountDisplayContext.getDefaultCommerceCurrencyCode()) %>" type="currency" value="<%= (commerceDiscount == null) ? BigDecimal.ZERO : commerceDiscountDisplayContext.getMaximumDiscountAmount() %>">
								<aui:validator name="min"><%= CommercePriceConstants.PRICE_VALUE_MIN %></aui:validator>
								<aui:validator name="max"><%= CommercePriceConstants.PRICE_VALUE_MAX %></aui:validator>
								<aui:validator name="number" />
							</aui:input>
						</div>
					</c:if>

					<div class="<%= colCssClass %>">
						<aui:select label="level" name="level" required="<%= true %>">

							<%
							for (String commerceDiscountLevel : CommerceDiscountConstants.LEVELS) {
							%>

								<aui:option label="<%= commerceDiscountLevel %>" selected="<%= Objects.equals(commerceDiscount.getLevel(), commerceDiscountLevel) %>" value="<%= commerceDiscountLevel %>" />

							<%
							}
							%>

						</aui:select>
					</div>
				</div>
			</commerce-ui:panel>
		</div>

		<div class="col-12 col-xl-4">
			<commerce-ui:panel
				bodyClasses="flex-fill"
				title='<%= LanguageUtil.get(request, "schedule[noun]") %>'
			>
				<liferay-ui:error exception="<%= CommerceDiscountExpirationDateException.class %>" message="please-select-a-valid-expiration-date" />

				<aui:input formName="fm" label="publish-date" name="displayDate" />

				<aui:input dateTogglerCheckboxLabel="never-expire" disabled="<%= neverExpire %>" formName="fm" name="expirationDate" />
			</commerce-ui:panel>
		</div>
	</div>

	<div class="row">
		<div class="col-12">
			<c:if test="<%= commerceDiscountDisplayContext.hasCustomAttributesAvailable() %>">
				<commerce-ui:panel
					title='<%= LanguageUtil.get(request, "custom-attributes") %>'
				>
					<liferay-expando:custom-attribute-list
						className="<%= CommerceDiscount.class.getName() %>"
						classPK="<%= (commerceDiscount != null) ? commerceDiscount.getCommerceDiscountId() : 0 %>"
						editable="<%= true %>"
						label="<%= true %>"
					/>
				</commerce-ui:panel>
			</c:if>
		</div>
	</div>

	<%@ include file="/commerce_discounts/coupon_code.jspf" %>

	<c:if test="<%= Objects.equals(target, CommerceDiscountConstants.TARGET_PRODUCT) %>">
		<%@ include file="/commerce_discounts/target/products.jspf" %>
	</c:if>

	<c:if test="<%= Objects.equals(target, CommerceDiscountConstants.TARGET_SKUS) %>">
		<%@ include file="/commerce_discounts/target/skus.jspf" %>
	</c:if>

	<c:if test="<%= Objects.equals(target, CommerceDiscountConstants.TARGET_CATEGORIES) %>">
		<%@ include file="/commerce_discounts/target/categories.jspf" %>
	</c:if>

	<c:if test="<%= Objects.equals(target, CommerceDiscountConstants.TARGET_PRICING_CLASS) %>">
		<%@ include file="/commerce_discounts/target/pricing_classes.jspf" %>
	</c:if>

	<%@ include file="/commerce_discounts/rules.jspf" %>
</aui:form>

<aui:script sandbox="<%= true %>">
	Liferay.provide(window, '<portlet:namespace />selectType', () => {
		const portletURL = Liferay.Util.PortletURL.createPortletURL(
			'<%= currentURLObj %>',
			{
				usePercentage: document.getElementById(
					'<portlet:namespace />usePercentage'
				).value,
			}
		);

		window.location.replace(portletURL.toString());
	});

	Liferay.provide(window, '<portlet:namespace />selectTarget', () => {
		const portletURL = Liferay.Util.PortletURL.createPortletURL(
			'<%= currentURLObj %>',
			{
				target: document.getElementById('<portlet:namespace />target')
					.value,
			}
		);

		window.location.replace(portletURL.toString());
	});
</aui:script>