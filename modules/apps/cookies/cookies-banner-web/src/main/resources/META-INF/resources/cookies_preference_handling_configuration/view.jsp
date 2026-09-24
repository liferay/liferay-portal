<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CookiesPreferenceHandlingConfigurationDisplayContext cookiesPreferenceHandlingConfigurationDisplayContext = (CookiesPreferenceHandlingConfigurationDisplayContext)request.getAttribute(CookiesBannerWebKeys.COOKIES_PREFERENCE_HANDLING_CONFIGURATION_DISPLAY_CONTEXT);

String forceReconsentURL = cookiesPreferenceHandlingConfigurationDisplayContext.getForceReconsentURL(renderResponse);
%>

<aui:link hashedFile="<%= true %>" href="cookies-banner-web/cookies_preference_handling_configuration/css/main.css" rel="stylesheet" type="text/css" />

<fieldset>
	<legend class="sr-only"><liferay-ui:message key="consent-manager-configuration" /></legend>

	<div class="alert <%= cookiesPreferenceHandlingConfigurationDisplayContext.isCookiesPreferenceHandlingActive() ? "alert-success" : "alert-warning" %> d-flex align-items-center justify-content-between">
		<span>
			<liferay-ui:message key='<%= cookiesPreferenceHandlingConfigurationDisplayContext.isCookiesPreferenceHandlingActive() ? "this-experience-is-live-and-visible-to-site-visitors" : "this-experience-is-in-draft-mode-and-not-visible-to-site-visitors" %>' />
		</span>

		<button class="btn <%= cookiesPreferenceHandlingConfigurationDisplayContext.isCookiesPreferenceHandlingActive() ? "btn-secondary" : "btn-warning" %> <%= !cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingEnabled() ? "disabled" : "" %>" <%= !cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingEnabled() ? "disabled" : "" %> id="<portlet:namespace />toggleActiveButton" type="button">
			<liferay-ui:message key='<%= cookiesPreferenceHandlingConfigurationDisplayContext.isCookiesPreferenceHandlingActive() ? "deactivate" : "activate" %>' />
		</button>
	</div>

	<div class="c-mt-5 row">
		<div class="col-sm-12 form-group">
			<div class="form-group__inner">
				<clay:checkbox
					aria-describedby='<%= liferayPortletResponse.getNamespace() + "enabledHelp" %>'
					checked="<%= cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingEnabled() %>"
					id='<%= liferayPortletResponse.getNamespace() + "enabled" %>'
					label="enabled"
					name='<%= liferayPortletResponse.getNamespace() + "enabled" %>'
				/>

				<div class="form-feedback-group" id="<portlet:namespace />enabledHelp">
					<div class="form-text text-weight-normal"><liferay-ui:message key="cookie-enabled-help" /></div>
				</div>
			</div>
		</div>
	</div>

	<div class="row">
		<div class="col-sm-12 form-group">
			<div class="form-group__inner">
				<clay:checkbox
					aria-describedby='<%= liferayPortletResponse.getNamespace() + "explicitConsentModeHelp" %>'
					checked="<%= cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingExplicitConsentMode() %>"
					disabled="<%= !cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingEnabled() %>"
					id='<%= liferayPortletResponse.getNamespace() + "explicitConsentMode" %>'
					label="cookie-explicit-consent-mode"
					name='<%= liferayPortletResponse.getNamespace() + "explicitConsentMode" %>'
				/>

				<div class="form-feedback-group" id="<portlet:namespace />explicitConsentModeHelp">
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
					<input
						aria-describedby="<portlet:namespace />consentRenewalPeriodHelp"
						class="form-control"
						<%= !cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingEnabled() ? "disabled" : "" %>
						id="<portlet:namespace />consentRenewalPeriod"
						max="12"
						min="1"
						name="<portlet:namespace />consentRenewalPeriod"
						<%= cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingEnabled() ? "required" : "" %>
						type="number"
						value="<%= cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingConsentRenewalPeriod() %>"
					/>
				</div>

				<div class="form-group-item">

					<%
					String consentRenewalPeriodTimeUnit = cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingConsentRenewalPeriodTimeUnit();
					%>

					<select
						aria-label='<%= HtmlUtil.escape(LanguageUtil.get(request, "time-unit")) %>'
						class="form-control"
						<%= !cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingEnabled() ? "disabled" : "" %>
						id="<portlet:namespace />consentRenewalPeriodTimeUnit"
						name="<portlet:namespace />consentRenewalPeriodTimeUnit"
					>
						<option <%= Objects.equals("days", consentRenewalPeriodTimeUnit) ? "selected" : "" %> value="days"><liferay-ui:message key="days" /></option>
						<option <%= Objects.equals("weeks", consentRenewalPeriodTimeUnit) ? "selected" : "" %> value="weeks"><liferay-ui:message key="weeks" /></option>
						<option <%= Objects.equals("months", consentRenewalPeriodTimeUnit) ? "selected" : "" %> value="months"><liferay-ui:message key="months" /></option>
					</select>
				</div>
			</div>

			<div class="c-mb-1 form-feedback-group" id="<portlet:namespace />consentRenewalPeriodHelp">
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
					<input
						aria-describedby="<portlet:namespace />dissentRenewalPeriodHelp"
						class="form-control"
						<%= !cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingEnabled() ? "disabled" : "" %>
						id="<portlet:namespace />dissentRenewalPeriod"
						max="12"
						min="0"
						name="<portlet:namespace />dissentRenewalPeriod"
						<%= cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingEnabled() ? "required" : "" %>
						type="number"
						value="<%= cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingDissentRenewalPeriod() %>"
					/>
				</div>

				<div class="form-group-item">

					<%
					String dissentRenewalPeriodTimeUnit = cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingDissentRenewalPeriodTimeUnit();
					%>

					<select
						aria-label='<%= HtmlUtil.escape(LanguageUtil.get(request, "time-unit")) %>'
						class="form-control"
						<%= !cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingEnabled() ? "disabled" : "" %>
						id="<portlet:namespace />dissentRenewalPeriodTimeUnit"
						name="<portlet:namespace />dissentRenewalPeriodTimeUnit"
					>
						<option <%= Objects.equals("days", dissentRenewalPeriodTimeUnit) ? "selected" : "" %> value="days"><liferay-ui:message key="days" /></option>
						<option <%= Objects.equals("weeks", dissentRenewalPeriodTimeUnit) ? "selected" : "" %> value="weeks"><liferay-ui:message key="weeks" /></option>
						<option <%= Objects.equals("months", dissentRenewalPeriodTimeUnit) ? "selected" : "" %> value="months"><liferay-ui:message key="months" /></option>
					</select>
				</div>
			</div>

			<div class="c-mb-1 form-feedback-group" id="<portlet:namespace />dissentRenewalPeriodHelp">
				<div class="form-text text-weight-normal">
					<liferay-ui:message key="cookie-dissent-renewal-period-help" />
				</div>
			</div>
		</div>
	</div>

	<div class="row">
		<div class="col-sm-12 form-group">
			<button aria-describedby="<portlet:namespace />forcedReconsentHelp" class="btn btn-secondary <%= !cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingEnabled() ? "disabled" : "" %>" id="<portlet:namespace />forcedReconsentButton" type="button">
				<liferay-ui:message key="forced-reconsent" />
			</button>

			<div class="c-mb-1 form-feedback-group" id="<portlet:namespace />forcedReconsentHelp">
				<div class="form-text text-weight-normal">
					<liferay-ui:message key="forced-reconsent-help" />
				</div>
			</div>
		</div>
	</div>

	<aui:input name="active" type="hidden" value="<%= cookiesPreferenceHandlingConfigurationDisplayContext.isCookiesPreferenceHandlingActive() %>" />

	<aui:input name="modifiedDate" type="hidden" />

	<div class="row">
		<div class="col-sm-12 form-group">
			<div class="form-group__inner">
				<clay:checkbox
					aria-describedby='<%= liferayPortletResponse.getNamespace() + "storeConsentHelp" %>'
					checked="<%= cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingStoreConsent() %>"
					disabled="<%= !cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingEnabled() %>"
					id='<%= liferayPortletResponse.getNamespace() + "storeConsent" %>'
					label="cookie-store-consent"
					name='<%= liferayPortletResponse.getNamespace() + "storeConsent" %>'
				/>

				<div class="form-feedback-group" id="<portlet:namespace />storeConsentHelp">
					<div class="form-text text-weight-normal">
						<liferay-ui:message key="cookie-store-consent-help" />
					</div>
				</div>
			</div>
		</div>
	</div>
</fieldset>

<clay:row>
	<clay:col
		cssClass="form-group"
		sm="12"
	>
		<div class="form-group__inner">
			<clay:checkbox
				aria-describedby='<%= liferayPortletResponse.getNamespace() + "globalPrivacyControlEnabledHelp" %>'
				checked="<%= cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingGlobalPrivacyControlEnabled() %>"
				disabled="<%= !cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingEnabled() %>"
				id='<%= liferayPortletResponse.getNamespace() + "globalPrivacyControlEnabled" %>'
				label="global-privacy-control-enabled"
				name='<%= liferayPortletResponse.getNamespace() + "globalPrivacyControlEnabled" %>'
			/>

			<div class="form-feedback-group" id="<portlet:namespace />globalPrivacyControlEnabledHelp">
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
				aria-describedby='<%= liferayPortletResponse.getNamespace() + "floatingIconEnabledHelp" %>'
				checked="<%= cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingFloatingIconEnabled() %>"
				disabled="<%= !cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingEnabled() %>"
				id='<%= liferayPortletResponse.getNamespace() + "floatingIconEnabled" %>'
				label="floating-icon-enabled"
				name='<%= liferayPortletResponse.getNamespace() + "floatingIconEnabled" %>'
			/>

			<div class="form-feedback-group" id="<portlet:namespace />floatingIconEnabledHelp">
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
				currentLogoURL='<%= (customFloatingIconImageId == 0) ? themeDisplay.getPathThemeImages() + "/spacer.png" : themeDisplay.getPathImage() + "/floating_icon?img_id=" + customFloatingIconImageId + "&t=" + WebServerServletTokenUtil.getToken(customFloatingIconImageId) %>'
				defaultLogoURL='<%= themeDisplay.getPathThemeImages() + "/spacer.png" %>'
				label='<%= LanguageUtil.get(request, "custom-icon") %>'
				type="floating_icon"
			/>
		</div>

		<div class="form-feedback-group mt-2">
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

	toggleRenewalPeriodMaxAttribute(false, '<%= consentRenewalPeriodTimeUnit %>');

	toggleRenewalPeriodMaxAttribute(true, '<%= dissentRenewalPeriodTimeUnit %>');

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

		if (forcedReconsentButton) {
			forcedReconsentButton.addEventListener('click', function (event) {
				Liferay.Util.openConfirmModal({
					message:
						'<liferay-ui:message key='<%= cookiesPreferenceHandlingConfigurationDisplayContext.isCookiesPreferenceHandlingActive() ? "you-are-about-to-force-reconsent" : "you-are-about-to-change-the-consent-renewal-period-when-active" %>' />',
					onConfirm: function (isConfirmed) {
						if (isConfirmed) {
							Liferay.Util.fetch('<%= forceReconsentURL %>', {
								method: 'POST',
							}).then((response) => {
								if (response.ok) {
									var modifiedDateField = document.getElementById(
										'<portlet:namespace />modifiedDate'
									);

									if (modifiedDateField) {
										modifiedDateField.value =
											new Date().getTime();
									}

									form.dataset.skipActivationWarn = 'true';

									form.requestSubmit();
								}
								else {
									Liferay.Util.openToast({
										message: Liferay.Language.get(
											'your-request-failed-to-complete'
										),
										type: 'danger',
									});
								}
							});
						}
					},
				});
			});
		}

		var activeInput = document.getElementById('<portlet:namespace />active');
		var toggleActiveButton = document.getElementById(
			'<portlet:namespace />toggleActiveButton'
		);

		if (activeInput && toggleActiveButton) {
			toggleActiveButton.addEventListener('click', function (event) {
				var isCurrentlyActive = activeInput.value === 'true';

				var performToggle = function () {
					form.reset();

					activeInput.value = isCurrentlyActive ? 'false' : 'true';
					form.dataset.skipActivationWarn = 'true';

					form.requestSubmit();
				};

				if (isCurrentlyActive) {
					Liferay.Util.openConfirmModal({
						message:
							'<liferay-ui:message key="you-are-about-to-deactivate-the-consent-manager" />',
						onConfirm: function (isConfirmed) {
							if (isConfirmed) {
								performToggle();
							}
						},
					});
				}
				else {
					performToggle();
				}
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

			var dissentRenewalPeriod = document.getElementById(
				'<portlet:namespace />dissentRenewalPeriod'
			);

			if (!dissentRenewalPeriod.value || isNaN(dissentRenewalPeriod.value)) {
				event.preventDefault();
				event.stopImmediatePropagation();
				return;
			}

			if (form.dataset.skipActivationWarn === 'true') {
				return;
			}

			var enabled = document.getElementById('<portlet:namespace />enabled');

			var renewalPeriodChanged =
				(consentRenewalPeriod.value !==
					'<%= cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingConsentRenewalPeriod() %>' ||
					consentRenewalPeriodTimeUnit.value !==
						'<%= consentRenewalPeriodTimeUnit %>' ||
					dissentRenewalPeriod.value !==
						'<%= cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingDissentRenewalPeriod() %>' ||
					dissentRenewalPeriodTimeUnit.value !==
						'<%= dissentRenewalPeriodTimeUnit %>') &&
				enabled.checked &&
				<%= cookiesPreferenceHandlingConfigurationDisplayContext.getCookiesPreferenceHandlingEnabled() %>;

			if (renewalPeriodChanged) {
				form.dataset.renewalPeriodChanged = 'true';
			}
			else {
				delete form.dataset.renewalPeriodChanged;
			}

			if (
				activeInput &&
				activeInput.value !== 'true' &&
				renewalPeriodChanged
			) {
				event.preventDefault();
				event.stopImmediatePropagation();

				Liferay.Util.openConfirmModal({
					message:
						'<liferay-ui:message key="you-are-about-to-change-the-consent-renewal-period-when-active" />',
					onConfirm: function (isConfirmed) {
						if (isConfirmed) {
							Liferay.Util.fetch('<%= forceReconsentURL %>', {
								method: 'POST',
							}).then((response) => {
								if (response.ok) {
									form.dataset.skipActivationWarn = 'true';

									form.requestSubmit();
								}
								else {
									Liferay.Util.openToast({
										message: Liferay.Language.get(
											'your-request-failed-to-complete'
										),
										type: 'danger',
									});
								}
							});
						}
					},
				});
			}
		});
	}
</aui:script>