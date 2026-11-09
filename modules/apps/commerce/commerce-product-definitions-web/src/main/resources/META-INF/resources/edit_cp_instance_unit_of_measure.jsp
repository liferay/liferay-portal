<%--
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CPInstanceUnitOfMeasureDisplayContext cpInstanceUnitOfMeasureDisplayContext = (CPInstanceUnitOfMeasureDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure = cpInstanceUnitOfMeasureDisplayContext.getCPInstanceUnitOfMeasure();
%>

<liferay-frontend:side-panel-content
	title='<%= LanguageUtil.format(request, "edit-x", cpInstanceUnitOfMeasure.getName(languageId), false) %>'
>
	<portlet:actionURL name="/cp_definitions/edit_cp_instance_unit_of_measure" var="editCPInstanceUnitOfMeasureActionURL" />

	<aui:form action="<%= editCPInstanceUnitOfMeasureActionURL %>" method="post" name="fm">
		<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
		<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
		<aui:input name="cpDefinitionId" type="hidden" value="<%= cpInstanceUnitOfMeasureDisplayContext.getCPDefinitionId() %>" />
		<aui:input name="cpInstanceId" type="hidden" value="<%= cpInstanceUnitOfMeasure.getCPInstanceId() %>" />
		<aui:input name="cpInstanceUnitOfMeasureId" type="hidden" value="<%= cpInstanceUnitOfMeasure.getCPInstanceUnitOfMeasureId() %>" />
		<aui:input name="sku" type="hidden" value="<%= cpInstanceUnitOfMeasure.getSku() %>" />

		<liferay-ui:error exception="<%= CommercePriceEntryPriceException.class %>" message="please-enter-a-valid-price" />
		<liferay-ui:error exception="<%= CPInstanceUnitOfMeasureIncrementalOrderQuantityException.class %>" message="decimals-allowed-cannot-be-less-than-the-number-of-decimals-in-the-base-unit-quantity" />
		<liferay-ui:error exception="<%= CPInstanceUnitOfMeasurePriceException.class %>" message="please-enter-a-valid-price" />
		<liferay-ui:error exception="<%= CPInstanceUnitOfMeasureQuantityException.class %>" message="please-enter-a-valid-quantity" />
		<liferay-ui:error exception="<%= CPInstanceUnitOfMeasureRateException.class %>" message="conversion-rate-quantity-must-be-greater-than-zero" />
		<liferay-ui:error exception="<%= DuplicateCPInstanceUnitOfMeasureKeyException.class %>" message="there-is-another-unit-of-measure-with-the-same-key" />

		<aui:model-context bean="<%= cpInstanceUnitOfMeasure %>" model="<%= CPInstanceUnitOfMeasure.class %>" />

		<div class="row">
			<div class="col-12">
				<commerce-ui:panel
					title='<%= LanguageUtil.get(request, "details") %>'
				>
					<c:if test="<%= !cpInstanceUnitOfMeasure.isPrimary() %>">
						<div class="row">
							<div class="col-6">
								<aui:input helpMessage="set-as-primary-unit-of-measure-help" inlineLabel="right" label="set-as-primary-unit-of-measure" name="primary" type="toggle-switch" value="<%= cpInstanceUnitOfMeasure.getPrimary() %>" />
							</div>
						</div>

						<div class="row">
							<div class="col-6">
								<label class="field-label"><liferay-ui:message key="primary-unit-of-measure" /></label>

								<div class="col-6 form-group">
									<%= HtmlUtil.escape(cpInstanceUnitOfMeasureDisplayContext.getPrimaryCPInstanceUnitOfMeasureName()) %>
								</div>
							</div>
						</div>
					</c:if>

					<div class="row">
						<div class="col-6">
							<aui:input defaultLanguageId="<%= cpInstanceUnitOfMeasureDisplayContext.getCatalogDefaultLanguageId() %>" label="unit-of-measure" localized="<%= true %>" name="name" required="<%= true %>" value="<%= cpInstanceUnitOfMeasure.getName(locale) %>" />
						</div>

						<div class="col-6">
							<aui:input label="key" name="key" required="<%= true %>" type="text" />
						</div>
					</div>

					<c:choose>
						<c:when test="<%= cpInstanceUnitOfMeasure.isPrimary() %>">
							<div class="row">
								<div class="col-6">
									<aui:input label="decimal-allowed-precision" name="precision" required="<%= true %>" type="text" value="0">
										<aui:validator name="digits" />
										<aui:validator name="min">0</aui:validator>
									</aui:input>
								</div>

								<div class="col-6">
									<aui:input helpMessage="base-unit-quantity-help" ignoreRequestValue="<%= true %>" label="base-unit-quantity" name="incrementalOrderQuantity" required="<%= true %>" type="text" value='<%= BeanParamUtil.getDouble(cpInstanceUnitOfMeasure, request, "incrementalOrderQuantity", BigDecimal.ZERO.doubleValue()) %>'>
										<aui:validator name="number" />
										<aui:validator name="min">0</aui:validator>
									</aui:input>
								</div>
							</div>

							<div class="row">
								<div class="col-6">
									<aui:input helpMessage="priority-help" label="priority" name="priority" type="text">
										<aui:validator name="number" />
									</aui:input>
								</div>

								<div class="col-6">
									<aui:input helpMessage="pricing-quantity-help" ignoreRequestValue="<%= true %>" label="pricing-quantity" name="pricingQuantity" type="text" value='<%= BeanParamUtil.getDouble(cpInstanceUnitOfMeasure, request, "pricingQuantity", BigDecimal.ZERO.doubleValue()) %>'>
										<aui:validator name="number" />
										<aui:validator name="min">0</aui:validator>
									</aui:input>
								</div>
							</div>
						</c:when>
						<c:otherwise>
							<div class="row">
								<div class="col-6">
									<aui:input ignoreRequestValue="<%= true %>" label="conversion-rate-to-primary-unit-of-measure" name="rate" type="text" value="<%= cpInstanceUnitOfMeasure.getRate() %>">
										<aui:validator name="number" />

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
									<aui:input label="decimal-allowed-precision" name="precision" required="<%= true %>" type="text">
										<aui:validator name="digits" />
										<aui:validator name="min">0</aui:validator>
									</aui:input>
								</div>
							</div>

							<div class="row">
								<div class="col-6">
									<aui:input helpMessage="base-unit-quantity-help" ignoreRequestValue="<%= true %>" label="base-unit-quantity" name="incrementalOrderQuantity" required="<%= true %>" type="text" value='<%= BeanParamUtil.getDouble(cpInstanceUnitOfMeasure, request, "incrementalOrderQuantity", BigDecimal.ZERO.doubleValue()) %>'>
										<aui:validator name="number" />
										<aui:validator name="min">0</aui:validator>
									</aui:input>
								</div>

								<div class="col-6">
									<aui:input helpMessage="priority-help" label="priority" name="priority" type="text">
										<aui:validator name="number" />
									</aui:input>
								</div>
							</div>

							<div class="row">
								<div class="col-6">
									<aui:input helpMessage="pricing-quantity-help" ignoreRequestValue="<%= true %>" label="pricing-quantity" name="pricingQuantity" type="text" value='<%= BeanParamUtil.getDouble(cpInstanceUnitOfMeasure, request, "pricingQuantity", BigDecimal.ZERO.doubleValue()) %>'>
										<aui:validator name="number" />
										<aui:validator name="min">0</aui:validator>
									</aui:input>
								</div>
							</div>
						</c:otherwise>
					</c:choose>

					<div class="row">
						<div class="col-6">
							<aui:input checked="<%= true %>" inlineLabel="right" label="purchasable" name="active" type="checkbox" value="<%= cpInstanceUnitOfMeasure.getActive() %>" />
						</div>
					</div>
				</commerce-ui:panel>
			</div>
		</div>

		<aui:button-row>
			<aui:button cssClass="btn-lg btn-primary" name="saveButton" primary="<%= true %>" value="save" />

			<aui:button cssClass="btn-lg" type="cancel" />
		</aui:button-row>
	</aui:form>

	<liferay-frontend:component
		context='<%=
			HashMapBuilder.<String, Object>put(
				"message", LanguageUtil.get(request, "exit-with-primary-unit-of-measure-changed-saving-help")
			).put(
				"primary", cpInstanceUnitOfMeasure.isPrimary()
			).build()
		%>'
		module="{editCpInstanceUnitOfMeasure} from commerce-product-definitions-web"
	/>
</liferay-frontend:side-panel-content>