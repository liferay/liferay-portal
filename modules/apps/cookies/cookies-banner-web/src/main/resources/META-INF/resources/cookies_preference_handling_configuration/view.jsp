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

<aui:link hashedFile="<%= true %>" href="cookies-banner-web/cookies_preference_handling_configuration/css/main.css" rel="stylesheet" type="text/css" />

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
				checked="<%= cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingExplicitConsentMode() %>"
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

<div class="row">
	<div class="col-sm-12 form-group">
		<div class="form-group__inner">
			<aui:input disabled="<%= !cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingEnabled() %>" id='<%= liferayPortletResponse.getNamespace() + "consentRenewalPeriod" %>' label="cookie-consent-renewal-period" max="12" min="1" name='<%= liferayPortletResponse.getNamespace() + "consentRenewalPeriod" %>' required="<%= cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingEnabled() %>" type="number" useNamespace="<%= false %>" value="<%= (cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingConsentRenewalPeriod() == 0) ? 12 : cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingConsentRenewalPeriod() %>" />

			<div aria-hidden="true" class="form-feedback-group">
				<div class="form-text text-weight-normal">
					<liferay-ui:message key="cookie-consent-renewal-period-help" />
				</div>
			</div>
		</div>
	</div>
</div>

<div class="row">
	<div class="col-sm-12 form-group">
		<div class="form-group__inner">
			<aui:input disabled="<%= !cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingEnabled() %>" id='<%= liferayPortletResponse.getNamespace() + "dissentRenewalPeriod" %>' label="cookie-dissent-renewal-period" max="12" min="0" name='<%= liferayPortletResponse.getNamespace() + "dissentRenewalPeriod" %>' required="<%= cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingEnabled() %>" type="number" useNamespace="<%= false %>" value="<%= cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingDissentRenewalPeriod() %>" />
		</div>
	</div>
</div>

<aui:input name="modifiedDate" type="hidden" />

<c:if test='<%= FeatureFlagManagerUtil.isEnabled("LPD-75032") %>'>
	<div class="row">
		<div class="col-sm-12 form-group">
			<div class="form-group__inner">
				<clay:checkbox
					checked="<%= cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingStoreConsent() %>"
					disabled="<%= !cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingEnabled() %>"
					id='<%= liferayPortletResponse.getNamespace() + "storeConsent" %>'
					label="cookie-store-consent"
					name='<%= liferayPortletResponse.getNamespace() + "storeConsent" %>'
				/>

				<div aria-hidden="true" class="form-feedback-group">
					<div class="form-text text-weight-normal">
						<liferay-ui:message key="cookie-store-consent-help" />
					</div>
				</div>
			</div>
		</div>
	</div>
</c:if>

<c:if test='<%= FeatureFlagManagerUtil.isEnabled("LPD-75027") %>'>
	<h3 class="sheet-subtitle"><liferay-ui:message key="floating-icon" /></h3>

	<clay:row>
		<clay:col
			cssClass="form-group"
			sm="12"
		>
			<div class="form-group__inner">
				<clay:checkbox
					checked="<%= cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingFloatingIconEnabled() %>"
					id='<%= liferayPortletResponse.getNamespace() + "floatingIconEnabled" %>'
					label="floating-icon-enabled"
					name='<%= liferayPortletResponse.getNamespace() + "floatingIconEnabled" %>'
				/>

				<div aria-hidden="true" class="form-feedback-group">
					<div class="form-text text-weight-normal"><liferay-ui:message key="floating-icon-enabled-help" /></div>
				</div>
			</div>
		</clay:col>
	</clay:row>

	<clay:row>
		<clay:col
			cssClass="form-group"
			sm="12"
		>
			<h4><liferay-ui:message key="icon" /></h4>

			<div class="align-items-center d-flex flex-wrap">

				<%
				for (String icon : new String[] {"cookie", "shield-check", "unlock", "control-panel", "custom"}) {
				%>

					<div class="align-items-center d-flex mb-3 mr-4">
						<aui:input checked="<%= Objects.equals(cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingFloatingIcon(), icon) %>" id="<%= icon %>" label="" name="floatingIcon" type="radio" value="<%= icon %>" wrapperCssClass="mb-0" />

						<c:choose>
							<c:when test='<%= Objects.equals("custom", icon) %>'>
								<label class="align-items-center cursor-pointer d-inline-flex justify-content-center mb-0 ml-3" for="<portlet:namespace /><%= icon %>">
									<liferay-ui:message key="custom" />
								</label>
							</c:when>
							<c:otherwise>
								<label class="align-items-center cursor-pointer d-inline-flex floating-icon-custom justify-content-center mb-0 ml-3 rounded-circle text-white" for="<portlet:namespace /><%= icon %>">
									<clay:icon
										symbol="<%= icon %>"
									/>
								</label>
							</c:otherwise>
						</c:choose>
					</div>

				<%
				}
				%>

			</div>

			<div id="<portlet:namespace />logoSelectorContainer">

				<%
				long customFloatingIconImageId = cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingCustomFloatingIconImageId();
				%>

				<liferay-frontend:logo-selector
					aspectRatio="<%= 1 %>"
					currentLogoURL='<%= (customFloatingIconImageId == 0) ? themeDisplay.getPathThemeImages() + "/spacer.png" : themeDisplay.getPathImage() + "/floating_icon?img_id=" + customFloatingIconImageId %>'
					defaultLogoURL='<%= themeDisplay.getPathThemeImages() + "/spacer.png" %>'
					label='<%= LanguageUtil.get(request, "custom-icon") %>'
					type="floating_icon"
				/>
			</div>

			<div aria-hidden="true" class="form-feedback-group mt-2">
				<div class="form-text text-weight-normal"><liferay-ui:message key="floating-icon-help" /></div>
			</div>
		</clay:col>
	</clay:row>
</c:if>

<liferay-frontend:component
	module="{ConfigurationFormEventHandler} from cookies-banner-web"
/>

<aui:script>
	var logoSelectorContainer = document.getElementById(
		'<portlet:namespace />logoSelectorContainer'
	);
	var customRadioButton = document.getElementById('<portlet:namespace />custom');
	var allRadioButtons = document.querySelectorAll(
		'input[name="<portlet:namespace />floatingIcon"]'
	);

	function toggleLogoSelector() {
		if (customRadioButton.checked) {
			logoSelectorContainer.classList.remove('d-none');
		}
		else {
			logoSelectorContainer.classList.add('d-none');
		}
	}

	toggleLogoSelector();

	allRadioButtons.forEach(function (radio) {
		radio.addEventListener('change', toggleLogoSelector);
	});

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

			var dissentRenewalPeriod = document.getElementById(
				'<portlet:namespace />dissentRenewalPeriod'
			);

			if (!dissentRenewalPeriod.value || isNaN(dissentRenewalPeriod.value)) {
				event.preventDefault();
				event.stopImmediatePropagation();
				return;
			}

			var enabled = document.getElementById('<portlet:namespace />enabled');

			if (
				(consentRenewalPeriod.value !==
					'<%= cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingConsentRenewalPeriod() %>' ||
					dissentRenewalPeriod.value !==
						'<%= cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingDissentRenewalPeriod() %>') &&
				enabled.checked &&
				<%= cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingEnabled() %>
			) {
				event.preventDefault();
				event.stopImmediatePropagation();

				Liferay.Util.openConfirmModal({
					message:
						'<liferay-ui:message key="you-are-about-to-change-the-consent-renewal-period" />',
					onConfirm: (isConfirmed) => {
						if (isConfirmed) {
							var modifiedDate = document.getElementById(
								'<portlet:namespace />modifiedDate'
							);

							modifiedDate.value = new Date().getTime();

							form.submit();
						}
					},
				});
			}
		});
	}
</aui:script>