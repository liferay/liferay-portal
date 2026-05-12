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
		<label class="c-mb-1 c-mt-2 font-weight-semi-bold <%= !cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingEnabled() ? "disabled" : "" %>" for="<portlet:namespace />consentRenewalPeriod" id="<portlet:namespace />consentRenewalPeriodLabel">
			<liferay-ui:message key="cookie-consent-renewal-period" />
		</label>

		<div class="form-group-autofit">
			<div class="form-group-item">
				<aui:input disabled="<%= !cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingEnabled() %>" id='<%= liferayPortletResponse.getNamespace() + "consentRenewalPeriod" %>' label="" max="12" min="1" name='<%= liferayPortletResponse.getNamespace() + "consentRenewalPeriod" %>' required="<%= cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingEnabled() %>" type="number" useNamespace="<%= false %>" value="<%= cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingConsentRenewalPeriod() %>" />
			</div>

			<div class="form-group-item">
				<aui:select disabled="<%= !cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingEnabled() %>" id='<%= liferayPortletResponse.getNamespace() + "consentRenewalPeriodTimeUnit" %>' label="" name='<%= liferayPortletResponse.getNamespace() + "consentRenewalPeriodTimeUnit" %>' title="time-unit" useNamespace="<%= false %>" value="<%= cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingConsentRenewalPeriodTimeUnit() %>">
					<aui:option label="days" value="days" />
					<aui:option label="weeks" value="weeks" />
					<aui:option label="months" selected="<%= true %>" value="months" />
				</aui:select>
			</div>
		</div>

		<div aria-hidden="true" class="c-mb-1 form-feedback-group">
			<div class="form-text text-weight-normal">
				<liferay-ui:message key="cookie-consent-renewal-period-help" />
			</div>
		</div>
	</div>
</div>

<div class="row">
	<div class="col-sm-12 form-group">
		<label class="c-mb-1 c-mt-2 font-weight-semi-bold <%= !cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingEnabled() ? "disabled" : "" %>" for="<portlet:namespace />dissentRenewalPeriod" id="<portlet:namespace />dissentRenewalPeriodLabel">
			<liferay-ui:message key="cookie-dissent-renewal-period" />
		</label>

		<div class="form-group-autofit">
			<div class="form-group-item">
				<aui:input disabled="<%= !cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingEnabled() %>" id='<%= liferayPortletResponse.getNamespace() + "dissentRenewalPeriod" %>' label="" max="12" min="0" name='<%= liferayPortletResponse.getNamespace() + "dissentRenewalPeriod" %>' required="<%= cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingEnabled() %>" type="number" useNamespace="<%= false %>" value="<%= cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingDissentRenewalPeriod() %>" />
			</div>

			<div class="form-group-item">
				<aui:select disabled="<%= !cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingEnabled() %>" id='<%= liferayPortletResponse.getNamespace() + "dissentRenewalPeriodTimeUnit" %>' label="" name='<%= liferayPortletResponse.getNamespace() + "dissentRenewalPeriodTimeUnit" %>' title="time-unit" useNamespace="<%= false %>" value="<%= cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingDissentRenewalPeriodTimeUnit() %>">
					<aui:option label="days" value="days" />
					<aui:option label="weeks" value="weeks" />
					<aui:option label="months" selected="<%= true %>" value="months" />
				</aui:select>
			</div>
		</div>

		<div aria-hidden="true" class="c-mb-1 form-feedback-group">
			<div class="form-text text-weight-normal">
				<liferay-ui:message key="cookie-dissent-renewal-period-help" />
			</div>
		</div>
	</div>
</div>

<div class="row">
	<div class="col-sm-12 form-group">
		<button class="btn btn-secondary <%= !cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingEnabled() ? "disabled" : "" %>" id="<portlet:namespace />forcedReconsentButton" type="button">
			<liferay-ui:message key="forced-reconsent" />
		</button>

		<div aria-hidden="true" class="c-mb-1 form-feedback-group">
			<div class="form-text text-weight-normal">
				<liferay-ui:message key="forced-reconsent-help" />
			</div>
		</div>
	</div>
</div>

<aui:input name="modifiedDate" type="hidden" />

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

<clay:row>
	<clay:col
		cssClass="form-group"
		sm="12"
	>
		<div class="form-group__inner">
			<clay:checkbox
				checked="<%= cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingGlobalPrivacyControlEnabled() %>"
				disabled="<%= !cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingEnabled() %>"
				id='<%= liferayPortletResponse.getNamespace() + "globalPrivacyControlEnabled" %>'
				label="global-privacy-control-enabled"
				name='<%= liferayPortletResponse.getNamespace() + "globalPrivacyControlEnabled" %>'
			/>

			<div aria-hidden="true" class="form-feedback-group">
				<div class="form-text text-weight-normal"><liferay-ui:message key="global-privacy-control-enabled-help" /></div>
			</div>
		</div>
	</clay:col>
</clay:row>

<h3 class="sheet-subtitle"><liferay-ui:message key="floating-icon" /></h3>

<clay:row>
	<clay:col
		cssClass="form-group"
		sm="12"
	>
		<div class="form-group__inner">
			<clay:checkbox
				checked="<%= cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingFloatingIconEnabled() %>"
				disabled="<%= !cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingEnabled() %>"
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
					<aui:input checked="<%= Objects.equals(cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingFloatingIcon(), icon) %>" disabled="<%= !cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingEnabled() %>" id="<%= icon %>" label="" name="floatingIcon" type="radio" value="<%= icon %>" wrapperCssClass="mb-0" />

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

<liferay-frontend:component
	module="{ConfigurationFormEventHandler} from cookies-banner-web"
/>

<aui:script>
	var logoSelectorContainer = document.getElementById(
		'<portlet:namespace />logoSelectorContainer'
	);
	var customRadioButton = document.getElementById('<portlet:namespace />custom');

	function toggleLogoSelector() {
		if (customRadioButton.checked) {
			logoSelectorContainer.classList.remove('d-none');
		}
		else {
			logoSelectorContainer.classList.add('d-none');
		}
	}

	toggleLogoSelector();

	var floatingIcons = document.querySelectorAll(
		'input[name="<portlet:namespace />floatingIcon"]'
	);

	floatingIcons.forEach(function (floatingIcon) {
		floatingIcon.addEventListener('change', toggleLogoSelector);
	});

	function toggleRenewalPeriodMaxAttribute(dissent, timeUnit) {
		var renewalPeriod = document.getElementById(
			'<portlet:namespace />consentRenewalPeriod'
		);

		if (dissent) {
			renewalPeriod = document.getElementById(
				'<portlet:namespace />dissentRenewalPeriod'
			);
		}

		if (timeUnit === 'days') {
			renewalPeriod.setAttribute('max', '365');
		}
		else if (timeUnit === 'months') {
			renewalPeriod.setAttribute('max', '12');
		}
		else {
			renewalPeriod.setAttribute('max', '52');
		}
	}

	toggleRenewalPeriodMaxAttribute(
		false,
		'<%= cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingConsentRenewalPeriodTimeUnit() %>'
	);

	toggleRenewalPeriodMaxAttribute(
		true,
		'<%= cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingDissentRenewalPeriodTimeUnit() %>'
	);

	var form = document.<portlet:namespace />fm;

	if (form) {
		var consentRenewalPeriodTimeUnit = document.getElementById(
			'<portlet:namespace />consentRenewalPeriodTimeUnit'
		);

		consentRenewalPeriodTimeUnit.addEventListener('change', (event) => {
			toggleRenewalPeriodMaxAttribute(false, event.target.value);
		});

		var dissentRenewalPeriodTimeUnit = document.getElementById(
			'<portlet:namespace />dissentRenewalPeriodTimeUnit'
		);

		dissentRenewalPeriodTimeUnit.addEventListener('change', (event) => {
			toggleRenewalPeriodMaxAttribute(true, event.target.value);
		});

		var forcedReconsentButton = document.getElementById(
			'<portlet:namespace />forcedReconsentButton'
		);

		var modifiedDate = document.getElementById(
			'<portlet:namespace />modifiedDate'
		);

		if (forcedReconsentButton && modifiedDate) {
			forcedReconsentButton.addEventListener('click', function (event) {
				Liferay.Util.openConfirmModal({
					message:
						'<liferay-ui:message key="you-are-about-to-force-reconsent" />',
					onConfirm: function (isConfirmed) {
						if (isConfirmed) {
							form.reset();

							modifiedDate.value = new Date().getTime();

							form.submit();
						}
					},
				});
			});
		}

		form.addEventListener('submit', (event) => {
			var consentRenewalPeriod = document.getElementById(
				'<portlet:namespace />consentRenewalPeriod'
			);

			if (!consentRenewalPeriod.value || isNaN(consentRenewalPeriod.value)) {
				event.preventDefault();
				event.stopImmediatePropagation();
				return;
			}

			if (modifiedDate) {
				modifiedDate.value = new Date().getTime();
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
					consentRenewalPeriodTimeUnit.value !==
						'<%= cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingConsentRenewalPeriodTimeUnit() %>' ||
					dissentRenewalPeriod.value !==
						'<%= cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingDissentRenewalPeriod() %>' ||
					dissentRenewalPeriodTimeUnit.value !==
						'<%= cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingDissentRenewalPeriodTimeUnit() %>') &&
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
							modifiedDate.value = new Date().getTime();

							form.submit();
						}
					},
				});
			}
		});
	}
</aui:script>