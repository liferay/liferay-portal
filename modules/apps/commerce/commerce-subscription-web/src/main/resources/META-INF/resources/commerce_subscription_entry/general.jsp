<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommerceSubscriptionEntryDisplayContext commerceSubscriptionEntryDisplayContext = (CommerceSubscriptionEntryDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CommerceSubscriptionEntry commerceSubscriptionEntry = commerceSubscriptionEntryDisplayContext.getCommerceSubscriptionEntry();
List<CPSubscriptionType> cpSubscriptionTypes = commerceSubscriptionEntryDisplayContext.getCPSubscriptionTypes();
int orderPaymentStatus = commerceSubscriptionEntryDisplayContext.getOrderPaymentStatus();

Map<String, String> contextParams = HashMapBuilder.<String, String>put(
	"commerceSubscriptionEntryId", String.valueOf(commerceSubscriptionEntry.getCommerceSubscriptionEntryId())
).build();

int subscriptionLength = BeanParamUtil.getInteger(commerceSubscriptionEntry, request, "subscriptionLength", 1);
String subscriptionType = BeanParamUtil.getString(commerceSubscriptionEntry, request, "subscriptionType");
long maxSubscriptionCycles = BeanParamUtil.getLong(commerceSubscriptionEntry, request, "maxSubscriptionCycles");
int deliverySubscriptionLength = BeanParamUtil.getInteger(commerceSubscriptionEntry, request, "deliverySubscriptionLength", 1);
String deliverySubscriptionType = BeanParamUtil.getString(commerceSubscriptionEntry, request, "deliverySubscriptionType");
long deliveryMaxSubscriptionCycles = BeanParamUtil.getLong(commerceSubscriptionEntry, request, "deliveryMaxSubscriptionCycles");

String defaultCPSubscriptionTypeLabel = StringPool.BLANK;
String defaultDeliveryCPSubscriptionTypeLabel = StringPool.BLANK;

CPSubscriptionType cpSubscriptionType = commerceSubscriptionEntryDisplayContext.getCPSubscriptionType(subscriptionType);
CPSubscriptionType deliveryCPSubscriptionType = commerceSubscriptionEntryDisplayContext.getCPSubscriptionType(deliverySubscriptionType);

if (cpSubscriptionType != null) {
	defaultCPSubscriptionTypeLabel = cpSubscriptionType.getLabel(locale);
}

if (deliveryCPSubscriptionType != null) {
	defaultDeliveryCPSubscriptionTypeLabel = deliveryCPSubscriptionType.getLabel(locale);
}

CPSubscriptionTypeJSPContributor cpSubscriptionTypeJSPContributor = commerceSubscriptionEntryDisplayContext.getCPSubscriptionTypeJSPContributor(subscriptionType);
CPSubscriptionTypeJSPContributor deliveryCPSubscriptionTypeJSPContributor = commerceSubscriptionEntryDisplayContext.getCPSubscriptionTypeJSPContributor(deliverySubscriptionType);

boolean ending = false;

if (maxSubscriptionCycles > 0) {
	ending = true;
}

boolean deliveryEnding = false;

if (deliveryMaxSubscriptionCycles > 0) {
	deliveryEnding = true;
}
%>

<liferay-ui:error exception="<%= CommerceSubscriptionEntryNextIterationDateException.class %>" message="please-enter-a-valid-date-for-a-non-inactive-subscription" />
<liferay-ui:error exception="<%= CommerceSubscriptionEntrySubscriptionStatusException.class %>" message="please-check-subscription-status" />

<commerce-ui:panel
	elementClasses="flex-fill"
	title='<%= LanguageUtil.get(request, "reference-order") %>'
>

<div class="row">
	<div class="col-3">
		<commerce-ui:info-box
			elementClasses="py-3"
			title='<%= LanguageUtil.get(request, "order-id") %>'
		>
			<span>
				<a href="<%= commerceSubscriptionEntryDisplayContext.getEditCommerceOrderURL(0) %>">
					<%= commerceSubscriptionEntryDisplayContext.getCommerceOrderId() %>
				</a>
			</span>
		</commerce-ui:info-box>
	</div>

	<div class="col-3">
		<commerce-ui:info-box
			elementClasses="py-3"
			title='<%= LanguageUtil.get(request, "payment-method") %>'
		>
			<img url="<%= HtmlUtil.escapeAttribute(commerceSubscriptionEntryDisplayContext.getOrderPaymentMethodImage()) %>" />

			<span><%= HtmlUtil.escape(commerceSubscriptionEntryDisplayContext.getOrderPaymentMethodName()) %></span>
		</commerce-ui:info-box>
	</div>

	<div class="col-3">
		<commerce-ui:info-box
			elementClasses="py-3"
			title='<%= LanguageUtil.get(request, "payment-status") %>'
		>
			<clay:label
				displayType="<%= CommerceOrderPaymentConstants.getOrderPaymentLabelStyle(orderPaymentStatus) %>"
				label="<%= LanguageUtil.get(request, CommerceOrderPaymentConstants.getOrderPaymentStatusLabel(orderPaymentStatus)) %>"
			/>
		</commerce-ui:info-box>
	</div>

	<div class="col-3">
		<commerce-ui:info-box
			elementClasses="py-3"
			title='<%= LanguageUtil.get(request, "start-date") %>'
		>
			<span><%= commerceSubscriptionEntryDisplayContext.getCommerceSubscriptionEntryStartDate() %></span>
		</commerce-ui:info-box>
	</div>
</div>
</commerce-ui:panel>

<portlet:actionURL name="/commerce_subscription_entry/edit_commerce_subscription_entry" var="editCommerceSubscriptionEntryActionURL" />

<aui:form action="<%= editCommerceSubscriptionEntryActionURL %>" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
	<aui:input name="commerceSubscriptionEntryId" type="hidden" value="<%= commerceSubscriptionEntryDisplayContext.getCommerceSubscriptionEntryId() %>" />

	<commerce-ui:panel
		title='<%= LanguageUtil.get(request, "payment-subscription") %>'
	>
		<commerce-ui:info-box
			elementClasses="py-3"
			title='<%= LanguageUtil.get(request, "info") %>'
		>
			<div class="row">
				<div class="col-6">
					<aui:select label="status" name="subscriptionStatus">

						<%
						for (int curSubscriptionStatus : CommerceSubscriptionEntryConstants.SUBSCRIPTION_STATUSES) {
						%>

							<aui:option data-label="<%= CommerceSubscriptionEntryConstants.getSubscriptionStatusLabel(curSubscriptionStatus) %>" label="<%= CommerceSubscriptionEntryConstants.getSubscriptionStatusLabel(curSubscriptionStatus) %>" selected="<%= commerceSubscriptionEntry.getSubscriptionStatus() == curSubscriptionStatus %>" value="<%= curSubscriptionStatus %>" />

						<%
						}
						%>

					</aui:select>

					<div class="never-ends-header">
						<aui:input checked="<%= ending ? false : true %>" name="neverEnds" onClick='<%= liferayPortletResponse.getNamespace() + "neverEndsToggle();" %>' type="toggle-switch" />
					</div>

					<div class="never-ends-content <%= ending ? StringPool.BLANK : "hide" %>">
						<aui:input helpMessage="max-subscription-cycles-help" label="end-after" name="maxSubscriptionCycles" suffix='<%= LanguageUtil.get(request, "cycles") %>' value="<%= String.valueOf(maxSubscriptionCycles) %>">
							<aui:validator name="digits" />

							<aui:validator errorMessage='<%= LanguageUtil.format(request, "please-enter-a-value-greater-than-or-equal-to-x", 1) %>' name="custom">
								function(val) {
									var subscriptionNeverEndsCheckbox = window.document.querySelector('#<portlet:namespace />neverEnds');

									if (subscriptionNeverEndsCheckbox && subscriptionNeverEndsCheckbox.checked) {
										return true;
									}

									if (parseInt(val, 10) > 0) {
										return true;
									}

									return false;
								}
							</aui:validator>
						</aui:input>
					</div>
				</div>

				<div class="col-6">
					<label for="<portlet:namespace />nextIterationDate"><liferay-ui:message key="next-iteration-date" /></label>

					<%
					Date nextIterationDate = commerceSubscriptionEntry.getNextIterationDate();

					int nextIterationDateDay = 0;
					int nextIterationDateMonth = -1;
					int nextIterationDateYear = 0;

					if (nextIterationDate != null) {
						Calendar calendar = CalendarFactoryUtil.getCalendar(nextIterationDate.getTime());

						nextIterationDateDay = calendar.get(Calendar.DAY_OF_MONTH);
						nextIterationDateMonth = calendar.get(Calendar.MONTH);
						nextIterationDateYear = calendar.get(Calendar.YEAR);
					}
					%>

					<liferay-ui:input-date
						dayParam="nextIterationDateDay"
						dayValue="<%= nextIterationDateDay %>"
						disabled="<%= false %>"
						monthParam="nextIterationDateMonth"
						monthValue="<%= nextIterationDateMonth %>"
						name="nextIterationDate"
						nullable="<%= true %>"
						showDisableCheckbox="<%= false %>"
						yearParam="nextIterationDateYear"
						yearValue="<%= nextIterationDateYear %>"
					/>
				</div>
			</div>
		</commerce-ui:info-box>

		<commerce-ui:info-box
			elementClasses="py-3"
			title='<%= LanguageUtil.get(request, "payment") %>'
		>
			<div class="row">
				<div class="col-6">
					<aui:select name="subscriptionType" onChange='<%= liferayPortletResponse.getNamespace() + "selectSubscriptionType();" %>' showEmptyOption="<%= true %>">

						<%
						for (CPSubscriptionType curCPSubscriptionType : cpSubscriptionTypes) {
						%>

							<aui:option data-label="<%= curCPSubscriptionType.getLabel(locale) %>" label="<%= curCPSubscriptionType.getLabel(locale) %>" selected="<%= subscriptionType.equals(curCPSubscriptionType.getName()) %>" value="<%= curCPSubscriptionType.getName() %>" />

						<%
						}
						%>

					</aui:select>

					<%
					if (cpSubscriptionTypeJSPContributor != null) {
						cpSubscriptionTypeJSPContributor.render(commerceSubscriptionEntry, request, PipingServletResponseFactory.createPipingServletResponse(pageContext));
					}
					%>

				</div>

				<div class="col-6">
					<div id="<portlet:namespace />cycleLengthContainer">
						<aui:input name="subscriptionLength" suffix="<%= defaultCPSubscriptionTypeLabel %>" value="<%= String.valueOf(subscriptionLength) %>">
							<aui:validator name="digits" />
							<aui:validator name="min">1</aui:validator>
						</aui:input>
					</div>
				</div>
			</div>
		</commerce-ui:info-box>
	</commerce-ui:panel>

	<commerce-ui:panel
		title='<%= LanguageUtil.get(request, "delivery-subscription") %>'
	>
		<commerce-ui:info-box
			elementClasses="py-3"
			title='<%= LanguageUtil.get(request, "info") %>'
		>
			<div class="row">
				<div class="col-6">
					<aui:select label="status" name="deliverySubscriptionStatus">

						<%
						for (int curSubscriptionStatus : CommerceSubscriptionEntryConstants.SUBSCRIPTION_STATUSES) {
						%>

							<aui:option data-label="<%= CommerceSubscriptionEntryConstants.getSubscriptionStatusLabel(curSubscriptionStatus) %>" label="<%= CommerceSubscriptionEntryConstants.getSubscriptionStatusLabel(curSubscriptionStatus) %>" selected="<%= commerceSubscriptionEntry.getDeliverySubscriptionStatus() == curSubscriptionStatus %>" value="<%= curSubscriptionStatus %>" />

						<%
						}
						%>

					</aui:select>

					<div class="delivery-never-ends-header">
						<aui:input checked="<%= deliveryEnding ? false : true %>" label="never-ends" name="deliveryNeverEnds" onClick='<%= liferayPortletResponse.getNamespace() + "deliveryNeverEndsToggle();" %>' type="toggle-switch" />
					</div>

					<div class="never-ends-content <%= ending ? StringPool.BLANK : "hide" %>">
						<aui:input helpMessage="max-subscription-cycles-help" label="end-after" name="deliveryMaxSubscriptionCycles" suffix='<%= LanguageUtil.get(request, "cycles") %>' value="<%= String.valueOf(deliveryMaxSubscriptionCycles) %>">
							<aui:validator name="digits" />

							<aui:validator errorMessage='<%= LanguageUtil.format(request, "please-enter-a-value-greater-than-or-equal-to-x", 1) %>' name="custom">
								function(val) {
									var deliveryNeverEndsCheckbox = window.document.querySelector('#<portlet:namespace />deliveryNeverEnds');

									if (deliveryNeverEndsCheckbox && deliveryNeverEndsCheckbox.checked) {
										return true;
									}

									if (parseInt(val, 10) > 0) {
										return true;
									}

									return false;
								}
							</aui:validator>
						</aui:input>
					</div>
				</div>

				<div class="col-6">
					<label for="<portlet:namespace />deliveryNextIterationDate"><liferay-ui:message key="next-iteration-date" /></label>

					<%
					Date deliveryNextIterationDate = commerceSubscriptionEntry.getDeliveryNextIterationDate();

					int deliveryNextIterationDateDay = 0;
					int deliveryNextIterationDateMonth = -1;
					int deliveryNextIterationDateYear = 0;

					if (deliveryNextIterationDate != null) {
						Calendar calendar = CalendarFactoryUtil.getCalendar(deliveryNextIterationDate.getTime());

						deliveryNextIterationDateDay = calendar.get(Calendar.DAY_OF_MONTH);
						deliveryNextIterationDateMonth = calendar.get(Calendar.MONTH);
						deliveryNextIterationDateYear = calendar.get(Calendar.YEAR);
					}
					%>

					<liferay-ui:input-date
						dayParam="deliveryNextIterationDateDay"
						dayValue="<%= deliveryNextIterationDateDay %>"
						disabled="<%= false %>"
						monthParam="deliveryNextIterationDateMonth"
						monthValue="<%= deliveryNextIterationDateMonth %>"
						name="deliveryNextIterationDate"
						nullable="<%= true %>"
						showDisableCheckbox="<%= false %>"
						yearParam="deliveryNextIterationDateYear"
						yearValue="<%= deliveryNextIterationDateYear %>"
					/>
				</div>
			</div>
		</commerce-ui:info-box>

		<commerce-ui:info-box
			elementClasses="py-3"
			title='<%= LanguageUtil.get(request, "delivery") %>'
		>
			<div class="row">
				<div class="col-6">
					<aui:select label="subscription-type" name="deliverySubscriptionType" onChange='<%= liferayPortletResponse.getNamespace() + "selectDeliverySubscriptionType();" %>' showEmptyOption="<%= true %>">

						<%
						for (CPSubscriptionType curCPSubscriptionType : cpSubscriptionTypes) {
						%>

							<aui:option data-label="<%= curCPSubscriptionType.getLabel(locale) %>" label="<%= curCPSubscriptionType.getLabel(locale) %>" selected="<%= deliverySubscriptionType.equals(curCPSubscriptionType.getName()) %>" value="<%= curCPSubscriptionType.getName() %>" />

						<%
						}
						%>

					</aui:select>

					<%
					if (deliveryCPSubscriptionTypeJSPContributor != null) {
						deliveryCPSubscriptionTypeJSPContributor.render(commerceSubscriptionEntry, request, PipingServletResponseFactory.createPipingServletResponse(pageContext), false);
					}
					%>

				</div>

				<div class="col-6">
					<div id="<portlet:namespace />deliveryCycleLengthContainer">
						<aui:input name="deliverySubscriptionLength" suffix="<%= defaultDeliveryCPSubscriptionTypeLabel %>" value="<%= String.valueOf(deliverySubscriptionLength) %>">
							<aui:validator name="digits" />
							<aui:validator name="min">0</aui:validator>
						</aui:input>
					</div>
				</div>
			</div>
		</commerce-ui:info-box>
	</commerce-ui:panel>
</aui:form>

<commerce-ui:panel
	bodyClasses="p-0"
	title='<%= LanguageUtil.get(request, "items") %>'
>
<frontend-data-set:classic-display
	contextParams="<%= contextParams %>"
	dataProviderKey="<%= CommerceSubscriptionFDSNames.SUBSCRIPTION_ORDER_ITEMS %>"
	id="<%= CommerceSubscriptionFDSNames.SUBSCRIPTION_ORDER_ITEMS %>"
	style="stacked"
/>
</commerce-ui:panel>

<aui:script sandbox="<%= true %>">
	Liferay.provide(window, '<portlet:namespace />selectSubscriptionType', () => {
		const portletURL = Liferay.Util.PortletURL.createPortletURL(
			'<%= currentURLObj %>',
			{
				subscriptionLength: document.getElementById(
					'<portlet:namespace />subscriptionLength'
				).value,
				subscriptionType: document.getElementById(
					'<portlet:namespace />subscriptionType'
				).value,
				maxSubscriptionCycles: document.getElementById(
					'<portlet:namespace />maxSubscriptionCycles'
				).value,
			}
		);

		window.location.replace(portletURL.toString());
	});

	Liferay.provide(
		window,
		'<portlet:namespace />selectDeliverySubscriptionType',
		() => {
			const portletURL = Liferay.Util.PortletURL.createPortletURL(
				'<%= currentURLObj %>',
				{
					deliverySubscriptionLength: document.getElementById(
						'<portlet:namespace />deliverySubscriptionLength'
					).value,
					deliverySubscriptionType: document.getElementById(
						'<portlet:namespace />deliverySubscriptionType'
					).value,
					deliveryMaxSubscriptionCycles: document.getElementById(
						'<portlet:namespace />deliveryMaxSubscriptionCycles'
					).value,
				}
			);

			window.location.replace(portletURL.toString());
		}
	);
</aui:script>

<aui:script use="liferay-form">
	document
		.getElementById('<portlet:namespace />neverEnds')
		.addEventListener('change', (event) => {
			const formValidator = Liferay.Form.get(
				'<portlet:namespace />fm'
			).formValidator;

			formValidator.validateField(
				'<portlet:namespace />maxSubscriptionCycles'
			);
		});

	document
		.getElementById('<portlet:namespace />deliveryNeverEnds')
		.addEventListener('change', (event) => {
			const formValidator = Liferay.Form.get(
				'<portlet:namespace />fm'
			).formValidator;

			formValidator.validateField(
				'<portlet:namespace />deliveryMaxSubscriptionCycles'
			);
		});
</aui:script>

<aui:script>
	function <portlet:namespace />neverEndsToggle() {
		document.querySelector('.never-ends-content').classList.toggle('hide');
	}

	function <portlet:namespace />deliveryNeverEndsToggle() {
		document
			.querySelector(
				'#<portlet:namespace />deliveryNeverEndsContainer .never-ends-content'
			)
			.classList.toggle('hide');
	}
</aui:script>