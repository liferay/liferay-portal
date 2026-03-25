/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {delegate} from 'frontend-js-web';

export default function ({namespace}) {
	const delegateHandler = delegate(
		document.body,
		'change',
		'input[type="checkbox"]',
		(event) => {
			const consentRenewalPeriod = document.querySelector(
				`input[type='number'][name='${namespace}consentRenewalPeriod']`
			);

			const consentRenewalPeriodLabel = document.querySelector(
				`label[for='${namespace}consentRenewalPeriod']`
			);

			const dissentRenewalPeriod = document.querySelector(
				`input[type='number'][name='${namespace}dissentRenewalPeriod']`
			);

			const dissentRenewalPeriodLabel = document.querySelector(
				`label[for='${namespace}dissentRenewalPeriod']`
			);

			const explicitConsentMode = document.querySelector(
				`input[type='checkbox'][name='${namespace}explicitConsentMode']`
			);

			const floatingIconEnabled = document.querySelector(
				`input[type='checkbox'][name='${namespace}floatingIconEnabled']`
			);

			const floatingIcons = document.querySelectorAll(
				`input[type='radio'][name='${namespace}floatingIcon']`
			);

			const logoSelectorContainer = document.getElementById(
				`${namespace}logoSelectorContainer`
			);

			const storeConsent = document.querySelector(
				`input[type='checkbox'][name='${namespace}storeConsent']`
			);

			if (event.delegateTarget.id === `${namespace}enabled`) {
				if (event.delegateTarget.checked) {
					consentRenewalPeriod.classList.remove('disabled');
					consentRenewalPeriod.removeAttribute('disabled');
					consentRenewalPeriod.required = true;
					consentRenewalPeriodLabel?.classList.remove('disabled');
					dissentRenewalPeriod.classList.remove('disabled');
					dissentRenewalPeriod.removeAttribute('disabled');
					dissentRenewalPeriod.required = true;
					dissentRenewalPeriodLabel?.classList.remove('disabled');
					explicitConsentMode.removeAttribute('disabled');

					if (Liferay.FeatureFlags['LPD-75027']) {
						floatingIconEnabled.removeAttribute('disabled');

						floatingIcons.forEach((iconInput) => {
							iconInput.removeAttribute('disabled');

							const label = document.querySelector(
								`label[for='${iconInput.id}']`
							);
							if (label) {
								label.classList.remove('disabled');
							}
						});

						if (logoSelectorContainer) {
							logoSelectorContainer.classList.remove('disabled');
							logoSelectorContainer
								.querySelectorAll('input, button')
								.forEach((element) => {
									element.removeAttribute('disabled');
								});
						}
					}

					if (Liferay.FeatureFlags['LPD-75032']) {
						storeConsent.removeAttribute('disabled');
					}
				}
				else {
					consentRenewalPeriod.classList.add('disabled');
					consentRenewalPeriod.required = false;
					consentRenewalPeriod.setAttribute('disabled', '');
					consentRenewalPeriodLabel?.classList.add('disabled');
					dissentRenewalPeriod.classList.add('disabled');
					dissentRenewalPeriod.required = false;
					dissentRenewalPeriod.setAttribute('disabled', '');
					dissentRenewalPeriodLabel?.classList.add('disabled');
					explicitConsentMode.setAttribute('disabled', '');

					if (Liferay.FeatureFlags['LPD-75027']) {
						floatingIconEnabled.setAttribute('disabled', '');

						floatingIcons.forEach((iconInput) => {
							iconInput.setAttribute('disabled', '');

							const label = document.querySelector(
								`label[for='${iconInput.id}']`
							);
							if (label) {
								label.classList.add('disabled');
							}
						});

						if (logoSelectorContainer) {
							logoSelectorContainer.classList.add('disabled');
							logoSelectorContainer
								.querySelectorAll('input, button')
								.forEach((element) => {
									element.setAttribute('disabled', '');
								});
						}
					}

					if (Liferay.FeatureFlags['LPD-75032']) {
						storeConsent.checked = false;
						storeConsent.setAttribute('disabled', '');
					}
				}
			}
		}
	);

	return {
		dispose() {
			delegateHandler.dispose();
		},
	};
}
