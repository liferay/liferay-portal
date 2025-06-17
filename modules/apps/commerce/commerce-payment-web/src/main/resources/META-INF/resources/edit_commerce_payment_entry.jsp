<%--
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String backURL = ParamUtil.getString(request, "backURL", String.valueOf(renderResponse.createRenderURL()));

CommercePaymentEntryDisplayContext commercePaymentEntryDisplayContext = (CommercePaymentEntryDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CommercePaymentEntry commercePaymentEntry = commercePaymentEntryDisplayContext.getCommercePaymentEntry();

String note = (commercePaymentEntry == null) ? StringPool.BLANK : commercePaymentEntry.getNote();
int paymentStatus = (commercePaymentEntry == null) ? CommercePaymentEntryConstants.STATUS_PENDING : commercePaymentEntry.getPaymentStatus();

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(backURL);
%>

<liferay-portlet:renderURL var="editCommercePaymentEntryExternalReferenceCodeURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
	<portlet:param name="mvcRenderCommandName" value="/commerce_payment/edit_commerce_payment_entry_external_reference_code" />
	<portlet:param name="commercePaymentEntryId" value="<%= String.valueOf(commercePaymentEntryDisplayContext.getCommercePaymentEntryId()) %>" />
</liferay-portlet:renderURL>

<commerce-ui:header
	actions="<%= commercePaymentEntryDisplayContext.getHeaderActionModels() %>"
	bean="<%= commercePaymentEntry %>"
	beanIdLabel='<%= (commercePaymentEntry == null) ? null : "id" %>'
	externalReferenceCode="<%= (commercePaymentEntry == null) ? StringPool.BLANK : commercePaymentEntry.getExternalReferenceCode() %>"
	externalReferenceCodeEditUrl="<%= (commercePaymentEntry == null) ? null : editCommercePaymentEntryExternalReferenceCodeURL %>"
	model="<%= CommercePaymentEntry.class %>"
	title="<%= (commercePaymentEntry == null) ? StringPool.BLANK : String.valueOf(commercePaymentEntry.getCommercePaymentEntryId()) %>"
/>

<div class="container mt-4">
	<portlet:actionURL name="/commerce_payment/edit_commerce_payment_entry" var="editCommercePaymentEntryActionURL" />

	<aui:form action="<%= editCommercePaymentEntryActionURL %>" cssClass="pt-4" method="post" name="fm">
		<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= (commercePaymentEntry == null) ? Constants.ADD : Constants.UPDATE %>" />
		<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
		<aui:input name="backURL" type="hidden" value="<%= backURL %>" />
		<aui:input name="externalReferenceCode" type="hidden" value="<%= (commercePaymentEntry == null) ? StringPool.BLANK : commercePaymentEntry.getExternalReferenceCode() %>" />
		<aui:input name="commercePaymentEntryId" type="hidden" value="<%= commercePaymentEntryDisplayContext.getCommercePaymentEntryId() %>" />
		<aui:input name="commerceChannelId" type="hidden" value="<%= commercePaymentEntryDisplayContext.getCommerceChannelId() %>" />
		<aui:input name="className" type="hidden" value="<%= commercePaymentEntryDisplayContext.getClassName() %>" />
		<aui:input name="classPK" type="hidden" value="<%= commercePaymentEntryDisplayContext.getClassPK() %>" />
		<aui:input name="currencyCode" type="hidden" value="<%= commercePaymentEntryDisplayContext.getCurrencyCode() %>" />
		<aui:input name="languageId" type="hidden" value="<%= commercePaymentEntryDisplayContext.getLanguageId() %>" />
		<aui:input name="payload" type="hidden" value="<%= commercePaymentEntryDisplayContext.getPayload() %>" />
		<aui:input name="paymentIntegrationKey" type="hidden" value="<%= commercePaymentEntryDisplayContext.getPaymentIntegrationKey() %>" />
		<aui:input name="paymentIntegrationType" type="hidden" value="<%= commercePaymentEntryDisplayContext.getPaymentIntegrationType() %>" />
		<aui:input name="transactionCode" type="hidden" value="<%= commercePaymentEntryDisplayContext.getTransactionCode() %>" />
		<aui:input name="type" type="hidden" value="<%= CommercePaymentEntryConstants.TYPE_REFUND %>" />

		<liferay-ui:error embed="<%= false %>" exception="<%= CommercePaymentEntryAmountException.class %>" message="please-enter-a-valid-amount" />
		<liferay-ui:error embed="<%= false %>" exception="<%= CommercePaymentEntryPaymentIntegrationTypeException.class %>" message="the-payment-integration-type-is-invalid" />
		<liferay-ui:error embed="<%= false %>" exception="<%= CommercePaymentEntryPaymentStatusException.class %>" message="the-payment-status-is-invalid" />
		<liferay-ui:error embed="<%= false %>" exception="<%= CommercePaymentEntryReasonKeyException.class %>" message="please-select-a-valid-reason" />

		<aui:model-context bean="<%= commercePaymentEntry %>" model="<%= CommercePaymentEntry.class %>" />

		<div class="row">
			<div class="col-12">
				<commerce-ui:panel
					bodyClasses="flex-fill"
					title='<%= LanguageUtil.get(request, "payment-details") %>'
				>
					<div class="row vertically-divided">
						<div class="col-6">
							<commerce-ui:info-box
								elementClasses="py-3"
								title='<%= LanguageUtil.get(request, "payment-id") %>'
							>
								<p class="mb-0">#<%= String.valueOf(commercePaymentEntryDisplayContext.getClassPK()) %></p>
							</commerce-ui:info-box>

							<commerce-ui:info-box
								elementClasses="py-3"
								title='<%= LanguageUtil.get(request, "related-to") %>'
							>
								<p class="mb-0"><%= commercePaymentEntryDisplayContext.getRelatedToClassName() %></p>

								<c:choose>
									<c:when test="<%= commercePaymentEntryDisplayContext.isRelatedToOrder() %>">
										<a href="<%= commercePaymentEntryDisplayContext.getRelatedToURL() %>">
											<p class="mb-0">#<%= commercePaymentEntryDisplayContext.getRelatedToClassPK() %></p>
										</a>
									</c:when>
									<c:otherwise>
										<p class="mb-0">#<%= commercePaymentEntryDisplayContext.getRelatedToClassPK() %></p>
									</c:otherwise>
								</c:choose>
							</commerce-ui:info-box>
						</div>

						<div class="col-6">
							<commerce-ui:info-box
								elementClasses="py-3"
								title='<%= LanguageUtil.get(request, "payment-method") %>'
							>
								<p class="mb-0"><%= commercePaymentEntryDisplayContext.getPaymentMethod() %></p>
							</commerce-ui:info-box>

							<commerce-ui:info-box
								elementClasses="py-3"
								title='<%= LanguageUtil.get(request, "payment-date") %>'
							>
								<p class="mb-0"><%= commercePaymentEntryDisplayContext.getPaymentDate() %></p>
							</commerce-ui:info-box>
						</div>
					</div>
				</commerce-ui:panel>
			</div>
		</div>

		<div class="row">
			<div class="col-12">
				<commerce-ui:panel
					bodyClasses="flex-fill"
					title='<%= LanguageUtil.get(request, "refund-details") %>'
				>
					<div class="row vertically-divided">
						<div class="col-6">
							<commerce-ui:info-box
								elementClasses="py-3"
								title='<%= LanguageUtil.get(request, "payments-total-amount") %>'
							>
								<p class="mb-0"><%= commercePaymentEntryDisplayContext.getTotalAmountFormatted() %></p>
							</commerce-ui:info-box>

							<commerce-ui:info-box
								elementClasses="py-3"
								title='<%= LanguageUtil.get(request, "delivery") %>'
							>
								<p class="mb-0"><%= commercePaymentEntryDisplayContext.getDeliveryFormatted() %></p>
							</commerce-ui:info-box>

							<commerce-ui:info-box
								elementClasses="py-3"
								title='<%= LanguageUtil.get(request, "refund-already-completed") %>'
							>
								<p class="mb-0"><%= commercePaymentEntryDisplayContext.getRefundAlreadyCompleted() %></p>
							</commerce-ui:info-box>
						</div>

						<div class="col-6">
							<commerce-ui:info-box
								elementClasses="py-3"
								title='<%= LanguageUtil.get(request, "refund-status") %>'
							>
								<span class="mb-0">
									<clay:label
										displayType="<%= CommercePaymentEntryConstants.getPaymentLabelStyle(paymentStatus) %>"
										label="<%= LanguageUtil.get(request, CommercePaymentEntryConstants.getPaymentStatusLabel(paymentStatus)) %>"
									/>
								</span>
							</commerce-ui:info-box>

							<liferay-portlet:renderURL var="editCommercePaymentEntryNoteURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
								<portlet:param name="mvcRenderCommandName" value="/commerce_payment/edit_commerce_payment_entry_note" />
								<portlet:param name="commercePaymentEntryId" value="<%= String.valueOf(commercePaymentEntryDisplayContext.getCommercePaymentEntryId()) %>" />
							</liferay-portlet:renderURL>

							<c:if test="<%= commercePaymentEntry != null %>">
								<commerce-ui:info-box
									actionContext='<%=
										HashMapBuilder.<String, Object>put(
											"containerCssClasses", "modal-height-md"
										).put(
											"namespace", liferayPortletResponse.getNamespace()
										).put(
											"refreshOnClose", true
										).put(
											"size", "md"
										).build()
									%>'
									actionLabel='<%= commercePaymentEntryDisplayContext.hasCommercePaymentEntryModelPermission(ActionKeys.UPDATE) ? LanguageUtil.get(request, Validator.isNull(note) ? "add" : "edit") : null %>'
									actionUrl="<%= commercePaymentEntryDisplayContext.hasCommercePaymentEntryModelPermission(ActionKeys.UPDATE) ? editCommercePaymentEntryNoteURL : null %>"
									elementClasses="py-3"
									title='<%= LanguageUtil.get(request, "comment") %>'
								>
									<c:choose>
										<c:when test="<%= Validator.isNull(note) %>">
											<span class="text-muted">
												<liferay-ui:message key="click-add-to-insert" />
											</span>
										</c:when>
										<c:otherwise>
											<%= HtmlUtil.escape(note) %>
										</c:otherwise>
									</c:choose>
								</commerce-ui:info-box>
							</c:if>
						</div>
					</div>
				</commerce-ui:panel>
			</div>
		</div>

		<div class="row">
			<div class="col-6">
				<commerce-ui:panel
					bodyClasses="flex-fill"
					title='<%= LanguageUtil.get(request, "refund-amount") %>'
				>
					<div class="row">
						<div class="col-12">
							<aui:input disabled="<%= commercePaymentEntryDisplayContext.isDisabled() %>" ignoreRequestValue="<%= true %>" name="amount" required="<%= true %>" suffix="<%= commercePaymentEntryDisplayContext.getCurrencyCode() %>" type="text" value="<%= commercePaymentEntryDisplayContext.getFormattedValue(commercePaymentEntryDisplayContext.getAmount()) %>" />
						</div>
					</div>
				</commerce-ui:panel>
			</div>

			<div class="col-6">
				<commerce-ui:panel
					bodyClasses="flex-fill"
					title='<%= LanguageUtil.get(request, "refund-reason") %>'
				>
					<div class="row">
						<div class="col-12">
							<aui:select disabled="<%= commercePaymentEntryDisplayContext.isDisabled() %>" label="reason" name="reasonKey" required="<%= true %>" showEmptyOption="<%= true %>">

								<%
								for (CommercePaymentEntryRefundType commercePaymentEntryRefundType : commercePaymentEntryDisplayContext.getCommercePaymentEntryRefundTypes()) {
									String key = commercePaymentEntryRefundType.getKey();
								%>

									<aui:option label="<%= commercePaymentEntryRefundType.getName(locale) %>" selected="<%= (commercePaymentEntry != null) && key.equals(commercePaymentEntry.getReasonKey()) %>" value="<%= key %>" />

								<%
								}
								%>

							</aui:select>
						</div>
					</div>
				</commerce-ui:panel>
			</div>
		</div>

		<div class="row">
			<div class="col-12">
				<commerce-ui:panel
					bodyClasses="p-0"
					elementClasses="flex-fill"
					title='<%= LanguageUtil.get(request, "refund-history") %>'
				>
					<frontend-data-set:headless-display
						apiURL="<%= commercePaymentEntryDisplayContext.getAPIURL() %>"
						formName="fm"
						id="<%= CommercePaymentsFDSNames.REFUNDS %>"
						propsTransformer="{commercePaymentPropsTransformer} from commerce-payment-web"
						style="fluid"
					/>
				</commerce-ui:panel>
			</div>
		</div>
	</aui:form>
</div>

<liferay-frontend:component
	context='<%=
		HashMapBuilder.<String, Object>put(
			"CMD", Constants.CMD
		).put(
			"PUBLISH", Constants.PUBLISH
		).build()
	%>'
	module="{editCommercePaymentEntry} from commerce-payment-web"
/>