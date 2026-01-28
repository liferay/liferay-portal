<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ProductAnalyticsConfigurationDisplayContext productAnalyticsConfigurationDisplayContext = (ProductAnalyticsConfigurationDisplayContext)request.getAttribute(ProductAnalyticsWebKeys.PRODUCT_ANALYTICS_CONFIGURATION_DISPLAY_CONTEXT);
%>

<div class="c-mt-5 row">
	<div class="col-sm-12 form-group">
		<div class="form-group__inner">
			<clay:checkbox
				checked="<%= productAnalyticsConfigurationDisplayContext.getEnabled() %>"
				id='<%= liferayPortletResponse.getNamespace() + "enabled" %>'
				label="enabled"
				name='<%= liferayPortletResponse.getNamespace() + "enabled" %>'
			/>

			<div aria-hidden="true" class="form-feedback-group">
				<div class="form-text text-weight-normal"><liferay-ui:message key="cookie-enabled-help" /></div>
			</div>
		</div>
	</div>
</div>

<div class="row">
	<div class="col-sm-12 form-group">
		<div class="form-group__inner">
			<aui:input disabled="<%= !productAnalyticsConfigurationDisplayContext.getEnabled() %>" id='<%= liferayPortletResponse.getNamespace() + "consentRenewalPeriod" %>' label="cookie-consent-renewal-period" max="12" min="1" name='<%= liferayPortletResponse.getNamespace() + "consentRenewalPeriod" %>' required="<%= productAnalyticsConfigurationDisplayContext.getEnabled() %>" type="number" useNamespace="<%= false %>" value="<%= (productAnalyticsConfigurationDisplayContext.getConsentRenewalPeriod() == 0) ? 12 : productAnalyticsConfigurationDisplayContext.getConsentRenewalPeriod() %>" />

			<div aria-hidden="true" class="form-feedback-group">
				<div class="form-text text-weight-normal">
					<liferay-ui:message key="cookie-consent-renewal-period-help" />
				</div>
			</div>
		</div>
	</div>
</div>

<aui:input name="lastModified" type="hidden" value="<%= productAnalyticsConfigurationDisplayContext.getLastModified() %>" />

<liferay-frontend:component
	module="{ConfigurationFormEventHandler} from product-analytics-web"
/>

<aui:script>
	var form = document.<portlet:namespace />fm;

	if (form) {
		form.addEventListener('submit', (event) => {
			var consentRenewalPeriod = document.getElementById(
				'<portlet:namespace />consentRenewalPeriod'
			);

			if (!consentRenewalPeriod.value || isNaN(consentRenewalPeriod.value)) {
				event.preventDefault();
				event.stopImmediatePropagation();
				return;
			}

			var enabled = document.getElementById('<portlet:namespace />enabled');

			if (
				enabled.checked &&
				<%= productAnalyticsConfigurationDisplayContext.getEnabled() %>
			) {
				event.preventDefault();
				event.stopImmediatePropagation();

				Liferay.Util.openConfirmModal({
					message:
						'<liferay-ui:message key="you-are-about-to-change-the-consent-renewal-period" />',
					onConfirm: (isConfirmed) => {
						if (isConfirmed) {
							var lastModified = document.getElementById(
								'<portlet:namespace />lastModified'
							);

							lastModified.value = new Date().getTime();

							form.submit();
						}
					},
				});
			}
		});
	}
</aui:script>