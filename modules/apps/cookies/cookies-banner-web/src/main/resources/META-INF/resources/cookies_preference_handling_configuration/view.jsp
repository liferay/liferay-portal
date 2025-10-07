<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CookiesPreferenceHandlingConfigurationDisplayContext cookiesPreferenceHandlingConfigurationDisplayContext = (CookiesPreferenceHandlingConfigurationDisplayContext)request.getAttribute(CookiesBannerWebKeys.COOKIES_PREFERENCE_HANDLING_CONFIGURATION_DISPLAY_CONTEXT);
%>

<div class="c-mt-5 row">
	<div class="col-sm-12 form-group">
		<div class="form-group__inner">
			<clay:checkbox
				checked="<%= cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingEnabled() %>"
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
			<clay:checkbox
				checked="<%= !cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingEnabled() || cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingExplicitConsentMode() %>"
				disabled="<%= !cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingEnabled() %>"
				id='<%= liferayPortletResponse.getNamespace() + "explicitConsentMode" %>'
				label="cookie-explicit-consent-mode"
				name='<%= liferayPortletResponse.getNamespace() + "explicitConsentMode" %>'
			/>

			<div aria-hidden="true" class="form-feedback-group">
				<div class="form-text text-weight-normal">
					<liferay-ui:message key="cookie-explicit-consent-mode-help" />
				</div>
			</div>
		</div>
	</div>
</div>

<liferay-frontend:component
	module="{ConfigurationFormEventHandler} from cookies-banner-web"
/>