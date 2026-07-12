<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/returns/init.jsp" %>

<%
CommerceReturnContentDisplayContext commerceReturnContentDisplayContext = (CommerceReturnContentDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CommerceReturn commerceReturn = commerceReturnContentDisplayContext.getCommerceReturn();

String note = StringPool.BLANK;

if (commerceReturn != null) {
	note = commerceReturn.getNote();
}

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(String.valueOf(renderResponse.createRenderURL()));
%>

<commerce-ui:header
	actions="<%= commerceReturnContentDisplayContext.getHeaderActionModels() %>"
	additionalStatusLabel="<%= commerceReturn.getReturnStatus() %>"
	additionalStatusLabelStyle="<%= CommerceReturnConstants.getReturnStatusLabelStyle(commerceReturn.getReturnStatus()) %>"
	externalReferenceCode="<%= (commerceReturn == null) ? StringPool.BLANK : commerceReturn.getExternalReferenceCode() %>"
	model="<%= ObjectEntry.class %>"
	title="<%= (commerceReturn == null) ? StringPool.BLANK : String.valueOf(commerceReturn.getId()) %>"
/>

<div class="container mt-4">
	<aui:form cssClass="pt-4" method="post" name="fm">
		<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= (commerceReturn == null) ? Constants.ADD : Constants.UPDATE %>" />
		<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
		<aui:input name="commerceReturnId" type="hidden" value="<%= commerceReturnContentDisplayContext.getCommerceReturnItemId() %>" />

		<c:choose>
			<c:when test="<%= (commerceReturn != null) && (commerceReturn.getRequestedItems() <= 0) %>">
				<clay:alert
					displayType="info"
					message="please-add-at-least-one-item-you-wish-to-return-from-the-order-to-proceed-with-the-request"
				/>
			</c:when>
			<c:otherwise>
				<c:if test='<%= Objects.equals(commerceReturn.getReturnStatus(), "draft") %>'>
					<clay:alert
						dismissible="<%= true %>"
						displayType="warning"
						message="please-review-the-details-of-the-returning-items-before-submitting-the-request"
					/>
				</c:if>
			</c:otherwise>
		</c:choose>

		<div class="row">
			<div class="col-12">
				<commerce-ui:panel
					bodyClasses="flex-fill"
					title='<%= LanguageUtil.get(request, "details") %>'
				>
					<div class="row vertically-divided">
						<div class="col-4">
							<commerce-ui:info-box
								elementClasses="py-3"
								title='<%= LanguageUtil.get(request, "account-info") %>'
							>
								<p class="mb-0"><%= HtmlUtil.escape(commerceReturnContentDisplayContext.getAccountEntryName()) %></p>
								<p class="mb-0">#<%= (commerceReturn == null) ? StringPool.BLANK : String.valueOf(commerceReturn.getAccountId()) %></p>
							</commerce-ui:info-box>

							<commerce-ui:info-box
								elementClasses="py-3"
								title='<%= LanguageUtil.get(request, "shipping-address") %>'
							>
								<p class="mb-0"><%= HtmlUtil.escape(commerceReturnContentDisplayContext.getShippingAddress()) %></p>
							</commerce-ui:info-box>
						</div>

						<div class="col-4">
							<commerce-ui:info-box
								elementClasses="py-3"
								title='<%= LanguageUtil.get(request, "order-id") %>'
							>
								<p class="mb-0">#<%= (commerceReturn == null) ? StringPool.BLANK : String.valueOf(commerceReturn.getOrderId()) %></p>
							</commerce-ui:info-box>

							<commerce-ui:info-box
								elementClasses="py-3"
								title='<%= LanguageUtil.get(request, "order-date") %>'
							>
								<p class="mb-0"><%= commerceReturnContentDisplayContext.getOrderDate() %></p>
							</commerce-ui:info-box>

							<commerce-ui:info-box
								elementClasses="py-3"
								title='<%= LanguageUtil.get(request, "payment-method") %>'
							>
								<p class="mb-0"><%= HtmlUtil.escape(commerceReturnContentDisplayContext.getPaymentMethod()) %></p>
							</commerce-ui:info-box>
						</div>

						<div class="col-4">
							<liferay-portlet:renderURL var="editCommerceReturnNoteURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
								<portlet:param name="mvcRenderCommandName" value="/commerce_return_content/edit_commerce_return_note" />
								<portlet:param name="redirect" value="<%= currentURL %>" />
								<portlet:param name="commerceReturnId" value="<%= String.valueOf(commerceReturnContentDisplayContext.getCommerceReturnId()) %>" />
							</liferay-portlet:renderURL>

							<c:if test="<%= commerceReturn != null %>">
								<commerce-ui:info-box
									actionContext='<%=
										HashMapBuilder.<String, Object>put(
											"namespace", liferayPortletResponse.getNamespace()
										).put(
											"refreshOnClose", true
										).put(
											"size", "lg"
										).build()
									%>'
									actionLabel='<%= Objects.equals(commerceReturn.getReturnStatus(), "draft") ? LanguageUtil.get(request, Validator.isNull(note) ? "add" : "edit") : null %>'
									actionURL='<%= Objects.equals(commerceReturn.getReturnStatus(), "draft") ? editCommerceReturnNoteURL : null %>'
									elementClasses="py-3"
									title='<%= LanguageUtil.get(request, "note") %>'
								>
									<c:choose>
										<c:when test="<%= Validator.isNull(note) %>">
											<c:if test='<%= Objects.equals(commerceReturn.getReturnStatus(), "draft") %>'>
												<span class="text-secondary">
													<liferay-ui:message key="click-add-to-insert" />
												</span>
											</c:if>
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
			<div class="col-12">
				<commerce-ui:panel
					bodyClasses="flex-fill"
					title='<%= LanguageUtil.get(request, "items") %>'
				>
					<div id="<portlet:namespace />return-items-container">
						<frontend-data-set:headless-display
							apiURL="<%= commerceReturnContentDisplayContext.getReturnItemsAPIURL() %>"
							creationMenu="<%= commerceReturnContentDisplayContext.getCommerceReturnItemCreationMenu() %>"
							fdsActionDropdownItems="<%= commerceReturnContentDisplayContext.getCommerceReturnItemFDSActionDropdownItems() %>"
							formName="fm"
							id="<%= ((commerceReturn == null) || Objects.equals(commerceReturn.getReturnStatus(), CommerceReturnConstants.RETURN_STATUS_DRAFT)) ? CommerceOrderFDSNames.DRAFT_RETURN_ITEMS : CommerceOrderFDSNames.RETURN_ITEMS %>"
							propsTransformer="{commerceReturnItemsPropsTransformer} from commerce-order-content-web"
							style="stacked"
						/>

						<liferay-frontend:component
							context='<%=
								HashMapBuilder.<String, Object>put(
									"namespace", liferayPortletResponse.getNamespace()
								).put(
									"returnableOrderItemsContextParams", commerceReturnContentDisplayContext.getReturnableOrderItemsContextParams()
								).put(
									"viewReturnableOrderItemsURL", commerceReturnContentDisplayContext.getViewReturnableOrderItemsURL()
								).build()
							%>'
							module="{viewCommerceOrderDetailsCTAs} from commerce-order-content-web"
						/>
					</div>
				</commerce-ui:panel>
			</div>
		</div>

		<div class="row">
			<div class="col-12">
				<commerce-ui:panel
					bodyClasses="flex-fill"
					title='<%= LanguageUtil.get(request, "refund-summary") %>'
				>
					<div class="row summary-table text-right">
						<div class="col-6 col-md-9">
							<span class="summary-table-item"><liferay-ui:message key="units-returned" /></span>
						</div>

						<div class="col-6 col-md-3">
							<span class="summary-table-item"><%= commerceReturnContentDisplayContext.getUnitsReturned() %></span>
						</div>
					</div>

					<div class="row summary-table text-right">
						<div class="col-6 col-md-9">
							<span class="summary-table-item"><liferay-ui:message key="refund-subtotal" /></span>
						</div>

						<div class="col-6 col-md-3">
							<span class="summary-table-item"><%= commerceReturnContentDisplayContext.getRefundSubtotal() %></span>
						</div>
					</div>

					<div class="row summary-table text-right">
						<div class="col-12">
							<hr />
						</div>
					</div>

					<div class="row summary-table text-right">
						<div class="col-6 col-md-9">
							<div class="h4 my-2 summary-table-item-big"><liferay-ui:message key="total-estimated-refund" /></div>
						</div>

						<div class="col-6 col-md-3">
							<div class="h4 my-2 summary-table-item-big"><%= commerceReturnContentDisplayContext.getTotalEstimatedRefund() %></div>
						</div>
					</div>
				</commerce-ui:panel>
			</div>
		</div>
	</aui:form>
</div>

<liferay-frontend:component
	context='<%=
		HashMapBuilder.<String, Object>put(
			"commerceReturnId", commerceReturnContentDisplayContext.getCommerceReturnId()
		).put(
			"redirectURL", currentURL
		).put(
			"returnStatus", CommerceReturnConstants.RETURN_STATUS_PENDING
		).build()
	%>'
	module="{editCommerceReturn} from commerce-order-content-web"
/>